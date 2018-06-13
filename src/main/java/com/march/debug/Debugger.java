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
    private DebugInjector mDebugInjector;

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

    public void init(Application app, boolean debug, DebugInjector injector) {
        mDebugInjector = injector == null ? DebugInjector.EMPTY : injector;
        mDebug = debug;
        Stetho.initialize(Stetho.newInitializerBuilder(app)
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(app))
                .build());
        initLeakCanary(app);
        handleLog("debug", "日志采集初始化完毕");
    }

    private void initLeakCanary(Application app) {
        if (LeakCanary.isInAnalyzerProcess(app)) {
            return;
        }
        LeakCanary.install(app);
        mRefWatcher = LeakCanary.install(app);
    }

    public void initOkHttp(OkHttpClient.Builder builder) {
        builder.addInterceptor(new StethoInterceptor());
        builder.addInterceptor(new DebugInterceptor());
    }

    public void setDebug(boolean debug) {
        this.mDebug = debug;
    }

    public RefWatcher getRefWatcher() {
        return mRefWatcher;
    }

    public DebugInjector getDebugInjector() {
        return mDebugInjector;
    }

    public void handleLog(String tag, String logMsg) {
        mDataSource.storeLog(new ConsoleModel(tag, logMsg));
    }

    public void handleLog(int level, String tag, String logMsg) {
        mDataSource.storeLog(new ConsoleModel(level, tag, logMsg));
    }

    public DataSource getDataSource() {
        return mDataSource;
    }
}
