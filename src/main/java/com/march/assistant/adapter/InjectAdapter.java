package com.march.assistant.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;

/**
 * CreateAt : 2018/7/16
 * Describe :
 *
 * @author chendong
 */
public interface InjectAdapter {

    boolean isInterceptRequest(String url);

    void handleScanResult(@NonNull Activity activity,@NonNull CharSequence text);

}
