package com.banksys.batchexecutionsys.configuration.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 功能：
 * 作者：Luo。
 * 日期：2024/3/1 10:37
 */
//1导入maven
//2启用feign客户端(Appliction)
//3写注释
@RestController
//4声明feign客户端
@FeignClient("accountSys")
public interface Feign {
	//4调用bankSys模块的test方法测试feign的连通性
	//5.在要是用的地方注入feign并使用
	@GetMapping("/Account/currentBatch")
	int currentBatch();

	@GetMapping("/Account/regularBatch")
	int regularBatch();

	@GetMapping("/Account/giveMoney")
	int giveMoney();

	@GetMapping("/Account/flowRecord")
	int flowRecord();
}
