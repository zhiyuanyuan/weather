package com.zhiyuan.weather.base;

import android.app.Application;
import android.content.Context;

import com.github.moduth.blockcanary.BlockCanary;
import com.squareup.leakcanary.LeakCanary;
import com.zhiyuan.weather.BuildConfig;
import com.zhiyuan.weather.util.CrashHandler;
import com.zhiyuan.weather.util.PLog;

import io.reactivex.plugins.RxJavaPlugins;

/**
 * Created by admin on 2017/7/20.
 */

public class BaseApplication extends Application {

    private static String sCacheDir;
    private static Context sAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sAppContext = getApplicationContext();
        CrashHandler.init(new CrashHandler(getApplicationContext()));
        if (!BuildConfig.DEBUG) {
        //    FIR.init(this);//// TODO: 2017/7/20  崩溃收集
        } else {

        //    Stetho.initializeWithDefaults(this);
        }
        BlockCanary.install(this, new AppBlockCanaryContext()).start();
        LeakCanary.install(this);
        RxJavaPlugins.setErrorHandler(throwable -> {
            if (throwable != null) {
                PLog.e(throwable.toString());
            } else {
                PLog.e("call onError but exception is null");
            }
        });
         /*
         * 如果存在SD卡则将缓存写入SD卡,否则写入手机内存
         */
        if (getApplicationContext().getExternalCacheDir() != null && ExistSDCard()) {
            sCacheDir = getApplicationContext().getExternalCacheDir().toString();
        } else {
            sCacheDir = getApplicationContext().getCacheDir().toString();
        }
    }
    private boolean ExistSDCard() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    public static String getAppCacheDir() {
        return sCacheDir;
    }
    public static Context getAppContext() {
        return sAppContext;
    }
}
