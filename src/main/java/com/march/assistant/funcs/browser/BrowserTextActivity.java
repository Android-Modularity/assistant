package com.march.assistant.funcs.browser;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.march.assistant.R;
import com.march.assistant.base.BaseAssistantActivity;
import com.march.common.exts.FileX;
import com.march.common.exts.StreamX;
import com.march.common.pool.ExecutorsPool;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * CreateAt : 2018/6/12
 * Describe :
 *
 * @author chendong
 */
public class BrowserTextActivity extends BaseAssistantActivity {


    public static final String DATA = "data";

    private TextView mContentTv;

    public static void startActivity(Context context, String text) {
        Intent intent = new Intent(context, BrowserTextActivity.class);
        intent.putExtra(DATA, text);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browser_text_activity);
        mContentTv = findViewById(R.id.content_tv);
        findViewById(R.id.switch_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断当前屏幕方向
                if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    //切换竖屏
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    //切换横屏
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
            }
        });
        String content = getIntent().getStringExtra(DATA);
        if (TextUtils.isEmpty(content)) {
            content = "没有数据";
        } else if (content.length() < 400) {
            setFileContent(content);
        }
        mContentTv.setText(content);
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
