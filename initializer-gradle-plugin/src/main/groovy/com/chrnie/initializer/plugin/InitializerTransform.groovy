package com.chrnie.initializer.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import groovy.io.FileType

class InitializerTransform extends Transform {

    @Override
    String getName() {
        return InitializerTransform.class.name
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation invocation) throws TransformException, InterruptedException, IOException {
        def inputs = invocation.inputs
        def outputProvider = invocation.outputProvider


        def taskClassNameList = []
        def hookJarFile = null

        inputs.each { input ->

            input.jarInputs.each { jarInput ->
                def jarFile = jarInput.file
                def destFile = outputProvider.getContentLocation(
                        jarInput.name,
                        jarInput.contentTypes,
                        jarInput.scopes,
                        Format.JAR
                )
                FileUtils.copyFile(jarFile, destFile)

                ClassScanner.scanJar(destFile) { className, superName, interfaces ->
                    if (Const.SCAN_CLASS_SUPER_NAME == superName) {
                        taskClassNameList.add(className)
                        return
                    }

                    if (className == Const.HOOK_CLASS_NAME) {
                        hookJarFile = destFile
                        return
                    }
                }
            }

            input.directoryInputs.each { directoryInput ->
                def directoryFile = directoryInput.file
                def destFile = outputProvider.getContentLocation(
                        directoryInput.name,
                        directoryInput.contentTypes,
                        directoryInput.scopes,
                        Format.DIRECTORY
                )
                FileUtils.copyDirectory(directoryFile, destFile)

                destFile.eachFileRecurse(FileType.FILES) { classFile ->
                    ClassScanner.scanClass(classFile) { className, superName, interfaces ->
                        if (Const.SCAN_CLASS_SUPER_NAME == superName) {
                            taskClassNameList.add(className)
                        }
                    }
                }
            }

        }

        if (hookJarFile != null) {
            def generator = new MethodCodeGenerator(hookJarFile, taskClassNameList)
            generator.generate()
        }
    }
}