package com.wenchao.eventbusdemo.eventbus;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author wenchao
 * @date 2019/7/7.
 * @time 18:29
 * description：
 */
public class EventBus {

    private static volatile EventBus instance;
    private Map<Object, List<SubscribeMethod>> cacheMap;
    private Handler mHandler;
    private ExecutorService mExecutorService;

    private EventBus() {
        cacheMap = new HashMap<>();
        mHandler = new Handler(Looper.getMainLooper());
        mExecutorService = Executors.newCachedThreadPool();
    }

    public static EventBus getDefault() {
        if (instance == null) {
            synchronized (EventBus.class) {
                if (instance == null) {
                    instance = new EventBus();
                }
            }
        }
        return instance;
    }

    public void register(Object object) {
        List<SubscribeMethod> list = cacheMap.get(object);
        if (list == null) {
            list = findSubscribeMethods(object);
            cacheMap.put(object, list);
        }
    }

    private List<SubscribeMethod> findSubscribeMethods(Object object) {
        List<SubscribeMethod> list = new ArrayList<>();
        Class<?> clazz = object.getClass();
        //循环找其父类的带有subscribe的方法
        while (clazz != null) {
            //凡是系统级别的父类，直接省略
            final String name = clazz.getName();
            if (name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("android.")) {
                break;
            }

            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                //寻找所有带subscribe注解的方法
                Subscribe subscribe = method.getAnnotation(Subscribe.class);
                if (subscribe == null) {
                    continue;
                }
                //判断当前带有subscribe注解的方法，有且只有一个参数
                Class<?>[] types = method.getParameterTypes();
                if (types.length != 1) {
                    Log.e("wenchao", "EventBus only accepts one parameter!");
                }
                ThreadMode threadMode = subscribe.threadMode();
                SubscribeMethod subscribeMethod = new SubscribeMethod(method, threadMode, types[0]);
                list.add(subscribeMethod);
            }
            clazz = clazz.getSuperclass();
        }
        return list;
    }

    public void post(final Object type) {
        Set<Object> set = cacheMap.keySet();
        Iterator<Object> iterator = set.iterator();
        while (iterator.hasNext()) {
            final Object obj = iterator.next();
            List<SubscribeMethod> list = cacheMap.get(obj);
            for (final SubscribeMethod subscribleMethod : list) {
                // 简单的理解：两个列对比一下，看看是否一致 (不严谨的说法)
                // a（subscribleMethod.getType()）对象所对应的类信息，是b（type.getClass()）对象所对应的类信息的父类或者父接口
                if (subscribleMethod.getType().isAssignableFrom(type.getClass())) {
                    switch (subscribleMethod.getThreadMode()) {
                        // 不管你在post是在主线程 还是在子线程，我都在主线程接受
                        case MAIN:
                            // 主 - 主
                            if (Looper.myLooper() == Looper.getMainLooper()) {
                                invoke(subscribleMethod, obj, type);
                            } else {
                                // 子 - 主
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        invoke(subscribleMethod, obj, type);
                                    }
                                });
                            }
                            break;
                        case BACKGROUND:
                            // 主 - 子
                            if (Looper.myLooper() == Looper.getMainLooper()) {
                                mExecutorService.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        invoke(subscribleMethod, obj, type);
                                    }
                                });
                            } else {
                                // 子 - 子
                                invoke(subscribleMethod, obj, type);
                            }
                            break;

                        default:
                            break;
                    }
                    // 不管你在post是在子线程还是在主线程，我都在子线程接受
                }
            }
        }
    }

    private void invoke(SubscribeMethod subscribeMethod, Object obj, Object bean) {
        Method method = subscribeMethod.getMethod();
        try {
            method.invoke(obj, bean);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
