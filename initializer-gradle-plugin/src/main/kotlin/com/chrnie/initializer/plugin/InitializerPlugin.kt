package com.chrnie.initializer.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class InitializerPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        addInitializerDependencyInProject(project)
        registerTransformIfAppProject(project)
    }

    private fun addInitializerDependencyInProject(project: Project) {
        fun getInitializerPluginVersion(): String {
            return project.rootProject
                .buildscript
                .configurations
                .getByName("classpath")
                .dependencies
                .firstOrNull { (it.group == POM_GROUP_ID) and (it.name == POM_PLUGIN_ARTIFACT_ID) }
                ?.let { it.version }
                ?: throw RuntimeException("can not found initializer gradle plugin in classpath")
        }

        project.dependencies.add(
            "implementation",
            "$POM_GROUP_ID:$POM_RUNTIME_ARTIFACT_ID:${getInitializerPluginVersion()}"
        )
    }

    private fun registerTransformIfAppProject(project: Project) {
        val app = project.plugins.hasPlugin(AppPlugin::class.java)
        if (app) {
            val android = project.extensions.getByType(AppExtension::class.java)
            android.registerTransform(InitializerTransform(project))
        }
    }
}