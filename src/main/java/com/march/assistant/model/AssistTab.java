package com.march.assistant.model;

import com.march.assistant.base.BaseAssistFragment;
import com.march.assistant.callback.AssistFunc;

/**
 * CreateAt : 2018/11/29
 * Describe :
 *
 * @author chendong
 */
public class AssistTab {

    public static final int ID_TOOL    = 400;
    public static final int ID_CONSOLE = 300;
    public static final int ID_NET     = 200;
    public static final int ID_FILE    = 100;

    public int                                       id;
    public String                                    title;
    public AssistFunc<AssistTab, BaseAssistFragment> maker;


    public AssistTab(int id, String title, AssistFunc<AssistTab, BaseAssistFragment> maker) {
        this.id = id;
        this.title = title;
        this.maker = maker;
    }
}
