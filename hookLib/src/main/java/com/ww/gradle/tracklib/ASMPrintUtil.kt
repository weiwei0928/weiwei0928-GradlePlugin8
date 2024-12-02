package com.ww.gradle.tracklib

import org.objectweb.asm.ClassReader
import org.objectweb.asm.util.ASMifier
import org.objectweb.asm.util.Printer
import org.objectweb.asm.util.Textifier
import org.objectweb.asm.util.TraceClassVisitor
import java.io.PrintWriter;

/**
 * @Author weiwei
 * @Date 2024/12/1 20:07
 */
object ASMPrintUtil {

    @JvmStatic
    fun main(args: Array<String>) {
        // (1) 设置参数
//        val className = "com.ww.gradle.plugin.util.Test"
        val className = ASMPrintTest::class.java.name
        printClass(className)
    }

    fun printClass(className: String) {
        val parsingOptions: Int = ClassReader.SKIP_FRAMES or ClassReader.SKIP_DEBUG
        val asmCode = true

        // (2) 打印结果
        val printer: Printer = if (asmCode) ASMifier() else Textifier()
        val printWriter: PrintWriter = PrintWriter(System.out, true)
        val traceClassVisitor: TraceClassVisitor = TraceClassVisitor(null, printer, printWriter)
        ClassReader(className).accept(traceClassVisitor, parsingOptions)
    }


}