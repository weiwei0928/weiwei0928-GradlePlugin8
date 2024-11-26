package com.ww.gradle.tracklib

import android.os.SystemClock
import android.util.Log
import java.util.concurrent.atomic.AtomicInteger

/**
 * @Author weiwei
 * @Date 2024/11/23 18:33
 */
@IgnoreTrace
class SampleMethodHook : IMethodHookHandler {
    private var filter: LogFilter? = null

    fun setFilter(filter: LogFilter?) {
        this.filter = filter
    }

    @IgnoreTrace
    internal class InnerClass(var time: Long?) {
        var integer: AtomicInteger = AtomicInteger(1)
    }

    companion object {
        private val local = ThreadLocal<HashMap<String?, Any>>()
        private const val LINE =
            "══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════"
    }


    override fun onMethodEnter(className: String?, methodName: String?) {
        val name = className + methodName
        var map = local.get()
        if (map == null) {
            map = HashMap(16)
            local.set(map)
        }
        val data = map[name] as InnerClass?
        if (data?.time == null) {
            map[name] =
                InnerClass(SystemClock.elapsedRealtime())
            return
        }

        data.integer.incrementAndGet()
        return
    }

    override fun onMethodReturn(className: String?, methodName: String?) {
        val name = className + methodName
        val map: HashMap<String?, Any>? = local.get()
        var data: InnerClass? = null
        if (map != null) {
            data = map[name] as InnerClass?
        }
        if (data?.time == null) {
            Log.d("MethodHookHandler", "$name <-- not has data !")
            return
        }

        if (data.integer.decrementAndGet() <= 0) {
            map?.remove(name)
            if (map!!.size == 0) {
                local.remove()
            }
            val time = SystemClock.elapsedRealtime() - data.time!!
            val d = 30
            if (time <= d) {
                return
            }
            val msgBuilder = StringBuilder(16 * 10)
                .append(" ")
                .append("\n╔").append(LINE)
                .append("\n║ [Thread]:").append(Thread.currentThread().name)
                .append("\n║ [Class]:").append(className)
                .append("\n║ [Method]:").append(methodName)
                .append("\n║ [Time]:").append(time).append(" ms")
                .append("\n╚").append(LINE)
            val msg = msgBuilder.toString()
            val i = 100
            val w = 300
            val e = 500
            if (time <= i) {
                Log.d("MethodHookHandler", msg)
            } else if (time <= w) {
                Log.i("MethodHookHandler", msg)
            } else if (time <= e) {
                Log.w("MethodHookHandler", msg)
            } else {
                Log.e("MethodHookHandler", msg)
            }
        }
    }
}

