package com.zfy.assistant.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.zfy.assistant.Assistant;
import com.zfy.assistant.DragLayout;
import com.zfy.assistant.EntryActivity;
import com.march.assistant.R;
import com.zfy.assistant.base.BaseAssistActivity;
import com.march.common.impl.ActivityLifecycleCallback;
import com.march.common.x.SizeX;

/**
 * CreateAt : 2018/6/19
 * Describe :
 *
 * @author chendong
 */
public class AssistantActivityLifeCallback extends ActivityLifecycleCallback {

    @Override
    public void onActivityCreated(final Activity activity, Bundle savedInstanceState) {
        try {
            if (!Assistant.assist().opts().isDebug()) {
                return;
            }
            if (!Assistant.assist().opts().isShowDebugBtn()) {
                return;
            }
            if (activity instanceof BaseAssistActivity) {
                return;
            }
            if (activity.getClass().getSimpleName().toLowerCase().contains("splash")) {
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
                    activity.startActivity(new Intent(activity, EntryActivity.class));
                }
            });
            dragLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    dragLayout.setVisibility(View.GONE);
                    return true;
                }
            });
            ((ViewGroup) activity.getWindow().getDecorView()).addView(dragLayout, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
