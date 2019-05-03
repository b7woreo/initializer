package com.chrnie.initializer.plugin

const val POM_GROUP_ID = "com.chrnie"
const val POM_PLUGIN_ARTIFACT_ID = "initializer-gradle-plugin"
const val POM_RUNTIME_ARTIFACT_ID = "initializer"
const val TASK_CLASS_SUPER_NAME = "com/chrnie/initializer/Task"
const val HOOK_CLASS_NAME = "com/chrnie/initializer/TaskLoader"
const val HOOK_CLASS_FILE_NAME = "$HOOK_CLASS_NAME.class"
const val HOOK_METHOD_NAME = "loadTasks"