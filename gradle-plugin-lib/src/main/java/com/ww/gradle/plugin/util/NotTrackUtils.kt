package com.ww.gradle.plugin.util

import org.objectweb.asm.tree.ClassNode

object NotTrackUtils {

    //一些默认无需插桩的类
    private val UN_NEED_TRACE_CLASS = arrayOf(
        "/R.class",
        "/R$",
        "/Manifest.class",
        "/BuildConfig.class",
        "androidx/core/os/TraceCompat.class",
        "androidx/core/os/TraceCompat\$Api18Impl.class",
        "androidx/core/os/TraceCompat\$Api29Impl.class",
        "androidx/tracing/Trace.class",
        "androidx/tracing/TraceApi18Impl.class",
        "androidx/tracing/TraceApi29Impl.class",
    )

    /**
     * 不需要插桩的包
     * 配置格式：例：package = kotlin/jvm/internal/
     */
    private val UN_NEED_TRACE_PACKAGE = arrayOf("kotlin/")


    /**
     * @param fileName 格式：
     * 例：fileName = kotlin/jvm/internal/Intrinsics.class
     */
    private fun isNeedTraceClass(fileName: String): Boolean {
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
    fun isNotTrackByConfig(classNode: ClassNode): Boolean {
        return isNotTrackByConfig("${classNode.name}.class")
    }

    /**
     * @param fileName 格式：
     * 例：fileName = kotlin/jvm/internal/Intrinsics.class
     * @return true:不插桩
     */
    fun isNotTrackByConfig(fileName: String): Boolean {
        return if (isNeedTraceClass(fileName)) {
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
    fun isNotTrackByAnnotation(classNode: ClassNode): Boolean {
        //处理注解
        val annotations = classNode.invisibleAnnotations //获取声明的所有注解
        if (annotations != null) { //遍历注解
            for (annotationNode in annotations) {
                //获取注解的描述信息
                if ("Lcom/ww/gradle/tracklib/IgnoreTrace;" == annotationNode.desc) {
                    return true
                }
            }
        }
        return false
    }


}