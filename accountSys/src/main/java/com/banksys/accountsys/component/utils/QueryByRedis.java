package com.banksys.accountsys.component.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

/**
 * 功能：
 * 作者：Luo。
 * 日期：2024/2/27 9:50
 */
@Component
public class QueryByRedis {

    @Autowired
    @Qualifier("myRedisTemplate")
    private RedisTemplate redisTemplate;

    public String queryStringByRedis(String key) {
        ValueOperations vo = redisTemplate.opsForValue();
        Object obj = vo.get(key);
        if (obj instanceof String) {
            return (String) obj;
        }
        return null;
    }
}
