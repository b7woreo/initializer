package com.chrnie.initializer.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class InitializerPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        boolean app = project.plugins.hasPlugin(AppPlugin)
        if (!app) {
            return
        }

        def android = project.extensions.getByType(AppExtension)
        android.registerTransform(new InitializerTransform())
    }
}