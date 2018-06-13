package com.march.debug.funcs.browser;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.march.common.utils.StreamUtils;
import com.march.debug.R;
import com.march.debug.base.BaseDebugActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * CreateAt : 2018/6/12
 * Describe :
 *
 * @author chendong
 */
public class BrowserTextActivity extends BaseDebugActivity {


    public static final String DATA = "data";

    public static void startActivity(Context context, String text) {
        Intent intent = new Intent(context, BrowserTextActivity.class);
        intent.putExtra(DATA, text);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browser_text_activity);
        TextView contentTv = findViewById(R.id.content_tv);
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
        if(content.length() < 400 ){
            File file = new File(content);
            if(file.exists() && file.isFile() && file.length() > 0){
                try {
                    content =  StreamUtils.saveStreamToString(new FileInputStream(file));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        if (TextUtils.isEmpty(content)) {
            content = "没有数据";
        }
        contentTv.setText(content);
    }
}
