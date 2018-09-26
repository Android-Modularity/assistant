package com.march.assistant.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.march.assistant.DragLayout;
import com.march.common.exts.SizeX;
import com.march.common.impl.ActivityLifecycleCallback;
import com.march.assistant.AssistantActivity;
import com.march.assistant.Assistant;
import com.march.assistant.R;
import com.march.assistant.base.BaseAssistantActivity;

/**
 * CreateAt : 2018/6/19
 * Describe :
 *
 * @author chendong
 */
public class AssistantActivityLifeCallback extends ActivityLifecycleCallback {

    @Override
    public void onActivityCreated(final Activity activity, Bundle savedInstanceState) {
        super.onActivityCreated(activity, savedInstanceState);
        try {
            if (!Assistant.getInst().getConfig().isShowDebugBtn()) {
                return;
            }
            if (activity.getClass().getSimpleName().contains("alibc")) {
                return;
            }
            if (activity instanceof BaseAssistantActivity) {
                return;
            }
            final DragLayout dragLayout = (DragLayout) activity.getLayoutInflater().inflate(R.layout.debug_view, null);
            dragLayout.setLayoutParams(new FrameLayout.LayoutParams(SizeX.dp2px(40), SizeX.dp2px(40)));
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.END | Gravity.BOTTOM;
            params.rightMargin = 100;
            params.bottomMargin = 350;
            dragLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.startActivity(new Intent(activity, AssistantActivity.class));
                }
            });
            dragLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    activity.startActivity(new Intent(activity, AssistantActivity.class));
                    return true;
                }
            });
            ((ViewGroup) activity.getWindow().getDecorView()).addView(dragLayout, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
