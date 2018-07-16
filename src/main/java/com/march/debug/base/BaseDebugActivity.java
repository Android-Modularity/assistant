package com.march.debug.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.march.common.utils.immersion.ImmersionStatusBarUtils;

/**
 * CreateAt : 2018/6/13
 * Describe :
 *
 * @author chendong
 */
public class BaseDebugActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImmersionStatusBarUtils.translucent(this);
        ImmersionStatusBarUtils.setStatusBarLightMode(this);
    }
}
