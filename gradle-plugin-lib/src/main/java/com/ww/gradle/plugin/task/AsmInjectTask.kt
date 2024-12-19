package com.ww.gradle.plugin.task

import com.ww.gradle.plugin.MethodHookConfig
import com.ww.gradle.plugin.util.ClassHandler
import com.ww.gradle.plugin.InjectClassLoader
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream


abstract class AsmInjectTask : DefaultTask() {

    //所有的jar文件输入信息
    @get:InputFiles
    abstract val allJars: ListProperty<RegularFile>

    //所有的class文件输入信息
    @get:InputFiles
    abstract val allDirectories: ListProperty<Directory>

    //经过插桩修改后的输出信息
    @get:OutputFile
    abstract val output: RegularFileProperty

    @TaskAction
    fun taskAction() {

        val classLoader = createClassLoader(project, allDirectories, allJars)
        val config = project.extensions.getByName("methodHookConfig") as MethodHookConfig
        //输出到output的流
        val jarOutput = JarOutputStream(
            BufferedOutputStream(FileOutputStream(output.get().asFile))
        )
        //开始hook代码
        println("┌-----------------------------┐")
        println("|      Method Hook Begin      |")
        println("└-----------------------------┘")
        //遍历扫描class
        allDirectories.get().forEach { directory ->
            directory.asFile.walk().forEach { file ->
                if (file.isFile) {
                    if (file.absolutePath.endsWith(".class")) {
                        ClassHandler.handleClassInDirectory(classLoader, file, config)
                    }
                    val relativePath = directory.asFile.toURI().relativize(file.toURI()).path
                    jarOutput.putNextEntry(JarEntry(relativePath.replace(File.separatorChar, '/')))
                    jarOutput.write(file.inputStream().readBytes())
                    jarOutput.closeEntry()
                }
            }
        }

        //遍历扫描jar
        allJars.get().forEach { jarInputFile ->
            val jarFile = JarFile(jarInputFile.asFile)
            jarFile.entries().iterator().forEach { jarEntry ->
                //过滤掉非class文件，并去除重复无效的META-INF文件
                if (jarEntry.name.endsWith(".class") && !jarEntry.name.contains("META-INF") && !jarEntry.name.equals("module-info.class")) {
                    val outByteArray =
                        ClassHandler.handleClassInJar(classLoader, jarFile, jarEntry, config)
                    jarOutput.putNextEntry(JarEntry(jarEntry.name))
                    jarOutput.write(outByteArray)
                    jarOutput.closeEntry()
                } else {
//                    jarOutput.putNextEntry(JarEntry(jarEntry.name))
//                    jarOutput.write(jarFile.getInputStream(jarEntry).readBytes())
//                    jarOutput.closeEntry()
                }
            }
            jarFile.close()
        }
        jarOutput.close()
        println("┌--------------------------┐")
        println("|      Method Hook End     |")
        println("└--------------------------┘")
    }


    private fun createClassLoader(
        project: Project,
        allDirectories: ListProperty<Directory>,
        allJars: ListProperty<RegularFile>
    ): ClassLoader {
        val inputFiles = mutableListOf<File>()
        //1.所有类文件夹
        allDirectories.get().forEach { directoryInput ->
            inputFiles.add(directoryInput.asFile)
        }
        //2.所有jar
        allJars.get().forEach { jarInput ->
            inputFiles.add(jarInput.asFile)
        }
        return InjectClassLoader.getClassLoader(project, inputFiles)
    }


}