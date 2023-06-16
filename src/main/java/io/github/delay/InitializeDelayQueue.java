package io.github.delay;

import io.github.delay.properties.DelayProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 初始化延迟队列
 *
 * @author 苦瓜不苦
 * @date 2023/6/13 16:38
 **/
@EnableConfigurationProperties(DelayProperties.class)
public class InitializeDelayQueue<T> implements InitializingBean {


    private final List<AbstractConsumer<T>> consumerList;

    private final DelayProperties delayProperties;

    public InitializeDelayQueue(List<AbstractConsumer<T>> consumerList, DelayProperties delayProperties) {
        this.consumerList = consumerList;
        this.delayProperties = delayProperties;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        if (Objects.isNull(consumerList) || consumerList.isEmpty()) {
            return;
        }
        ThreadPoolExecutor executor = threadPoolExecutor(consumerList.size());
        consumerList.forEach(consumer -> {
            // 开启任务队列
            consumer.set(delayProperties);
            executor.execute(consumer.runnable());
        });
    }

    /**
     * 创建线程池
     *
     * @param size 数量
     * @return
     */
    private ThreadPoolExecutor threadPoolExecutor(int size) {
        return new ThreadPoolExecutor(size, (size + size), size, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new ThreadPoolExecutor.AbortPolicy());
    }


}
