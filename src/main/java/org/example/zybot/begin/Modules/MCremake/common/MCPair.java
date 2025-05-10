package org.example.zybot.begin.Modules.MCremake.common;

import org.springframework.stereotype.Service;

@Service
/*
 * 简单的 Pair 类，用于存储两个值。
 *
 * @param <T> 第一个值的类型
 * @param <U> 第二个值的类型
 */
public class MCPair<T, U> {
    private T first;
    private U second;

    public MCPair(){
    }

    public MCPair(final T first, final U second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public U getSecond() {
        return second;
    }
    public void setFirst(final T first) {
        this.first = first;
    }
    public void setSecond(final U second) {
        this.second = second;
    }
}