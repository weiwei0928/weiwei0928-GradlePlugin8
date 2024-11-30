package com.ww.gradle.plugin

/**
 * @Author weiwei
 * @Date 2024/11/25 15:10
 */
open class MethodHookConfig {
    /**
     * transform 范围
     * true 项目范围，不包括导入的项目或外部依赖项。
     * false 完整范围，包括项目范围、导入的项目和所有外部依赖关系。
     */
    var scopeAll: Boolean = false

    /**
     * 是否启用代码插桩
     */
    var enableHook: Boolean = false

    /**
     * 是否将所有的方法都统计，否则只统计注解和正则设置的
     */
    var hookAll: Boolean = false

    /**
     * 是否打印日志
     */
    var printLog: Boolean = true

    /**
     * 是否保存mapping
     */
    var saveMapping: Boolean = true

    /**
     * jar正则表达式,不设置默认不插桩jar包
     */
    var jarRegexList: List<String>? = null

    /**
     * 类名正则表达式 匹配规则
     */
    var classRegexList: List<String>? = null

    /**
     * 方法正则表达式 匹配规则
     */
    var methodRegexList: List<String>? = null

    /**
     * 是否用插桩后的jar包替换项目中的jar包
     */
    var replaceJar: Boolean = false

    var impl: String = ""


}

