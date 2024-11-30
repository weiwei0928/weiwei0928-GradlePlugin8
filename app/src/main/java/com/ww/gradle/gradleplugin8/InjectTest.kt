package com.ww.gradle.gradleplugin8

import android.os.Trace
import com.ww.gradle.tracklib.IgnoreInject
import kotlin.math.sqrt

/**
 * @Author weiwei
 * @Date 2024/11/24 20:51
 */
@IgnoreInject
object InjectTest {


    fun test() {
        val name = Trace.beginSection("开始处理")
        for (i in 0 until 100000) {
            sqrt(i.toDouble())
        }

        Thread.sleep(2000)
        Trace.endSection()
    }
}