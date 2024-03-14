package com.banksys.batchexecutionsys.configuration.feignConfig;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 功能：
 * 作者：Luo。
 * 日期：2024/3/1 11:32
 */
@Configuration
public class FeignConfig implements RequestInterceptor {

	@Autowired
	@Qualifier("myRedisTemplate")
	private RedisTemplate redisTemplate;

	@Override
	public void apply(RequestTemplate requestTemplate) {
		//1.生成uuid-token
		UUID uuid = UUID.randomUUID();
		String token = uuid.toString();
		token = token.replace("-", "");
		long currentTimestamp = System.currentTimeMillis();
		HashOperations ho = redisTemplate.opsForHash();
		ho.put(token, "userName", "userName");
		ho.put(token, "id", "id");
		ho.put(token, "currentTime", String.valueOf(currentTimestamp));
		ho.put(token, "token", token);
		//3.将认证成功的信息以JSON的形式返回到前端
		redisTemplate.expire(token, 1, TimeUnit.MINUTES);
		Map entries = ho.entries(token);

		// ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		// HttpServletRequest request = sra.getRequest();
		//获取请求头authorization中的token值并携带到下一个请求中
		// requestTemplate.header("authorization",request.getHeader("authorization"));
		requestTemplate.header("authorization",token);
	}
}
