package com.ww.gradle.tracklib

/**
 * @Author weiwei
 * @Date 2024/11/23 18:33
 */
interface IMethodHookHandler {

    fun onMethodEnter(
        className: String?,
        methodName: String?,
        argsType: String?,
        returnType: String?
    )


    fun onMethodReturn(
        className: String?,
        methodName: String?,
        argsType: String?,
        returnType: String?
    )
}

