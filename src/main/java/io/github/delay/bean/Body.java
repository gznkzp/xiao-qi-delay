package io.github.delay.bean;

import java.io.Serializable;

/**
 * @author 苦瓜不苦
 * @date 2023/6/15 23:06
 **/
public class Body<T> implements Serializable {

    /**
     * 唯一标识
     */
    private String id;

    /**
     * 业务参数
     */
    private T data;

    public void setBody(DelayedImpl<T> delayed) {
        this.id = delayed.getId();
        this.data = delayed.getData();
    }


    public String getId() {
        return id;
    }


    public T getData() {
        return data;
    }


    @Override
    public String toString() {
        return "Body{" +
                "id='" + id + '\'' +
                ", data=" + data +
                '}';
    }
}
