package com.march.debug.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.march.common.impl.ActivityLifecycleCallback;
import com.march.common.utils.DimensUtils;
import com.march.common.view.DragLayout;
import com.march.debug.DebugActivity;
import com.march.debug.R;
import com.march.debug.base.BaseDebugActivity;

/**
 * CreateAt : 2018/6/19
 * Describe :
 *
 * @author chendong
 */
public class DebugActivityLifecycleCallback extends ActivityLifecycleCallback {

    @Override
    public void onActivityCreated(final Activity activity, Bundle savedInstanceState) {
        super.onActivityCreated(activity, savedInstanceState);
        try {
            if (activity instanceof BaseDebugActivity) {
                return;
            }
            DragLayout dragLayout = (DragLayout) activity.getLayoutInflater().inflate(R.layout.debug_view, null);
            dragLayout.setLayoutParams(new FrameLayout.LayoutParams(DimensUtils.dp2px(40), DimensUtils.dp2px(40)));
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.END | Gravity.BOTTOM;
            params.rightMargin = 100;
            params.bottomMargin = 150;
            dragLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.startActivity(new Intent(activity, DebugActivity.class));
                }
            });
            dragLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    activity.startActivity(new Intent(activity, DebugActivity.class));
                    return true;
                }
            });
            ((ViewGroup) activity.getWindow().getDecorView()).addView(dragLayout, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
