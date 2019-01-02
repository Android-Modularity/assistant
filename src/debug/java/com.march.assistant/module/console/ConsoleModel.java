package com.march.assistant.module.console;

/**
 * CreateAt : 2018/6/12
 * Describe :
 *
 * @author chendong
 */
public class ConsoleModel implements java.io.Serializable{

    private int level = -1;
    private String tag;
    private String msg;

    public ConsoleModel() {
    }

    public ConsoleModel(int level, String tag, String msg) {
        this.level = level;
        this.tag = tag;
        this.msg = msg;
    }

    public ConsoleModel(String tag, String msg) {
        this.tag = tag;
        this.msg = msg;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
