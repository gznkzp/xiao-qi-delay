package io.github.delay.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;
import java.io.Serializable;

/**
 * @author 苦瓜不苦
 * @date 2023/6/13 22:43
 **/
@ConfigurationProperties(
        prefix = "delay"
)
public class DelayProperties implements Serializable {

    private Cache cache;

    public DelayProperties() {
    }

    public Cache getCache() {
        return cache;
    }

    public void setCache(Cache cache) {
        this.cache = cache;
    }

    @Override
    public String toString() {
        return "DelayProperties{" +
                "cache=" + cache +
                '}';
    }

    public static class Cache implements Serializable {
        /**
         * 是否开启持久化
         */
        private boolean open = false;

        /**
         * 持久化目录
         */
        private String store;

        public boolean isOpen() {
            return open;
        }

        public void setOpen(boolean open) {
            this.open = open;
        }

        public String getStore() {
            return store;
        }

        public void setStore(String store) {
            this.store = store;
            if (this.open) {
                File file = new File(this.store);
                if (!file.isDirectory()) {
                    throw new RuntimeException("could not find the persistence root directory");
                }
            }
        }

        @Override
        public String toString() {
            return "Cache{" +
                    "open=" + open +
                    ", store='" + store + '\'' +
                    '}';
        }
    }


}
