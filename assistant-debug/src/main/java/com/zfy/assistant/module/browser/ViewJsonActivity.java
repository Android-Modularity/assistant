package com.zfy.assistant.module.browser;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.march.assistant.R;
import com.zfy.assistant.base.BaseAssistActivity;
import com.zfy.assistant.common.AssistValues;
import com.march.common.pool.ExecutorsPool;
import com.march.common.x.EmptyX;
import com.march.common.x.FileX;
import com.march.common.x.StreamX;
import com.yuyh.jsonviewer.library.JsonRecyclerView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * CreateAt : 2018/12/3
 * Describe : 查看 json 显示
 *
 * @author chendong
 */
public class ViewJsonActivity extends BaseAssistActivity {

    public static void startActivity(Context context, String text, String path) {
        Intent intent = new Intent(context, ViewJsonActivity.class);
        if (EmptyX.isEmpty(text)) {
            AssistValues.VIEW_TEXT = null;
        } else {
            AssistValues.VIEW_TEXT = text;
        }
        // intent.putExtra(AssistValues.KEY_DATA, text);
        intent.putExtra(AssistValues.KEY_FILE, path);
        context.startActivity(intent);
    }

    private JsonRecyclerView mJsonRecyclerView;
    private TextView         mInfoTv;
    private ProgressBar      mProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browser_json_activity);
        mJsonRecyclerView = findViewById(R.id.json_rv);
        mInfoTv = findViewById(R.id.info_tv);
        mProgressBar = findViewById(R.id.loading_pb);
        mProgressBar.setVisibility(View.VISIBLE);
        initJsonView();
        String content = AssistValues.VIEW_TEXT;
        AssistValues.VIEW_TEXT = null;
        if (!TextUtils.isEmpty(content)) {
            bindJson(content);
        } else {
            String path = getIntent().getStringExtra(AssistValues.KEY_FILE);
            if (!TextUtils.isEmpty(content)) {
                setFileContent(path);
            } else {
                mInfoTv.setVisibility(View.VISIBLE);
                mInfoTv.setText("数据错误");
            }
        }
    }

    private void initJsonView() {
        // mJsonRecyclerView.setKeyColor();
        // mJsonRecyclerView.setValueTextColor();
        // mJsonRecyclerView.setValueNumberColor();
        // mJsonRecyclerView.setValueUrlColor();
        // mJsonRecyclerView.setValueNullColor();
        // mJsonRecyclerView.setBracesColor();
        mJsonRecyclerView.setTextSize(20);
        mJsonRecyclerView.setScaleEnable(true);
    }

    private void bindJson(String json) {
        mProgressBar.setVisibility(View.GONE);
        try {

            mJsonRecyclerView.bindJson(json);
        } catch (Exception e) {
            mInfoTv.setVisibility(View.VISIBLE);
            mInfoTv.setText("解析失败，不是 json 格式 " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setFileContent(final String path) {
        if (FileX.isNotExist(path)) {
            mInfoTv.setVisibility(View.VISIBLE);
            mInfoTv.setText("文件不存在");
            return;
        }
        ExecutorsPool.bg(() -> {
            try {
                final String content = StreamX.saveStreamToString(new FileInputStream(path));
                ExecutorsPool.ui(() -> bindJson(content));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AssistValues.VIEW_TEXT = null;
    }
}
