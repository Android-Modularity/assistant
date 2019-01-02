package com.march.assistant;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.march.assistant.common.AssistValues;
import com.march.assistant.common.AssistantActivityLifeCallback;
import com.march.assistant.common.AssistantJsonAdapterImpl;
import com.march.assistant.model.AssistInfo;
import com.march.assistant.model.AssistOpts;
import com.march.assistant.model.AssistTab;
import com.march.assistant.module.crash.CrashHandler;
import com.march.assistant.module.file.FileFragment;
import com.march.assistant.module.net.CharlesInterceptor;
import com.march.assistant.module.net.NetFragment;
import com.march.assistant.module.tools.ToolsFragment;
import com.march.common.Common;
import com.march.common.exts.ToastX;
import com.march.common.mgrs.KVMgr;
import com.squareup.leakcanary.AndroidExcludedRefs;
import com.squareup.leakcanary.DisplayLeakService;
import com.squareup.leakcanary.ExcludedRefs;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import okhttp3.OkHttpClient;

/**
 * CreateAt : 2018/12/1
 * Describe :
 *
 * @author chendong
 */
public class AssistantDebugImpl implements IAssistant {

    private AssistOpts mAssistOpts;
    private AssistInfo mAssistInfo;
    private DataSource mDataSource;

    private RefWatcher mRefWatcher; // LeakCanary Watcher

    @Override
    public void init(Application app, AssistOpts opts) {
        // init common
        if (!Common.exports.init) {
            Common.init(app, opts.getBuildClazz());
            Common.appConfig().CHANNEL = opts.getChannel();
            Common.exports.jsonParser = new AssistantJsonAdapterImpl();
            ToastX.Config config = new ToastX.Config();
            config.setOneToast(false);
            ToastX.init(config);
        }
        //
        mAssistOpts = opts;
        mAssistInfo = KVMgr.getInst().getObj(AssistValues.KEY_INFO, AssistInfo.class);
        if (mAssistInfo == null) {
            mAssistInfo = new AssistInfo();
        }
        if (mAssistOpts == null) {
            mAssistOpts = AssistOpts.create();
        }
        mDataSource = KVMgr.getInst().getObj(AssistValues.KEY_SOURCE, DataSource.class);
        if (mDataSource == null) {
            mDataSource = new DataSource();
        }
        // add default tabs
        mAssistOpts.addTab(new AssistTab(AssistTab.ID_TOOL, "工具", tab -> ToolsFragment.newInstance()));
        // mAssistOpts.addTab(new AssistTab(AssistTab.ID_CONSOLE, "控制台", tab -> ConsoleFragment.newInstance()));
        mAssistOpts.addTab(new AssistTab(AssistTab.ID_NET, "网络", tab -> NetFragment.newInstance()));
        mAssistOpts.addTab(new AssistTab(AssistTab.ID_FILE, "文件", tab -> FileFragment.newInstance()));
        //
        CrashHandler.init(app, null);
        Stetho.initializeWithDefaults(app);
        initLeakCanary(app);
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

    @Override
    public void hookOkHttp(Object builder) {
        if (builder instanceof OkHttpClient.Builder) {
            OkHttpClient.Builder builder1 = (OkHttpClient.Builder) builder;
            // 注入 chrome 调试工具
            builder1.addNetworkInterceptor(new StethoInterceptor());
            // 注入抓包工具
            builder1.addInterceptor(new CharlesInterceptor());
        }
    }

    @Override
    public void leakCanaryWatch(Object obj, String reference) {
        if (mRefWatcher != null) {
            mRefWatcher.watch(obj, reference);
        }
    }

    public DataSource dataSource() {
        return mDataSource;
    }

    @Override
    public AssistOpts opts() {
        return mAssistOpts;
    }

    @Override
    public AssistInfo info() {
        return mAssistInfo;
    }

    @Override
    public void flush() {
        if (mAssistInfo != null) {
            KVMgr.getInst().putObj(AssistValues.KEY_INFO, mAssistInfo);
        }
        if (mDataSource != null) {
            mDataSource.flush();
        }
    }
}
