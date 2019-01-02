package com.march.assistant.callback;

import android.app.Activity;
import android.support.annotation.NonNull;

/**
 * CreateAt : 2018/9/21
 * Describe :
 *
 * @author chendong
 */
public interface ScanResultCallback {
    boolean onScanResult(@NonNull Activity activity, @NonNull CharSequence text);
}
