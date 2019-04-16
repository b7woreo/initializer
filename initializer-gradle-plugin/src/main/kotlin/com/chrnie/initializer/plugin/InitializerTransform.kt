package com.chrnie.initializer.plugin

import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import org.gradle.api.Project
import java.io.File
import java.io.FileInputStream
import java.util.jar.JarFile

class InitializerTransform(private val project: Project) : Transform() {

    private val logger = project.logger

    override fun getName(): String = InitializerTransform::class.simpleName!!

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> =
        TransformManager.CONTENT_CLASS

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> =
        TransformManager.SCOPE_FULL_PROJECT

    override fun isIncremental(): Boolean = false

    override fun transform(invocation: TransformInvocation) {
        logger.quiet("Initializer register in project: ${project.name}")

        val inputs = invocation.inputs
        val outputProvider = invocation.outputProvider

        val classList = inputs.asSequence()
            .flatMap { input ->
                sequenceOf(
                    input.jarInputs
                        .asSequence()
                        .map { jarInput ->
                            outputProvider.getContentLocation(
                                jarInput.name,
                                jarInput.contentTypes,
                                jarInput.scopes,
                                Format.JAR
                            ).also { jarInput.file.copyTo(it, true) }
                        }.flatMap { findClassInJarFile(it) },
                    input.directoryInputs
                        .asSequence()
                        .map { directoryInput ->
                            outputProvider.getContentLocation(
                                directoryInput.name,
                                directoryInput.contentTypes,
                                directoryInput.scopes,
                                Format.DIRECTORY
                            ).also { directoryInput.file.copyRecursively(it, true) }
                        }.flatMap { findClassInDirectory(it) }
                ).flatten()
            }
            .toList()

        val taskNameList = findAllTaskName(classList)
        logger.quiet("Found task class: $taskNameList")

        classList.firstOrNull { HOOK_CLASS_NAME == it.name }
            ?.let { CodeGenerator(it.outputFile, taskNameList) }
            ?.generate()
            ?: throw RuntimeException("can not found $HOOK_CLASS_FILE_NAME")
    }

    private fun findClassInJarFile(file: File): Sequence<ClassMetadata> {
        return JarFile(file).use { jarFile ->
            jarFile.entries()
                .toList()
                .filter { !it.isDirectory and it.name.endsWith(".class") }
                .map { classEntry ->
                    jarFile.getInputStream(classEntry).use { ClassMetadata(it, file) }
                }
        }.asSequence()
    }

    private fun findClassInDirectory(file: File): Sequence<ClassMetadata> {
        return file.walkTopDown()
            .asSequence()
            .filter { it.isFile and it.name.endsWith(".class") }
            .map { classFile ->
                FileInputStream(classFile).use { ClassMetadata(it, file) }
            }
    }

    private fun findAllTaskName(list: List<ClassMetadata>): List<String> {
        fun traversTaskMetadata(
            superName: String,
            superNameToMetadata: Map<String, List<ClassMetadata>>
        ): Sequence<ClassMetadata> {
            return superNameToMetadata[superName]
                ?.asSequence()
                ?.flatMap { traversTaskMetadata(it.name, superNameToMetadata) + it }
                ?: emptySequence()
        }

        fun ensureTaskClassValid(classMetadata: ClassMetadata) {
            if (!classMetadata.isPublic) {
                throw RuntimeException("task class is not public access: ${classMetadata.name}")
            }

            if (classMetadata.isAbstract) {
                throw RuntimeException("task class is abstract: ${classMetadata.name}")
            }

            classMetadata.methods.firstOrNull { methodMetadata ->
                val constructor = methodMetadata.name == "<init>"
                val noneParam = methodMetadata.descriptor == "()V"
                val public = methodMetadata.isPublic
                constructor and noneParam and public
            }
                ?: throw RuntimeException("task class has not public empty constructor: ${classMetadata.name}")
        }

        return list.asSequence()
            .filter { it.superName != null }
            .groupBy { it.superName!! }
            .let { traversTaskMetadata(TASK_CLASS_SUPER_NAME, it) }
            .filter { !it.isAbstract }
            .map { it.apply { ensureTaskClassValid(this) } }
            .map { it.name }
            .toList()
    }
}
