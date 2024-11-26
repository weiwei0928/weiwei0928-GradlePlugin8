package com.ww.gradle.plugin.util

import com.ww.gradle.plugin.MethodHookConfig
import com.ww.gradle.plugin.MethodHookClassVisitor
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile

object ClassHandler {

    fun handleClassInDirectory(classLoader_: ClassLoader, inFile: File, config: MethodHookConfig) {
        val inputStream: InputStream = FileInputStream(inFile)
        val classReader = ClassReader(inputStream)
        val classNode = ClassNode()
        classReader.accept(classNode, 0)
        if (NotTrackUtils.isNotTrackByAnnotation(classNode) || NotTrackUtils.isNotTrackByConfig(
                classNode
            )
        ) {
            inputStream.close()
            return
        }
        val classWriter = object : ClassWriter(classReader, COMPUTE_FRAMES) {
            override fun getClassLoader(): ClassLoader {
                return classLoader_
            }
        }
        try {
            val classVisitor = MethodHookClassVisitor(Opcodes.ASM7, classWriter, config)
            classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES)
            //覆盖原来的class文件
            val code = classWriter.toByteArray()
            val fos =
                FileOutputStream(inFile.parentFile.absolutePath + File.separator + inFile.name)
            fos.write(code)
            fos.close()
        } catch (e: Throwable) {
            println("---error---插桩失败：inFile = $inFile")
            e.printStackTrace()
        } finally {
            inputStream.close()
        }
    }

    fun handleClassInJar(
        classLoader_: ClassLoader,
        jarFile: JarFile,
        jarEntry: JarEntry,
        config: MethodHookConfig
    ): ByteArray {

        val inputStream = jarFile.getInputStream(jarEntry)
        val entryName = jarEntry.name
        val byteArray = inputStream.readBytes()
        inputStream.close()

        val classReader = ClassReader(byteArray)
        val classNode = ClassNode() //创建ClassNode,读取的信息会封装到这个类里面
        classReader.accept(classNode, 0) //开始读取

        when {
            NotTrackUtils.isNotTrackByConfig(entryName) -> {
                return byteArray
            }

            NotTrackUtils.isNotTrackByAnnotation(classNode) -> {
                return byteArray
            }

            else -> {
                val classWriter = object : ClassWriter(classReader, COMPUTE_FRAMES) {
                    override fun getClassLoader(): ClassLoader {
                        return classLoader_
                    }
                }
                try {
                    val cv: ClassVisitor = MethodHookClassVisitor(Opcodes.ASM7, classWriter, config)
                    classReader.accept(cv, ClassReader.EXPAND_FRAMES)
                    val code = classWriter.toByteArray()
                    return code
                } catch (e: Throwable) {
                    println("---error---插桩失败：entryName = $entryName")
                    e.printStackTrace()
                    return byteArray
                }
            }
        }
    }

}