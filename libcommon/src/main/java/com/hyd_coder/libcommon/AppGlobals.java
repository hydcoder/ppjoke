package com.hyd_coder.libcommon;

import android.annotation.SuppressLint;
import android.app.Application;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Cerated by huangyingde
 * Create date : 2020/6/2 12:40
 * description : 提供全局的Application对象实例
 */
public class AppGlobals {

    private static Application sApplication;

    public static Application getApplication() {
        if (sApplication == null) {
            try {
                @SuppressLint({"DiscouragedPrivateApi", "PrivateApi"})
                Method method = Class.forName("android.app.ActivityThread").getDeclaredMethod("currentApplication");
                sApplication = (Application) method.invoke(null, null);
            } catch (NoSuchMethodException | ClassNotFoundException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return sApplication;
    }
}
