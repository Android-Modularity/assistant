package com.march.assistant;

/**
 * CreateAt : 2018/6/11
 * Describe : 获取 debug 下的实现
 *
 * @author chendong
 */
public class Assistant {

    private static volatile IAssistant sAssistant;

    public static IAssistant assist() {
        if (sAssistant == null) {
            synchronized (Assistant.class) {
                if (sAssistant == null) {
                    sAssistant = new AssistantDebugImpl();
                }
            }
        }
        return sAssistant;
    }

    private Assistant() {
    }

}
