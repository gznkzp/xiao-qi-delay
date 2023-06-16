package io.github.delay;


import io.github.delay.bean.Body;
import io.github.delay.bean.DelayQueueImpl;
import io.github.delay.bean.DelayedImpl;
import io.github.delay.properties.DelayProperties;
import io.github.delay.utils.NameUtil;

import java.io.File;

/**
 * 消费抽象类
 *
 * @author 苦瓜不苦
 * @date 2023/6/13 16:44
 **/
public abstract class AbstractConsumer<T> {

    private DelayQueueImpl<T> queue;


    /**
     * 推送任务
     *
     * @param active 活跃时间,单位(秒)
     * @param id     唯一标识
     * @param data   业务参数
     * @return
     */
    public String put(long active, String id, T data) {
        DelayedImpl<T> delayed = new DelayedImpl<>(active, id, data);
        queue.put(delayed);
        return delayed.getId();
    }

    /**
     * 推送任务
     *
     * @param active 活跃时间,单位(秒)
     * @param data   业务参数
     * @return
     */
    public String put(long active, T data) {
        return put(active, null, data);
    }

    /**
     * 失败回调
     *
     * @param body      数据
     * @param exception 异常
     */
    protected void failure(Body<T> body, Exception exception) {

    }

    /**
     * 成功回调
     *
     * @param body 数据
     */
    protected void success(Body<T> body) {

    }

    /**
     * 消费队列
     *
     * @param body 数据
     */
    protected abstract void execute(Body<T> body);

    /**
     * 前置通知
     *
     * @param body 数据
     */
    protected void before(Body<T> body) {

    }

    void set(DelayProperties delayProperties) {
        DelayProperties.Cache cache = delayProperties.getCache();
        if (cache.isOpen()) {
            String className = NameUtil.underline(this.getClass().getSimpleName());
            File file = new File(cache.getStore(), className);
            this.queue = new DelayQueueImpl<>(cache, file);
        } else {
            this.queue = new DelayQueueImpl<>(cache);
        }
    }


    Runnable runnable() {
        return () -> {
            do {
                Body<T> body = new Body<>();
                try {
                    DelayedImpl<T> delayed = queue.take();
                    body.setBody(delayed);
                    // 前置通知
                    before(body);
                    // 执行任务
                    execute(body);
                    // 成功回调
                    success(body);
                } catch (Exception e) {
                    try {
                        failure(body, e);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } while (true);
        };
    }


}
