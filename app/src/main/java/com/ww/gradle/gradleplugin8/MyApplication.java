package com.ww.gradle.gradleplugin8;

import android.app.Application;

//import com.ww.gradle.tracklib.MethodHookHandler;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
//        MethodHookHandler.enter("MyApplication", "onCreate","","");
        super.onCreate();
//        MethodHookHandler.exit("MyApplication", "onCreate","","");
    }

}
