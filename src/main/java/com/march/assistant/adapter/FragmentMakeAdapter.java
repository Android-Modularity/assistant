package com.march.assistant.adapter;

import com.march.assistant.base.BaseAssistantFragment;

import java.util.List;

/**
 * CreateAt : 2018/7/16
 * Describe :
 *
 * @author chendong
 */
public interface FragmentMakeAdapter {

    List<String> getTitles();

    BaseAssistantFragment makeFragment(String title);
}
