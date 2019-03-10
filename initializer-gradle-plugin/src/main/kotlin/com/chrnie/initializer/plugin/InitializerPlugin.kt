package com.chrnie.initializer.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class InitializerPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val app = project.plugins.hasPlugin(AppPlugin::class.java)
        if (!app) {
            return
        }

        val logger = project.logger
        logger.quiet("[Initializer]: register in module: ${project.name}")

        val android = project.extensions.getByType(AppExtension::class.java)
        android.registerTransform(InitializerTransform(logger))
    }

}