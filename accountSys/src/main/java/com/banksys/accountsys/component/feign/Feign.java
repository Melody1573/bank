package com.banksys.accountsys.component.feign;

import com.banksys.accountsys.component.result.ResultData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
@FeignClient("bankSys")
public interface Feign {
	//4调用bankSys模块的test方法测试feign的连通性
	//5.在要是用的地方注入feign并使用
	@RequestMapping("/test")
	ResultData testInvoke();

	@GetMapping("/Bank/queryClientById01")
	String queryClientById01(@RequestParam("id") int id);

	@GetMapping("/Bank/queryClientByCounterIdAndId")
	String queryClientByCounterIdAndId(@RequestParam("id") String id);

	@GetMapping("/Bank/queryClientById")
	ResultData queryClientById(@RequestParam("id") int id);
}
