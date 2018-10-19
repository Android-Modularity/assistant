package com.march.assistant;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.march.assistant.adapter.FragmentMakeAdapter;
import com.march.assistant.adapter.OkHttpInterceptAdapter;
import com.march.assistant.adapter.ScanResultAdapter;
import com.march.assistant.common.AssistantActivityLifeCallback;
import com.march.assistant.common.StorageInfoManager;
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

    private static Assistant              sInst;
    private        DataSource             mDataSource;
    private        RefWatcher             mRefWatcher;
    private        StorageInfoManager     mStorageInfoManager;
    private        InitConfig             mConfig;
    private        FragmentMakeAdapter    mFragmentMakeAdapter;
    private        ScanResultAdapter      mScanResultAdapter;
    private        OkHttpInterceptAdapter mOkHttpInterceptAdapter;

    private Assistant() {
        mDataSource = new DataSource();
        mStorageInfoManager = new StorageInfoManager();
        mStorageInfoManager.backUp();
    }

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

    public static void init(Application app, InitConfig cfg) {
        Assistant inst = getInst();
        inst.mConfig = cfg;
        inst.mFragmentMakeAdapter = cfg.fragmentMakeAdapter;
        inst.mScanResultAdapter = cfg.scanResultAdapter;
        inst.mOkHttpInterceptAdapter = cfg.okHttpInterceptAdapter;
        Stetho.initializeWithDefaults(app);
        inst.initLeakCanary(app);
        app.registerActivityLifecycleCallbacks(new AssistantActivityLifeCallback());
    }

    // 初始化内存泄漏检测
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
        mRefWatcher = LeakCanary.refWatcher(app)
                .listenerServiceClass(DisplayLeakService.class)
                .excludedRefs(excludedRefs)
                .buildAndInstall();

    }

    // 绑定 okHttp 调试
    public void hookOkHttp(OkHttpClient.Builder builder) {
        builder.addNetworkInterceptor(new StethoInterceptor());
        builder.addInterceptor(new CharlesInterceptor());
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

    /*
        获取配置项
     */


    public InitConfig getConfig() {
        return mConfig;
    }

    public FragmentMakeAdapter getFragmentMakeAdapter() {
        return mFragmentMakeAdapter;
    }

    public ScanResultAdapter getScanResultAdapter() {
        return mScanResultAdapter;
    }

    public OkHttpInterceptAdapter getOkHttpInterceptAdapter() {
        return mOkHttpInterceptAdapter;
    }
}
