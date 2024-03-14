package com.banksys.accountsys.configuration.securityConfig;

import com.alibaba.fastjson.JSON;
import com.banksys.accountsys.component.result.ResultData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

/**
 * 功能：
 * 作者：Luo。
 * 日期：2024/1/31 11:37
 */

//启动安全配置
@Configuration
@EnableWebSecurity
public class AuthConfig {

    @Autowired
    @Qualifier("myRedisTemplate")
    private RedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    // 配置密码加密工具到spring容器
    @Bean
    public BCryptPasswordEncoder configPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //配置认证管理器authenticationManager
    @Bean
    public AuthenticationManager configAM(AuthenticationConfiguration ac) throws Exception {
        return ac.getAuthenticationManager();
    }

    //认证、授权及其安全管理


    //接口放行,允许哪些接口默认可以使用
    @Bean
    public SecurityFilterChain manageAuth(HttpSecurity httpSecurity, AuthenticationManager manager) throws Exception {
        //禁用跨站请求伪造
        httpSecurity.csrf().disable();
        //允许跨域请求
        httpSecurity.cors();

        //配置放行
        httpSecurity.authorizeRequests()
                .antMatchers("/swagger-ui.html", "/v2/api-docs", "/swagger-resources/**", "/webjars/**").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .addFilter(new BasicAuthenticationFilter(manager) {
                    @Override
                    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
                        //当用户发起接口调用时会走该过滤器,此时获取token对token进行校验
                        //校验规则:有无token及token是否过期
                        Enumeration enumeration = request.getHeaderNames();
                        //针对postman调用的token截取
                        String authorization = request.getHeader("authorization");
                        if (authorization != null){
                            String token = authorization.substring(authorization.indexOf(" ") + 1);
                            //判断token是否有效
                            HashOperations ho = redisTemplate.opsForHash();
                            Map map = ho.entries(token);
                            if (map != null && map.size() == 4) {
                                //有效则对该接口放行
                                //构造一个非匿名的认证的对象
                                UsernamePasswordAuthenticationToken upaw =
                                        new UsernamePasswordAuthenticationToken(null, null, null);
                                //放入安全上下文中
                                SecurityContextHolder.getContext().setAuthentication(upaw);
                            } else {
                                //无效则不允许访问接口
                                response.setCharacterEncoding("utf8");
                                String jsonString = JSON.toJSONString(ResultData.expire());
                                response.getWriter().write(jsonString);
                                response.getWriter().close();
                                return;
                            }
                            super.doFilterInternal(request, response, chain);
                        }else{
                            //无效则不允许访问接口
                            response.setCharacterEncoding("utf8");
                            String jsonString = JSON.toJSONString(ResultData.expire());
                            response.getWriter().write(jsonString);
                            response.getWriter().close();
                            return;
                        }
                    }
                });

        return httpSecurity.build();
    }
}
