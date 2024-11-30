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

    /**
     * 不需要插桩的包
     * 配置格式：例：package = kotlin/jvm/internal/
     */
    private val UN_NEED_TRACE_PACKAGE = arrayOf("kotlin/","androidx/")


    /**
     * @param fileName 格式：
     * 例：fileName = kotlin/jvm/internal/Intrinsics.class
     */
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

    /**
     * @return true:不插桩
     */
    fun isNotInjectByConfig(classNode: ClassNode): Boolean {
        return isNotInjectByConfig("${classNode.name}.class")
    }

    /**
     * @param fileName 格式：
     * 例：fileName = kotlin/jvm/internal/Intrinsics.class
     * @return true:不插桩
     */
    fun isNotInjectByConfig(fileName: String): Boolean {
        return if (isNeedInjectClass(fileName)) {
            false
        } else {
            println("------class文件不插桩：$fileName")
            true
        }
    }

    /**
     * 用注解 com.ww.gradle.tracklib.NotTrack 注释的类，不插桩
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
//    fun isNotTrackReg(classNode: ClassNode): Boolean {
//
//    }

}