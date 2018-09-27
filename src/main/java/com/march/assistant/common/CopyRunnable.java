package com.march.assistant.common;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.march.common.exts.ToastX;

/**
 * CreateAt : 2018/6/13
 * Describe :
 *
 * @author chendong
 */
public class CopyRunnable implements Runnable {

    private Activity activity;
    private String text;

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
        ClipData mClipData = ClipData.newPlainText("Label", text);
        if (cm != null) {
            cm.setPrimaryClip(mClipData);
        }
        String show = text.length() > 10 ? text.substring(0, 4) + "..." : text;
        ToastX.show(show + "已复制");
    }
}
