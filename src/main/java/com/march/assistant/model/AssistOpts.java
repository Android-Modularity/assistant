package com.march.assistant.model;

import com.march.assistant.callback.ScanResultCallback;
import com.march.assistant.callback.UrlInterceptCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * CreateAt : 2018/11/29
 * Describe :
 *
 * @author chendong
 */
public class AssistOpts {

    // 是不是处于 debug 模式
    private boolean debug        = true;
    // 显示 debug 按钮
    private boolean showDebugBtn = true;
    // tab 列表
    private List<AssistTab>      tabs;
    // 扫描结果回调
    private ScanResultCallback   scanResultCallback;
    // url 拦截规则
    private UrlInterceptCallback shouldInterceptUrlCallback;

    private Class  buildClazz;
    private String channel;
    
    private AssistOpts() {
        this.tabs = new ArrayList<>();
    }

    public static AssistOpts create() {
        return new AssistOpts();
    }

    public static AssistOpts create( boolean debug, boolean showDebugBtn) {
        AssistOpts assistOpts = new AssistOpts();
        assistOpts.debug = debug;
        assistOpts.showDebugBtn = showDebugBtn;
        return assistOpts;
    }

    public AssistOpts config(Class buildClazz, String channel) {
        this.buildClazz = buildClazz;
        this.channel = channel;
        return this;
    }

    public AssistOpts addTab(AssistTab tab) {
        tabs.add(tab);
        return this;
    }

    public List<AssistTab> getTabs() {
        return tabs;
    }


    public void setShowDebugBtn(boolean showDebugBtn) {
        this.showDebugBtn = showDebugBtn;
    }

    public boolean isShowDebugBtn() {
        return showDebugBtn;
    }

    public boolean isDebug() {
        return debug;
    }


    public AssistOpts setScanResultCallback(ScanResultCallback scanResultCallback) {
        this.scanResultCallback = scanResultCallback;
        return this;
    }

    public AssistOpts setShouldInterceptUrlCallback(UrlInterceptCallback callback) {
        this.shouldInterceptUrlCallback = callback;
        return this;
    }

    public ScanResultCallback getScanResultCallback() {
        return scanResultCallback;
    }

    public UrlInterceptCallback getUrlInteceptCallback() {
        return shouldInterceptUrlCallback;
    }

    public Class getBuildClazz() {
        return buildClazz;
    }

    public String getChannel() {
        return channel;
    }
}

