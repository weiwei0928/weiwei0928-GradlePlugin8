package com.ww.gradle.plugin.util

import org.objectweb.asm.tree.ClassNode

object InjectUtils {

    //一些默认无需插桩的类
    private val UN_NEED_TRACE_CLASS = arrayOf(
        "/R.class",
        "/R$",
        "/Manifest.class",
        "/BuildConfig.class",
    )

    //默认无需插桩的包
    private val UN_NEED_TRACE_PACKAGE = arrayOf("kotlin/", "androidx/")


    private fun isNeedInjectClass(fileName: String): Boolean {
        var isNeed = true
        if (fileName.endsWith(".class")) {
            for (unTraceCls in UN_NEED_TRACE_CLASS) {
                if (fileName.contains(unTraceCls)) {
                    isNeed = false
                    break
                }
            }
            for (packageName in UN_NEED_TRACE_PACKAGE) {
                if (fileName.startsWith(packageName)) {
                    isNeed = false
                    break
                }
            }
        } else {
            isNeed = false
        }
        return isNeed
    }

    fun isNotInjectByPackageNameOrClassName(classNode: ClassNode): Boolean {
        return isNotInjectByPackageNameOrClassName("${classNode.name}.class")
    }

    /**
     * @param fileName
     * @return true:不插桩
     */
    fun isNotInjectByPackageNameOrClassName(fileName: String): Boolean {
        if (isNeedInjectClass(fileName)) {
            return false
        } else {
            println("------class文件不插桩：$fileName")
            return true
        }
    }

    /**
     * 用注解 com.ww.gradle.tracklib.IgnoreInject 注释的类，不插桩
     * @return true:不插桩
     */
    fun notInjectByAnnotation(classNode: ClassNode): Boolean {
        //处理注解
        val annotations = classNode.invisibleAnnotations //获取声明的所有注解
        if (annotations != null) { //遍历注解
            for (annotationNode in annotations) {
                //获取注解的描述信息
                if ("Lcom/ww/gradle/tracklib/IgnoreInject;" == annotationNode.desc) {
                    return true
                }
            }
        }
        return false
    }
}