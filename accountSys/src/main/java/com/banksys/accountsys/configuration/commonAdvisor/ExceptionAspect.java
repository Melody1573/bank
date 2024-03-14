package com.banksys.accountsys.configuration.commonAdvisor;


import org.aspectj.lang.annotation.AfterThrowing;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 功能：
 * 作者：Luo。
 * 日期：2024/2/20 19:38
 */
// @Aspect
// @Component
// @EnableAspectJAutoProxy
public class ExceptionAspect {

    @Autowired
    private HttpServletResponse response;

    @AfterThrowing(pointcut = "execution(* com.banksys.banksysback.service.*.*(..))", throwing = "e")
    public void afterThrowing(Exception e) throws IOException {
        // 在这里处理捕获到的异常
        String eName = e.getClass().getName();
        eName = eName.substring(eName.lastIndexOf(".") +1);
        System.out.println(eName);
        response.getWriter().write("errors");
        response.getWriter().close();
    }
}
