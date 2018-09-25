package com.march.assistant.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * CreateAt : 2018/6/12
 * Describe :
 *
 * @author chendong
 */
public abstract class BaseAssistantFragment extends Fragment {

    protected String mTitle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        initView(view);
        return view;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    public abstract int getLayoutId();

    public abstract void initView(View view);

    public boolean onBackPressed(){
        return false;
    }
}
