package com.march.assistant.module.tools;

import com.march.assistant.common.CopyRunnable;

/**
 * CreateAt : 2018/6/13
 * Describe :
 *
 * @author chendong
 */
public class ToolsModel {

    public String   title;
    public String   text;
    public Runnable runnable;

    ToolsModel(String title, Object text, Runnable runnable) {
        this.title = title;
        this.text = String.valueOf(text);
        this.runnable = runnable;
    }

    ToolsModel(String title, Object text) {
        this.title = title;
        this.text = String.valueOf(text);
        this.runnable = new CopyRunnable(this.text);
    }
}
