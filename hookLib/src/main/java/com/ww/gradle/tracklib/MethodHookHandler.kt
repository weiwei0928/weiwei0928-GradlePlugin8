package com.ww.gradle.tracklib


@IgnoreInject
object MethodHookHandler {
    /**
     * 如果设置了impl参数，则此对象实现将被编译期替换
     */
    private val M_PRINT: IMethodHookHandler = SampleMethodHook()

    @JvmStatic
    fun enter(className: String?, methodName: String?, argsType: String?, returnType: String?) {
        return M_PRINT.onMethodEnter(className, methodName, argsType,returnType)
    }

    @JvmStatic
    fun exit(className: String?, methodName: String?, argsType: String?, returnType: String?) {
        M_PRINT.onMethodReturn(className, methodName, argsType, returnType)
    }
}