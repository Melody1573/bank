package com.banksys.batchexecutionsys.configuration.scheduledConfig;

import com.banksys.batchexecutionsys.configuration.feign.Feign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

/**
 * 功能：
 * 作者：Luo。
 * 日期：2024/3/7 17:42
 */
@EnableAsync
@Configuration
@EnableScheduling
public class ScheduledConfig {

	@Autowired
	private Feign feign;

	// @Scheduled(cron = "0 0 12 * * 0")
	// @Scheduled(cron = "0 23 * * * *")
	@Scheduled(cron = "0 55 11 * * MON")
	@Async
	@Transactional
	public void run(){
		System.out.println("定时器执行了");
		int  currentBatch = feign.currentBatch();
		int  regularBatch = feign.regularBatch();
		int  giveMoney = feign.giveMoney();
		int  flowRecord = feign.flowRecord();
		System.out.println(currentBatch + "条活期跑批");
		System.out.println(regularBatch + "条定期跑批");
		System.out.println(giveMoney + "条派息");
		System.out.println(flowRecord + "条流水记账");
	}
}
