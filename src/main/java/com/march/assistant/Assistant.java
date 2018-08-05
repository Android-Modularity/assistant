package com.march.assistant;

import android.annotation.TargetApi;
import android.app.Application;
import android.os.Build;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.march.assistant.common.AssistantActivityLifeCallback;
import com.march.assistant.common.StorageInfoManager;
import com.march.assistant.funcs.console.ConsoleModel;
import com.march.assistant.funcs.net.CharlesInterceptor;
import com.squareup.leakcanary.AndroidExcludedRefs;
import com.squareup.leakcanary.DisplayLeakService;
import com.squareup.leakcanary.ExcludedRefs;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import okhttp3.OkHttpClient;

/**
 * CreateAt : 2018/6/11
 * Describe :
 *
 * @author chendong
 */
public class Assistant {

    private DataSource         mDataSource;
    private RefWatcher         mRefWatcher;
    private StorageInfoManager mStorageInfoManager;
    private InitConfig mInitCfg;
    private static Assistant sInst;

    public static Assistant getInst() {
        if (sInst == null) {
            synchronized (Assistant.class) {
                if (sInst == null) {
                    sInst = new Assistant();
                }
            }
        }
        return sInst;
    }

    private Assistant() {
        mDataSource = new DataSource();
        mStorageInfoManager = new StorageInfoManager();
        mStorageInfoManager.backUp();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static void init(Application app, InitConfig cfg) {
        Assistant inst = getInst();
        inst.mInitCfg = cfg;
        Stetho.initializeWithDefaults(app);
        inst.initLeakCanary(app);
        handleLog("debug", "日志采集初始化完毕");

        app.registerActivityLifecycleCallbacks(new AssistantActivityLifeCallback());
    }

    private void initLeakCanary(Application app) {
        if (LeakCanary.isInAnalyzerProcess(app)) {
            return;
        }
        ExcludedRefs excludedRefs = AndroidExcludedRefs.createAppDefaults()
                .instanceField("android.view.inputmethod.InputMethodManager", "sInstance")
                .instanceField("android.view.inputmethod.InputMethodManager", "mLastSrvView")
                .instanceField("com.android.internal.policy.PhoneWindow$DecorView", "mContext")
                .instanceField("android.support.v7.widget.SearchView$SearchAutoComplete", "mContext")
                .build();
//
        mRefWatcher = LeakCanary.refWatcher(app)
                .listenerServiceClass(DisplayLeakService.class)
                .excludedRefs(excludedRefs)
                .buildAndInstall();

    }

    public static void initOkHttp(OkHttpClient.Builder builder) {
        builder.addNetworkInterceptor(new StethoInterceptor());
        builder.addInterceptor(new CharlesInterceptor());
    }

    public static void handleLog(String tag, String logMsg) {
        getInst().mDataSource.storeLog(new ConsoleModel(tag, logMsg));
    }

    public static void handleLog(int level, String tag, String logMsg) {
        getInst().getDataSource().storeLog(new ConsoleModel(level, tag, logMsg));
    }

    public DataSource getDataSource() {
        return mDataSource;
    }


    public RefWatcher getRefWatcher() {
        return mRefWatcher;
    }

    public StorageInfoManager getStorageInfoManager() {
        return mStorageInfoManager;
    }

    public InitConfig getInitCfg() {
        return mInitCfg;
    }
}
