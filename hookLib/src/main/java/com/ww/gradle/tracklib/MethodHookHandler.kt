package com.ww.gradle.tracklib


@IgnoreInject
object MethodHookHandler {
    /**
     * 如果设置了impl参数，则此对象实现将被编译期替换
     */
    private val M_PRINT: IMethodHookHandler = SampleMethodHook()

    @JvmStatic
    fun enter(className: String?, methodName: String?) {
        return M_PRINT.onMethodEnter(className, methodName)
    }

    @JvmStatic
    fun exit(className: String?, methodName: String?) {
        M_PRINT.onMethodReturn(className, methodName)
    }
}