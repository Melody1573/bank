package com.banksys.accountsys.configuration.RedisConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 功能：连接redis数据库的接口
 * 作者：Luo。
 * 日期：2024/1/24 9:23
 */
@Configuration
public class RedisConfig {

    @Bean("myRedisTemplate")
    //字符串序列化
    public RedisTemplate configRedisForSerializer(RedisTemplate redisTemplate){
        StringRedisSerializer srs = new StringRedisSerializer();
        redisTemplate.setValueSerializer(srs);
        redisTemplate.setKeySerializer(srs);
        redisTemplate.setHashKeySerializer(srs);
        redisTemplate.setHashValueSerializer(srs);
        return redisTemplate;
    }
}
