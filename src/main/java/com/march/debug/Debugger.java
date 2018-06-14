package com.march.debug;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.march.debug.funcs.console.ConsoleModel;
import com.march.debug.funcs.net.DebugInterceptor;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import okhttp3.OkHttpClient;

/**
 * CreateAt : 2018/6/11
 * Describe :
 *
 * @author chendong
 */
public class Debugger {

    private DataSource    mDataSource;
    private boolean       mDebug;
    private RefWatcher    mRefWatcher;
    private DebugInjector mInjector;

    private static Debugger sInst;

    public static Debugger getInst() {
        if (sInst == null) {
            synchronized (Debugger.class) {
                if (sInst == null) {
                    sInst = new Debugger();
                }
            }
        }
        return sInst;
    }

    private Debugger() {
        mDebug = true;
        mDataSource = new DataSource();
    }

    public static void init(Application app, boolean debug, DebugInjector injector) {
        Debugger inst = getInst();
        inst.mInjector = injector == null ? DebugInjector.EMPTY : injector;
        inst.mDebug = debug;
        Stetho.initialize(Stetho.newInitializerBuilder(app)
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(app))
                .build());
        inst.initLeakCanary(app);
        handleLog("debug", "日志采集初始化完毕");
    }

    private void initLeakCanary(Application app) {
        if (LeakCanary.isInAnalyzerProcess(app)) {
            return;
        }
        LeakCanary.install(app);
        mRefWatcher = LeakCanary.install(app);
    }

    public static void initOkHttp(OkHttpClient.Builder builder) {
        builder.addInterceptor(new StethoInterceptor());
        builder.addInterceptor(new DebugInterceptor());
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

    public DebugInjector getInjector() {
        return mInjector;
    }
}
