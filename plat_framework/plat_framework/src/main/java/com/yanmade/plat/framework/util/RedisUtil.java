package com.yanmade.plat.framework.util;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @PostConstruct
    private void process() {
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
    }

    /**
     * 字符串类型处理
     * 
     * @param key
     * @return
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void set(String key, String value, int timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }
    
    public void delete(String key) {
        redisTemplate.opsForValue().getOperations().delete(key);
    }

    public int increment(String key) {
        return redisTemplate.opsForValue().increment(key).intValue();
    }

    /**
     * hash类型处理
     * 
     * @param key
     * @return
     */
    public Map<Object, Object> hashGet(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    public Object hashGet(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    public void hashSet(String key, Map<Object, Object> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }
    
    /**
     * set类型处理
     */
    public Set<Object> sGet(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    public void sSet(String key, Object... value) {
        redisTemplate.opsForSet().add(key, value);
    }

    /**
     * 设置过期时间
     */
    public void expire(String key, int timeout, TimeUnit unit) {
        redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 获取过期时间
     */
    public Long getExpire(String key, TimeUnit timeUnit) {
        return redisTemplate.getExpire(key, timeUnit);
    }

}
