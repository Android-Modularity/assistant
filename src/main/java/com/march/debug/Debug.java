package com.march.debug;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.march.debug.funcs.console.ConsoleModel;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import okhttp3.OkHttpClient;

/**
 * CreateAt : 2018/6/11
 * Describe :
 *
 * @author chendong
 */
public class Debug {

    private static DataSource sDataSource = new DataSource();

    private static boolean debug = true;

    private static RefWatcher sRefWatcher;

    public static void init(Application app, boolean debug) {
        Debug.debug = debug;
        Stetho.initialize(Stetho.newInitializerBuilder(app)
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(app))
                .build());
        initLeakCanary(app);
    }

    private static void initLeakCanary(Application app) {
        if (LeakCanary.isInAnalyzerProcess(app)) {
            return;
        }
        LeakCanary.install(app);
        sRefWatcher = LeakCanary.install(app);
    }

    public static void initOkHttp(OkHttpClient.Builder builder) {
        builder.addInterceptor(new StethoInterceptor());
    }

    public static void setDebug(boolean debug) {
        Debug.debug = debug;
    }

    public static RefWatcher getRefWatcher() {
        return sRefWatcher;
    }

    public static void handleLog(String tag, String logMsg) {
        sDataSource.storeLog(new ConsoleModel(tag, logMsg));
    }

    public static void handleLog(int level, String tag, String logMsg) {
        sDataSource.storeLog(new ConsoleModel(level, tag, logMsg));
    }

    public static DataSource getDataSource() {
        return sDataSource;
    }
}
