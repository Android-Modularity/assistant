package com.march.debug;

import android.app.Activity;
import android.support.annotation.NonNull;

/**
 * CreateAt : 2018/6/13
 * Describe :
 *
 * @author chendong
 */
public interface DebugInjector {

    DebugInjector EMPTY = new DebugInjector() {
        @Override
        public boolean checkNetModel(String url) {
            return true;
        }

        @Override
        public void handleScanResult(@NonNull Activity activity, @NonNull CharSequence text) {

        }

        @Override
        public Class getConfigClass() {
            return BuildConfig.class;
        }
    };

    /**
     * @param url url
     * @return true save, false not save
     */
    boolean checkNetModel(String url);

    void handleScanResult(@NonNull Activity activity,@NonNull CharSequence text);

    Class getConfigClass();

}
