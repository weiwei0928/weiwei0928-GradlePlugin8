package com.ww.gradle.tracklib

/**
 * @Author weiwei
 * @Date 2024/11/23 18:35
 */
interface LogFilter {
    /**
     * 日志过滤器
     * @param thread
     * @param className
     * @param methodName
     * @param o
     * @param ages
     * @return false 允许打印，true 不允许打印
     */
    fun onInvoke(
        thread: Thread?,
        className: String?,
        methodName: String?,
        o: Any?,
        ages: Array<out Any>
    ): Boolean
}

