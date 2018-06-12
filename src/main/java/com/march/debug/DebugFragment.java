package com.march.debug;

import android.support.v4.app.Fragment;

/**
 * CreateAt : 2018/6/12
 * Describe :
 *
 * @author chendong
 */
public abstract class DebugFragment extends Fragment {

    protected String mTitle;

    public interface DebugFragmentMaker {
        DebugFragment make(String title);
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }
}
