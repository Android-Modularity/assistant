package com.march.debug.funcs.browser;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.march.common.utils.FileUtils;
import com.march.common.utils.StreamUtils;
import com.march.debug.R;
import com.march.debug.base.BaseDebugActivity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * CreateAt : 2018/6/12
 * Describe :
 *
 * @author chendong
 */
public class BrowserTextActivity extends BaseDebugActivity {

    private ExecutorService mExecutorService;

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
        mExecutorService = Executors.newSingleThreadExecutor();
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
        if (FileUtils.isNotExist(path)) {
            return;
        }
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final String content = StreamUtils.saveStreamToString(new FileInputStream(path));
                    mContentTv.post(new Runnable() {
                        @Override
                        public void run() {
                            mContentTv.setText(content);
                        }
                    });
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
