package com.banksys.accountsys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class AccountSysApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountSysApplication.class, args);
    }

}
