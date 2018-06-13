package com.march.debug.common;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.march.common.utils.ToastUtils;

/**
 * CreateAt : 2018/6/13
 * Describe :
 *
 * @author chendong
 */
public class CopyRunnable implements Runnable {

    private Activity activity;
    private String   text;

    public CopyRunnable(Activity activity, String text) {
        this.activity = activity;
        this.text = text;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void run() {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        ClipboardManager cm = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData mClipData = ClipData.newPlainText("Label", "这里是要复制的文字");
        if (cm != null) {
            cm.setPrimaryClip(mClipData);
        }
        String show = text.length() > 10 ? text.substring(0, 4) + "..." : text;
        ToastUtils.show(show + "已复制");
    }
}
