package com.banksys.accountsys.controller;

import com.banksys.accountsys.component.feign.Feign;
import com.banksys.accountsys.component.utils.GetRandomNo;
import com.banksys.accountsys.dao.AccountMapper;
import com.banksys.accountsys.dao.TranslogMapper;
import com.banksys.accountsys.model.Account;
import com.banksys.accountsys.model.Translog;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * 功能：
 * 作者：Luo。
 * 日期：2024/3/7 16:54
 */
@RestController
@RequestMapping("/Account")
public class BatchExecutionController {

	@Autowired
	private AccountMapper accountMapper;

	@Autowired
	private TranslogMapper translogMapper;

	@Autowired
	private Feign feign;

	//活期跑批
	@GetMapping("/currentBatch")
	public int currentBatch() {
		// 活期
		LambdaUpdateWrapper<Account> updateWrapper = Wrappers.lambdaUpdate();
		updateWrapper
				.eq(Account::getStatus, "1")
				.eq(Account::getDepositType, "1")
				.setSql("interest = ROUND(balance*DATEDIFF(NOW(), balance_update)*rate/360,2)");
		int update = accountMapper.update(null, updateWrapper);
		return update;
	}

	//定期跑批
	@GetMapping("/regularBatch")
	public int regularBatch() {
		// 定期
		LambdaUpdateWrapper<Account> updateWrapper = Wrappers.lambdaUpdate();
		updateWrapper
				.eq(Account::getStatus, "1")
				.and(i -> i.eq(Account::getDepositType, "2").or().eq(Account::getDepositType, "3")) // 存款类型为 "2" 或 "3"
				.setSql("interest = ROUND(balance*DATEDIFF(fix_end_time, fix_begin_time)*rate/360,2)");
		int update = accountMapper.update(null, updateWrapper);
		return update;
	}

	//派息
	@GetMapping("/giveMoney")
	public int giveMoney() {
		LambdaUpdateWrapper<Account> updateWrapper = Wrappers.lambdaUpdate();
		Calendar instance = Calendar.getInstance();
		updateWrapper
				.eq(Account::getStatus, "1")
				.gt(Account::getInterest, 0)
				.setSql("balance = balance + interest")
				.set(Account::getBalanceUpdate, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(instance.getTime()))
				.setSql("balance_update = NOW()");
		int update = accountMapper.update(null, updateWrapper);
		return update;
	}

	//记录派息流水
	@GetMapping("/flowRecord")
	public int flowRecord() {
		int count = 0;
		//全部利息大于0的用户
		LambdaQueryWrapper<Account> queryWrapper = Wrappers.lambdaQuery();
		queryWrapper
				.eq(Account::getStatus, "1")
				.gt(Account::getInterest, 0);
		List<Account> accounts = accountMapper.selectList(queryWrapper);
		for (Account account : accounts) {
			Translog translog = new Translog();
			translog.setUserName(feign.queryClientById01(Integer.parseInt(account.getUserId())));
			translog.setAmount(String.valueOf(account.getInterest()));
			translog.setCounterPart(account.getNo());

			translog.setSeqNo("IST" + GetRandomNo.getRandomNo(11));
			translog.setTransType("4");
			Calendar instance = Calendar.getInstance();
			translog.setTransTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(instance.getTime()));
			translog.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(instance.getTime()));
			int insert = translogMapper.insert(translog);
			count += insert;
		}
		return count;
	}
}
