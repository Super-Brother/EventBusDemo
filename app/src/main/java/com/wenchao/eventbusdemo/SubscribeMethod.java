package com.wenchao.eventbusdemo;

import java.lang.reflect.Method;

/**
 * @author wenchao
 * @date 2019/7/7.
 * @time 18:43
 * description：
 */
public class SubscribeMethod {

    /**
     * 回调方法
     */
    private Method mMethod;

    /**
     * 线程模式
     */
    private ThreadMode mThreadMode;

    /**
     * 回调方法参数类型
     */
    private Class<?> type;

    public SubscribeMethod(Method mMethod, ThreadMode mThreadMode, Class<?> type) {
        this.mMethod = mMethod;
        this.mThreadMode = mThreadMode;
        this.type = type;
    }

    public Method getMethod() {
        return mMethod;
    }

    public void setMethod(Method mMethod) {
        this.mMethod = mMethod;
    }

    public ThreadMode getThreadMode() {
        return mThreadMode;
    }

    public void setThreadMode(ThreadMode mThreadMode) {
        this.mThreadMode = mThreadMode;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }
}
