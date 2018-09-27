package com.march.assistant;

import com.march.assistant.adapter.FragmentMakeAdapter;
import com.march.assistant.adapter.OkHttpInterceptAdapter;
import com.march.assistant.adapter.ScanResultAdapter;

/**
 * CreateAt : 2018/7/11
 * Describe :
 *
 * @author chendong
 */
public class InitConfig {

    boolean showDebugBtn = true;
    boolean debug = true;

    FragmentMakeAdapter fragmentMakeAdapter;
    ScanResultAdapter scanResultAdapter;
    OkHttpInterceptAdapter okHttpInterceptAdapter;

    public static InitConfig make(boolean debug, boolean showDebugBtn) {
        InitConfig config = new InitConfig();
        config.debug = debug;
        config.showDebugBtn = showDebugBtn;
        return config;
    }

    public InitConfig setFragmentMakeAdapter(FragmentMakeAdapter fragmentMakeAdapter) {
        this.fragmentMakeAdapter = fragmentMakeAdapter;
        return this;
    }

    public InitConfig setScanResultAdapter(ScanResultAdapter scanResultAdapter) {
        this.scanResultAdapter = scanResultAdapter;
        return this;
    }

    public InitConfig setOkHttpInterceptAdapter(OkHttpInterceptAdapter okHttpInterceptAdapter) {
        this.okHttpInterceptAdapter = okHttpInterceptAdapter;
        return this;
    }

    public boolean isShowDebugBtn() {
        return showDebugBtn;
    }

    public InitConfig setShowDebugBtn(boolean showDebugBtn) {
        this.showDebugBtn = showDebugBtn;
        return this;
    }

    public boolean isDebug() {
        return debug;
    }

    public InitConfig setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }
}
