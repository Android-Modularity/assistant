package com.march.assistant.base;

import android.support.v4.app.Fragment;

/**
 * CreateAt : 2018/6/12
 * Describe :
 *
 * @author chendong
 */
public abstract class BaseAssistantFragment extends Fragment {

    protected String mTitle;

    public interface AssistantFragmentMaker {
        BaseAssistantFragment make(String title);
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
