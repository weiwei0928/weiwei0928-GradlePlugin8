package com.ww.gradle.plugin

import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ScopedArtifacts
import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.ww.gradle.plugin.task.AsmTraceTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class AsmTrackPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create("methodHookConfig", MethodHookConfig::class.java)

        val isApp = project.plugins.hasPlugin(AppPlugin::class.java)

        if (isApp) {
            val android = project.extensions.getByType(AppExtension::class.java)
            val config = project.extensions.getByName("methodHookConfig") as MethodHookConfig

            val androidComponents =
                project.extensions.getByType(AndroidComponentsExtension::class.java)
            val scope = if (config.scopeAll) {
                ScopedArtifacts.Scope.ALL
            } else {
                ScopedArtifacts.Scope.PROJECT
            }

            androidComponents.onVariants { variant ->
                val taskProvider = project.tasks.register(//注册AsmTraceTask任务
                    "${variant.name}AsmTraceTask", AsmTraceTask::class.java
                )

                variant.artifacts.forScope(scope) //扫描所有class
                    .use(taskProvider)
                    .toTransform(
                        type = ScopedArtifact.CLASSES,
                        inputJars = AsmTraceTask::allJars,
                        inputDirectories = AsmTraceTask::allDirectories,
                        into = AsmTraceTask::output
                    )
            }
        }
    }
}
