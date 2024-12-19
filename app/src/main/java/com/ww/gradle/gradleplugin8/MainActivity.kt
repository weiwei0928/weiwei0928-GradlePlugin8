package com.ww.gradle.gradleplugin8

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        val test = Test()
//        test.test1()
        findViewById<View>(R.id.text).setOnClickListener {
            Thread.sleep(1000)
//            test.test2()
            test()
        }
        test()
    }

    private fun test() {
        println("test")
        InjectTest.test()
    }


}

