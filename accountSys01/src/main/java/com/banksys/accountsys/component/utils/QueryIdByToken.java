package com.banksys.accountsys.component.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 功能：
 * 作者：Luo。
 * 日期：2024/2/22 15:01
 */
@Component
public class QueryIdByToken {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    @Qualifier("myRedisTemplate")
    private RedisTemplate redisTemplate;

    public Integer queryIdByToken() {
        String authorization = request.getHeader("authorization");
        if (authorization != null) {
            String token = authorization.substring(authorization.indexOf(" ") + 1);
            HashOperations ho = redisTemplate.opsForHash();
            Map<String, String> map = ho.entries(token);
            String id = map.get("id");
            if (id != null){
                int i = Integer.parseInt(id);
                return i;
            }
            return null;
        }
        return null;
    }

    public String getToken(){
        String authorization = request.getHeader("authorization");
        if (authorization != null) {
            return authorization;
        }
        return null;
    }
}
