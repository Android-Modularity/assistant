package com.march.assistant.common;

import android.text.TextUtils;

import com.march.common.Common;
import com.march.common.exts.ClipboardX;
import com.march.common.exts.ToastX;

/**
 * CreateAt : 2018/6/13
 * Describe :
 *
 * @author chendong
 */
public class CopyRunnable implements Runnable {

    private String   text;

    public CopyRunnable(String text) {
        this.text = text;
    }

    @Override
    public void run() {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        ClipboardX.copy(Common.app(), text);
        String show = text.length() > 10 ? text.substring(0, 4) + "..." : text;
        ToastX.show(show + "已复制");
    }
}
