package com.march.assistant;

import android.app.Application;

import com.march.assistant.model.AssistInfo;
import com.march.assistant.model.AssistOpts;

/**
 * CreateAt : 2018/12/1
 * Describe :
 *
 * @author chendong
 */
public interface IAssistant {

    /**
     * 初始化
     *
     * @param app application
     */
    void init(Application app, AssistOpts opts);

    /**
     * 调试 OkHttp
     *
     * @param builder okhttp client builder
     */
    void hookOkHttp(Object builder);


    /**
     * 封装 leak canary watch 方法
     *
     * @param obj       watch 的对象
     * @param reference 标记
     */
    void leakCanaryWatch(Object obj, String reference);


    /**
     * 输入配置
     *
     * @return AssistOpts
     */
    AssistOpts opts();

    /**
     * 配置信息存储
     *
     * @return AssistInfo
     */
    AssistInfo info();


    /**
     * 向磁盘写入配置信息
     */
    void flush();

}
