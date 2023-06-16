package io.github.delay.bean;


import io.github.delay.properties.DelayProperties;

import java.io.*;
import java.util.Objects;
import java.util.concurrent.DelayQueue;

/**
 * @author 苦瓜不苦
 * @date 2023/6/16 9:16
 **/
public class DelayQueueImpl<T> extends DelayQueue<DelayedImpl<T>> {


    private final DelayProperties.Cache cache;


    private File file;


    public DelayQueueImpl(DelayProperties.Cache cache, File file) {
        super();
        this.cache = cache;
        this.file = file;
        local();
    }


    public DelayQueueImpl(DelayProperties.Cache cache) {
        super();
        this.cache = cache;
    }


    @Override
    public void put(DelayedImpl<T> delayed) {
        // 写入缓存
        if (cache.isOpen()) {
            write(delayed);
        }
        super.put(delayed);
    }


    @Override
    public DelayedImpl<T> take() throws InterruptedException {
        DelayedImpl<T> delayed = super.take();
        // 删除储存
        if (cache.isOpen()) {
            del(delayed);
        }
        return delayed;
    }


    private void del(DelayedImpl<T> delayed) {
        File file = getFile(delayed);
        if (file.exists()) {
            boolean delete = file.delete();
        }
    }


    private void write(DelayedImpl<T> delayed) {
        File file = getFile(delayed);
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(file))) {
            os.writeObject(delayed);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File getFile(DelayedImpl<T> delayed) {
        return new File(this.file, delayed.getId());
    }


    private void local() {
        // 判断目录是否存在,不存在则新建
        if (!file.isDirectory() && !file.mkdirs()) {
            throw new RuntimeException("cache directory creation failed");
        }
        File[] files = file.listFiles();
        if (Objects.isNull(files) || files.length <= 0) {
            return;
        }
        for (File fileCache : files) {
            DelayedImpl<T> delayed = read(fileCache);
            if (Objects.nonNull(delayed)) {
                super.put(delayed);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private DelayedImpl<T> read(File file) {
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(file))) {
            return (DelayedImpl<T>) is.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


}
