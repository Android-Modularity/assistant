package com.zfy.assistant;

import android.app.Application;

import com.zfy.assistant.model.AssistInfo;
import com.zfy.assistant.model.AssistOpts;

/**
 * CreateAt : 2018/12/1
 * Describe : release 不做调试
 *
 * @author chendong
 */
public class AssistantReleaseImpl implements IAssistant {

    private AssistOpts mAssistOpts;
    private AssistInfo mAssistInfo;

    @Override
    public void init(Application app, AssistOpts opts) {
        mAssistOpts = AssistOpts.create();
        mAssistInfo = new AssistInfo();
        // do nothing
    }

    @Override
    public void hookOkHttp(Object builder) {
        // do nothing
    }

    @Override
    public void leakCanaryWatch(Object obj, String reference) {
        // do nothing
    }

    @Override
    public AssistOpts opts() {
        return mAssistOpts;
    }

    @Override
    public AssistInfo info() {
        return mAssistInfo;
    }

    @Override
    public void flush() {
        // do nothing
    }
}
