package com.banksys.accountsys.configuration.feignConfig;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 功能：
 * 作者：Luo。
 * 日期：2024/3/1 11:32
 */
@Configuration
public class FeignConfig implements RequestInterceptor {
	@Override
	public void apply(RequestTemplate requestTemplate) {
		ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = sra.getRequest();
		//获取请求头authorization中的token值并携带到下一个请求中
		requestTemplate.header("authorization",request.getHeader("authorization"));
	}
}
