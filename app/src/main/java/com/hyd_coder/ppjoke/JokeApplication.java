package com.hyd_coder.ppjoke;

import android.app.Application;

import com.hyd_coder.libnetwork.ApiService;

/**
 * Cerated by huangyingde
 * Create date : 2020/6/10 17:10
 * description : JokeApplication
 */
public class JokeApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ApiService.init("http://123.56.232.18:8080/serverdemo", null);
    }
}
