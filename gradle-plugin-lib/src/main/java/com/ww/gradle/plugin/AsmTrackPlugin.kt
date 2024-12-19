package com.ww.gradle.plugin

import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ScopedArtifacts
import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.ww.gradle.plugin.task.AsmInjectTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class AsmTrackPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create("methodHookConfig", MethodHookConfig::class.java)
        val isApp = project.plugins.hasPlugin(AppPlugin::class.java)

        if (isApp) {
            val android = project.extensions.getByType(AppExtension::class.java)

            val androidComponents =
                project.extensions.getByType(AndroidComponentsExtension::class.java)

            androidComponents.onVariants { variant ->
                if (variant.buildType == "release") {
                    return@onVariants
                }
                println("name : ${variant.name} buildType :   ${variant.buildType}")
                val taskProvider = project.tasks.register(//注册AsmInjectTask任务
                    "${variant.name}AsmInjectTask", AsmInjectTask::class.java
                )
                val config = project.extensions.getByName("methodHookConfig") as MethodHookConfig
                println("config.enableHook === : ${config.enableHook} config.scopeAll === : ${config.scopeAll}")

                if (config.enableHook) {
                    val scope = if (config.scopeAll) {
                        ScopedArtifacts.Scope.ALL
                    } else {
                        ScopedArtifacts.Scope.PROJECT
                    }
                    variant.artifacts.forScope(scope) //扫描所有class
                        .use(taskProvider)
                        .toTransform(
                            type = ScopedArtifact.CLASSES,
                            inputJars = AsmInjectTask::allJars,
                            inputDirectories = AsmInjectTask::allDirectories,
                            into = AsmInjectTask::output
                        )
                }
            }
        }
    }
}
