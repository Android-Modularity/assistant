package com.march.assistant.funcs.env;

/**
 * CreateAt : 2018/9/23
 * Describe :
 *
 * @author chendong
 */
public class EnvModel {

    private String key;
    private String content;
    private Object extra;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Object getExtra() {
        return extra;
    }

    public void setExtra(Object extra) {
        this.extra = extra;
    }
}
