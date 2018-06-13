package com.march.debug.base;

import android.support.v4.app.Fragment;

/**
 * CreateAt : 2018/6/12
 * Describe :
 *
 * @author chendong
 */
public abstract class BaseDebugFragment extends Fragment {

    protected String mTitle;

    public interface DebugFragmentMaker {
        BaseDebugFragment make(String title);
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    public boolean onBackPressed(){
        return false;
    }
}
