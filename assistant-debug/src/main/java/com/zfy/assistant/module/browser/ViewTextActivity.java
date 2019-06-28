package com.zfy.assistant.module.browser;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;

import com.march.assistant.R;
import com.zfy.assistant.base.BaseAssistActivity;
import com.zfy.assistant.common.AssistValues;
import com.march.common.pool.ExecutorsPool;
import com.march.common.x.EmptyX;
import com.march.common.x.FileX;
import com.march.common.x.StreamX;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * CreateAt : 2018/6/12
 * Describe : 查看文字显示
 *
 * @author chendong
 */
public class ViewTextActivity extends BaseAssistActivity {

    public static void startActivity(Context context, String text, String path) {
        Intent intent = new Intent(context, ViewTextActivity.class);
        if (EmptyX.isEmpty(text)) {
            AssistValues.VIEW_TEXT = null;
        } else {
            AssistValues.VIEW_TEXT = text;
        }
        // intent.putExtra(AssistValues.KEY_DATA, text);
        intent.putExtra(AssistValues.KEY_FILE, path);
        context.startActivity(intent);
    }

    private TextView mContentTv;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browser_text_activity);
        mContentTv = findViewById(R.id.content_tv);
        findViewById(R.id.switch_btn).setOnClickListener(v -> {
            //判断当前屏幕方向
            if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                //切换竖屏
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                //切换横屏
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        });
        String content = AssistValues.VIEW_TEXT;
        AssistValues.VIEW_TEXT = null;
        if (!TextUtils.isEmpty(content)) {
            mContentTv.setText(content);
        } else {
            String path = getIntent().getStringExtra(AssistValues.KEY_FILE);
            if (!TextUtils.isEmpty(content)) {
                setFileContent(path);
            } else {
                mContentTv.setText("数据错误");
            }
        }
    }

    private void setFileContent(final String path) {
        if (FileX.isNotExist(path)) {
            return;
        }
        ExecutorsPool.bg(() -> {
            try {
                final String content = StreamX.saveStreamToString(new FileInputStream(path));
                ExecutorsPool.ui(() -> mContentTv.setText(content));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
    }
}
