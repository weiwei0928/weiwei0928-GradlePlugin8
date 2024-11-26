package com.ww.gradle.gradleplugin8

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.text).setOnClickListener {
            Thread.sleep(1000)

        }
//        test()
    }

    private fun test() {
        println("test")
        TraceTest.test()
    }



}

