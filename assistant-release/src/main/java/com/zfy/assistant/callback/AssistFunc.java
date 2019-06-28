package com.zfy.assistant.callback;

/**
 * CreateAt : 2018/12/14
 * Describe :
 *
 * @author chendong
 */
public interface AssistFunc<T, R> {
    R apply(T t);
}
