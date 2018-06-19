package com.march.debug;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.march.common.impl.ActivityLifecycleCallback;
import com.march.common.utils.DimensUtils;
import com.march.common.view.DragLayout;
import com.march.debug.base.BaseDebugActivity;
import com.march.debug.common.StorageInfoManager;
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

    private DataSource         mDataSource;
    private boolean            mDebug;
    private RefWatcher         mRefWatcher;
    private DebugInjector      mInjector;
    private StorageInfoManager mStorageInfoManager;

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
        mStorageInfoManager = new StorageInfoManager();
        mStorageInfoManager.backUp();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static void init(Application app, final boolean debug, DebugInjector injector) {
        Debugger inst = getInst();
        inst.mInjector = injector == null ? DebugInjector.EMPTY : injector;
        inst.mDebug = debug;
        Stetho.initialize(Stetho.newInitializerBuilder(app)
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(app))
                .build());
        inst.initLeakCanary(app);
        handleLog("debug", "日志采集初始化完毕");

        app.registerActivityLifecycleCallbacks(new ActivityLifecycleCallback() {
            @Override
            public void onActivityCreated(final Activity activity, Bundle savedInstanceState) {
                try {
                    if (activity instanceof BaseDebugActivity) {
                        return;
                    }
                    DragLayout dragLayout = (DragLayout) activity.getLayoutInflater().inflate(R.layout.debug_view, null);
                    dragLayout.setLayoutParams(new FrameLayout.LayoutParams(DimensUtils.dp2px(40), DimensUtils.dp2px(40)));
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.gravity = Gravity.END | Gravity.BOTTOM;
                    params.rightMargin = 100;
                    params.bottomMargin = 100;
                    dragLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            activity.startActivity(new Intent(activity, DebugActivity.class));
                        }
                    });
                    dragLayout.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            activity.startActivity(new Intent(activity, DebugActivity.class));
                            return true;
                        }
                    });
                    ((ViewGroup) activity.getWindow().getDecorView()).addView(dragLayout, params);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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

    public StorageInfoManager getStorageInfoManager() {
        return mStorageInfoManager;
    }
}
