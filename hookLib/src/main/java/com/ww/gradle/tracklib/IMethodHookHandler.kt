package com.ww.gradle.tracklib

/**
 * @Author weiwei
 * @Date 2024/11/23 18:33
 */
interface IMethodHookHandler {
    //    void onMethodEnter(Object thisObj,
    //                       String className,
    //                       String methodName,
    //                       String argsType,
    //                       String returnType,
    //                       Object... args
    //    );
    //
    //
    //    void onMethodReturn(Object returnObj,
    //                        Object thisObj,
    //                        String className,
    //                        String methodName,
    //                        String argsType,
    //                        String returnType,
    //                        Object... args);

    fun onMethodEnter(
        className: String?,
        methodName: String?
    )


    fun onMethodReturn(
        className: String?,
        methodName: String?
    )
}

