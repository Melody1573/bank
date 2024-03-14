package com.banksys.batchexecutionsys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class BatchExecutionSysApplication {
	public static void main(String[] args) {
		SpringApplication.run(BatchExecutionSysApplication.class, args);
	}

}
