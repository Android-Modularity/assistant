package com.march.assistant;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.march.assistant.base.BaseAssistantFragment;

import java.util.List;

/**
 * CreateAt : 2018/6/13
 * Describe :
 *
 * @author chendong
 */
public interface Injector {

    Injector EMPTY = new Injector() {

        @Override
        public boolean isInterceptRequest(String url) {
            return true;
        }

        @Override
        public void handleScanResult(@NonNull Activity activity, @NonNull CharSequence text) {

        }

    };


    boolean isInterceptRequest(String url);

    void handleScanResult(@NonNull Activity activity,@NonNull CharSequence text);

}
