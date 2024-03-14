package com.banksys.accountsys.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.banksys.accountsys.component.feign.Feign;
import com.banksys.accountsys.component.result.ResultData;
import com.banksys.accountsys.component.utils.GetRandomNo;
import com.banksys.accountsys.component.utils.QueryByRedis;
import com.banksys.accountsys.component.utils.QueryIdByToken;
import com.banksys.accountsys.dao.AccountMapper;
import com.banksys.accountsys.dao.InterestdicMapper;
import com.banksys.accountsys.dao.TranslogMapper;
import com.banksys.accountsys.model.Account;
import com.banksys.accountsys.model.Interestdic;
import com.banksys.accountsys.model.Translog;
import com.banksys.accountsys.model.VO.AccountVO;
import com.banksys.accountsys.model.VO.TransferVO;
import com.banksys.accountsys.model.VO.WithdrawVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能：
 * 作者：Luo。
 * 日期：2024/2/26 10:06
 */
@Service
@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED)
public class AccountService extends ServiceImpl<AccountMapper, Account> {

	@Autowired
	private AccountMapper accountMapper;

	@Autowired
	private InterestdicMapper interestdicMapper;

	@Autowired
	private TranslogMapper translogMapper;

	@Autowired
	private TranslogService translogService;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	// @Autowired
	// private QueryIdByToken queryIdByToken;
	//
	// @Autowired
	// private QueryByRedis queryByRedis;

	@Autowired
	private Feign feign;

	public int openAccount(Account account) {
		// 安全判定:根据柜员id查询是否有该用户权限
		String result = feign.queryClientByCounterIdAndId(account.getUserId());
		if (!"200".equals(result)) {
			return -6;
		}

		// 根据id查询该客户是否被加入黑名单
		ResultData resultData = null;
		try {
			resultData = feign.queryClientById(Integer.parseInt(account.getUserId()));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			// 客户查询出现异常
			return -10;
		}
		if (resultData.getCode() != 200) {
			// 客户信息查询失败
			return -9;
		}
		JSONObject jsonObject = new JSONObject((Map<String, Object>) resultData.getData());
		String ifBlack = jsonObject.getString("ifBlack");
		if ("1".equals(ifBlack)){
			// 客户被加入黑名单无法开户
			return -11;
		}

		// 判断用户是否为基本户
		if ("1".equals(account.getAccountType())) {
			//判断用户是否已经是基本户
			Map map = new HashMap();
			map.put("account_type", 1);
			map.put("user_id", account.getUserId());
			List list = accountMapper.selectByMap(map);
			if (list.size() > 0) {
				//已存在该基本户
				return -1;
			}
		} else if ("2".equals(account.getAccountType())) {
			// 如果用户是一般户，判断用户是否有基本户
			// 根据userId查询该用户的基本户
			LambdaQueryWrapper<Account> queryWrapper = Wrappers.lambdaQuery();
			queryWrapper.eq(Account::getAccountType, "1");
			queryWrapper.eq(Account::getUserId, account.getUserId());
			List<Object> list = accountMapper.selectObjs(queryWrapper);
			if (list == null || list.size() == 0) {
				return -7;
			}
		}
		// 初始化数据
		//账号
		account.setNo("6217" + GetRandomNo.getRandomNo(12));
		//利率、定期时间(根据存款类型获取)
		Map map = new HashMap<String, Object>();
		map.put("deposit_type", account.getDepositType());
		List<Interestdic> interestDic = interestdicMapper.selectByMap(map);
		if (interestDic.size() == 1) {
			//设置利率
			account.setRate(interestDic.get(0).getInterest());
			if (!"1".equals(interestDic.get(0).getDepositType())) {
				//获取当前时间
				Calendar calendar = Calendar.getInstance();
				//设置开始时间为当前时间
				account.setFixBeginTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
				switch (interestDic.get(0).getDepositType()) {
					//定期一年
					case "2":
						calendar.add(Calendar.YEAR, 1);
						break;
					//定期五年
					case "3":
						calendar.add(Calendar.YEAR, 5);
						break;
				}
				//设置结束时间
				account.setFixEndTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
			}
		}
		//余额
		account.setBalance("0.00");
		//利息
		account.setInterest(new BigDecimal(0.00));
		//开户时间
		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());
		account.setCreateTime(time);
		//余额更新时间
		account.setBalanceUpdate(time);
		// 判断数据是否为空
		if (account.getUserId() == null || "".equals(account.getUserId())) {
			return -2;
		}
		if (account.getAccountType() == null || "".equals(account.getAccountType())) {
			return -3;
		}
		if (account.getDepositType() == null || "".equals(account.getDepositType())) {
			return -4;
		}
		if (account.getStatus() == null || "".equals(account.getStatus())) {
			return -5;
		}
		if (account.getAccountPassword() == null || "".equals(account.getAccountPassword())) {
			return -8;
		}
		// 密码加密
		account.setAccountPassword(bCryptPasswordEncoder.encode(account.getAccountPassword()));
		// 执行开户
		int insert = accountMapper.insert(account);
		return insert;
	}

	public List<AccountVO> queryAccountByPage(Map<String, Object> map) {
		List<AccountVO> list = accountMapper.queryAccountByPage(map);
		return list;
	}

	public int queryAccountCount(Map<String, Object> map) {
		int count = accountMapper.queryAccountCount(map);
		return count;
	}

	public List<Account> queryAccountByUserId(int id) {
		try {
			Map map = new HashMap();
			map.put("user_id", id);
			List list = accountMapper.selectByMap(map);
			return list;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 根据账号查询账户信息
	 *
	 * @param no
	 * @return
	 */
	public List<Account> queryAccountByNo(String no) {
		try {
			Map map = new HashMap();
			map.put("no", no);
			List list = accountMapper.selectByMap(map);
			return list;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 存款
	 */
	public int depositMoney(Map<String, String> info) {
		// 校验拿来的值
		String no = info.get("no");
		String moneyStr = info.get("money");
		if (no == null || "".equals(no)) {
			return -1;
		}
		BigDecimal money = null;
		try {
			money = new BigDecimal(moneyStr);
		} catch (Exception e) {
			return -2;
		}
		if (money == null) {
			return -3;
		}
		BigDecimal min = new BigDecimal(0.01);
		BigDecimal max = new BigDecimal(999999999999999999999999999999999999999999999999999999999999999.55);
		int result = money.compareTo(max);
		if (result < 0) {
			result = money.compareTo(min);
			if (result <= 0) {
				//值太小
				return -5;
			}
		} else {
			//值超出了范围
			return -4;
		}
		List<Account> accounts = queryAccountByNo(no);
		if (accounts == null || accounts.size() == 0) {
			//用户异常
			return -6;
		}
		if ("2".equals(accounts.get(0).getStatus())) {
			//账户异常
			return -7;
		}

		//存款
		int i = -100;
		Calendar calendar = Calendar.getInstance();
		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
		LambdaUpdateWrapper<Account> updateWrapper = Wrappers.lambdaUpdate();
		updateWrapper.eq(Account::getNo, no);
		updateWrapper.setSql("balance = balance + " + money)
				.set(Account::getBalanceUpdate, time);
		Translog translog = new Translog();
		translog.setSeqNo("DEP" + GetRandomNo.getRandomNo(11));
		translog.setAccNo(no);
		//根据userId查询用户姓名
		// String userName = feign.queryClientById01(Integer.parseInt(accounts.get(0).getUserId()));
		// 根据id查询该客户是否被加入黑名单
		ResultData resultData = null;
		try {
			resultData = feign.queryClientById(Integer.parseInt(accounts.get(0).getUserId()));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			// 客户查询出现异常
			return -13;
		}
		if (resultData.getCode() != 200) {
			// 客户信息查询失败
			return -14;
		}
		JSONObject jsonObject = new JSONObject((Map<String, Object>) resultData.getData());
		String ifBlack = jsonObject.getString("ifBlack");
		if ("1".equals(ifBlack)){
			// 客户被加入黑名单无法开户
			return -15;
		}

		String userName = jsonObject.getString("userName");
		translog.setUserName(userName);
		translog.setAmount(String.valueOf(money));
		translog.setTransTime(time);
		translog.setTransType("1");
		translog.setCreateTime(time);
		//活期存款
		if ("1".equals(accounts.get(0).getDepositType())) {
			//存款
			if (!"1".equals(accounts.get(0).getDepositFlag())) {
				updateWrapper.set(Account::getDepositFlag, 1);
			}
			try {
				i = accountMapper.update(null, updateWrapper);
			} catch (DataIntegrityViolationException e) {
				return -12;
			}
			//添加记录
			if (i <= 0) {
				//存款异常
				return -8;
			}
			try {
				i = translogMapper.insert(translog);
			} catch (DataIntegrityViolationException e) {
				return -12;
			}
		} else {
			//定期存款
			//存款
			updateWrapper.set(Account::getFixBeginTime, time);
			if ("2".equals(accounts.get(0).getDepositType())) {
				calendar.add(Calendar.YEAR, 1);
			} else if ("3".equals(accounts.get(0).getDepositType())) {
				calendar.add(Calendar.YEAR, 5);
			} else {
				return -10;
			}
			time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
			updateWrapper.set(Account::getFixEndTime, time);
			if ("1".equals(accounts.get(0).getDepositFlag())) {
				return -11;
			}
			updateWrapper.set(Account::getDepositFlag, 1);
			try {
				i = accountMapper.update(null, updateWrapper);
			} catch (Exception e) {
				return -12;
			}
			//添加记录
			if (i <= 0) {
				//存款异常
				return -8;
			}
			try {
				i = translogMapper.insert(translog);
			} catch (DataIntegrityViolationException e) {
				return -12;
			}
		}
		return i;
	}

	public ResultData widthMoney(WithdrawVO withdrawVO) {
		// 校验拿来的值
		String no = withdrawVO.getNo();
		String moneyStr = withdrawVO.getMoney();
		if (no == null || "".equals(no)) {
			return ResultData.error("-1");
		}
		BigDecimal money = null;
		try {
			money = new BigDecimal(moneyStr);
		} catch (Exception e) {
			return ResultData.error("-2");
		}
		if (money == null) {
			return ResultData.error("-3");
		}
		BigDecimal min = new BigDecimal(0.01);
		BigDecimal max = new BigDecimal(50000);
		int result = money.compareTo(max);
		if (result <= 0) {
			result = money.compareTo(min);
			if (result <= 0) {
				//值太小
				return ResultData.error("-5");
			}
		} else {
			//值超出了范围
			return ResultData.error("-4");
		}
		List<Account> accounts = queryAccountByNo(no);
		if (accounts == null || accounts.size() == 0) {
			//用户异常
			return ResultData.error("-6");
		}
		if ("2".equals(accounts.get(0).getStatus())) {
			//账户异常
			return ResultData.error("-7");
		}

		// 根据id查询该客户是否被加入黑名单
		ResultData resultData = null;
		try {
			resultData = feign.queryClientById(Integer.parseInt(accounts.get(0).getUserId()));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			// 客户查询出现异常
			return ResultData.error("-15");
		}
		if (resultData.getCode() != 200) {
			// 客户信息查询失败
			return ResultData.error("-16");
		}
		JSONObject jsonObject = new JSONObject((Map<String, Object>) resultData.getData());
		String ifBlack = jsonObject.getString("ifBlack");
		if ("1".equals(ifBlack)){
			// 客户被加入黑名单无法取款
			return ResultData.error("-17");
		}

		// 取款
		// 查询今天该有账户的取款金额
		String outMondy = null;
		try {
			outMondy = translogService.queryUserWithdrawMoneyByTodayAndNo(no);
		} catch (Exception e) {
			return ResultData.error("-8");
		}
		if (outMondy != null) {
			// 使用50000-今天的取款金额-本次取款金额>0表示今天还能取款
			BigDecimal todayMondy = new BigDecimal("50000");
			BigDecimal hasMoney = todayMondy.subtract(new BigDecimal(outMondy));
			BigDecimal endMoney = hasMoney.subtract(money);
			BigDecimal zero = new BigDecimal("0");
			if (endMoney.compareTo(zero) < 0) {
				// 返回今天还能取多少钱
				return ResultData.error(hasMoney);
			}
		}
		// 查询当前账户里的存款够不够取
		BigDecimal accountMoney = new BigDecimal(accounts.get(0).getBalance());
		if (accountMoney.compareTo(money) < 0) {
			//钱不够取
			return ResultData.error("-12");
		}
		//根据userId查询用户姓名
		String userName = feign.queryClientById01(Integer.parseInt(accounts.get(0).getUserId()));
		//构建update对象
		Calendar calendar = Calendar.getInstance();
		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
		LambdaUpdateWrapper<Account> updateWrapper = Wrappers.lambdaUpdate();
		updateWrapper.eq(Account::getNo, no);
		updateWrapper.setSql("balance = balance - " + money)
				.set(Account::getBalanceUpdate, time);
		Translog translog = new Translog();
		translog.setSeqNo("WTD" + GetRandomNo.getRandomNo(11));
		translog.setAccNo(no);
		translog.setUserName(userName);
		translog.setAmount(String.valueOf(money));
		translog.setTransTime(time);
		translog.setTransType("2");
		translog.setCreateTime(time);
		// 如果是活期取款
		int i = -100;
		if ("1".equals(accounts.get(0).getDepositType())) {
			try {
				i = accountMapper.update(null, updateWrapper);
			} catch (Exception e) {
				return ResultData.error("-9");
			}
			//添加记录
			if (i <= 0) {
				//存款异常
				return ResultData.error("-10");
			}
			try {
				i = translogMapper.insert(translog);
				i = 1;
			} catch (Exception e) {
				return ResultData.error("-11");
			}
		} else if ("2".equals(accounts.get(0).getDepositType()) || "3".equals(accounts.get(0).getDepositType())) {
			// 获取到期时间
			String fixEndTime = accounts.get(0).getFixEndTime();
			// 判断今天是否大于到期时间
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			LocalDateTime EndTime = LocalDateTime.parse(fixEndTime, formatter);
			if (!EndTime.isBefore(now)) {
				return ResultData.error("-14");
			}
			try {
				i = accountMapper.update(null, updateWrapper);
			} catch (Exception e) {
				return ResultData.error("-9");
			}
			//添加记录
			if (i <= 0) {
				//存款异常
				return ResultData.error("-10");
			}
			try {
				i = translogMapper.insert(translog);
				i = 1;
			} catch (Exception e) {
				return ResultData.error("-11");
			}
		} else {
			//账户存款形式未知
			return ResultData.error("-13");
		}
		return ResultData.error(i + "");
	}

	public String transfer(TransferVO transferVO) {
		// 校验拿来的值
		String inputNo = transferVO.getInputNo();
		String outputNo = transferVO.getOutputNo();
		String moneyStr = transferVO.getMoney();
		if (inputNo == null || "".equals(inputNo) || outputNo == null || "".equals(outputNo)) {
			return "-1";
		}
		BigDecimal money = null;
		try {
			money = new BigDecimal(moneyStr);
		} catch (Exception e) {
			return "-2";
		}
		if (money == null) {
			return "-3";
		}

		// 根据id查询该客户是否被加入黑名单
		List<Account> accounts1 = queryAccountByNo(inputNo);
		List<Account> accounts2 = queryAccountByNo(outputNo);
		ResultData resultData1 = null;
		ResultData resultData2 = null;
		try {
			resultData1 = feign.queryClientById(Integer.parseInt(accounts1.get(0).getUserId()));
			resultData2 = feign.queryClientById(Integer.parseInt(accounts2.get(0).getUserId()));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			// 客户查询出现异常
			return "-17";
		}
		if (resultData1.getCode() != 200 || resultData2.getCode() != 200) {
			// 客户信息查询失败
			return "-18";
		}
		JSONObject jsonObject1 = new JSONObject((Map<String, Object>) resultData1.getData());
		JSONObject jsonObject2 = new JSONObject((Map<String, Object>) resultData2.getData());
		String ifBlack1 = jsonObject1.getString("ifBlack");
		String ifBlack2 = jsonObject2.getString("ifBlack");
		if ("1".equals(ifBlack1) || "1".equals(ifBlack2)){
			// 客户被加入黑名单无法开户
			return "-19";
		}

		BigDecimal min = new BigDecimal(0.01);
		BigDecimal max = new BigDecimal(999999999999999999999999999999999999999999999999999999999999999.99);
		int result = money.compareTo(max);
		if (result < 0) {
			result = money.compareTo(min);
			if (result <= 0) {
				//值太小
				return "-5";
			}
		} else {
			//值超出了范围
			return "-4";
		}
		// 判断俩账号是否相同
		if (inputNo.equals(outputNo)) {
			return "-15";
		}
		// 校验俩个用户账户状态是否正常
		try {
			if ("2".equals(accounts1.get(0).getStatus())) {
				// 入账账户异常
				return "-6";
			}
			if ("2".equals(accounts2.get(0).getStatus())) {
				// 出账账户异常
				return "-7";
			}
		} catch (Exception e) {
			e.printStackTrace();
			// 未知账户
			return "-10";
		}
		// 校验密码是否正确
		if (transferVO.getPassword() == null || "".equals(transferVO.getPassword()) || transferVO.getPassword() == null || "".equals(transferVO.getPassword())) {
			return "-16";
		}
		boolean matches = bCryptPasswordEncoder.matches(transferVO.getPassword(), accounts2.get(0).getAccountPassword());
		if (!matches) {
			return "-8";
		}
		// 校验出账账户余额是否充足
		BigDecimal accountMoney = new BigDecimal(accounts2.get(0).getBalance());
		if (accountMoney.compareTo(money) < 0) {
			//钱不够取
			return "-9";
		}
		// 判断入账用户是否定期账户
		if (!"1".equals(accounts1.get(0).getDepositType())) {
			// 定期账户无法入账
			return "-11";
		}

		// 根据账户的userId查询用户信息
		// String name01 = feign.queryClientById01(Integer.parseInt(accounts1.get(0).getUserId()));
		// String name02 = feign.queryClientById01(Integer.parseInt(accounts2.get(0).getUserId()));
		String name01 = jsonObject1.getString("userName");
		String name02 = jsonObject2.getString("userName");

		// 校验入账账户是否还能存入金额
		//构建update对象
		Calendar calendar = Calendar.getInstance();
		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
		LambdaUpdateWrapper<Account> updateWrapper1 = Wrappers.lambdaUpdate();
		updateWrapper1.eq(Account::getNo, accounts1.get(0).getNo());
		updateWrapper1.setSql("balance = balance + " + money)
				.set(Account::getBalanceUpdate, time);
		LambdaUpdateWrapper<Account> updateWrapper2 = Wrappers.lambdaUpdate();
		updateWrapper2.eq(Account::getNo, accounts2.get(0).getNo());
		updateWrapper2.setSql("balance = balance - " + money)
				.set(Account::getBalanceUpdate, time);
		Translog translog = new Translog();
		translog.setSeqNo("TSF" + GetRandomNo.getRandomNo(11));
		// 判断当前是入账还是出账
		if ("1".equals(transferVO.getType())) {
			translog.setAccNo(accounts2.get(0).getNo());
			translog.setUserName(name02);
			translog.setCounterPart(accounts1.get(0).getNo());
		} else if ("2".equals(transferVO.getType())) {
			translog.setAccNo(accounts1.get(0).getNo());
			translog.setUserName(name01);
			translog.setCounterPart(accounts2.get(0).getNo());
		} else {
			// 业务信息未知
			return "-12";
		}
		translog.setAmount(String.valueOf(money));
		translog.setTransTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
		translog.setTransType("3");
		translog.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
		// 判断是不是初次入账
		if (!"1".equals(accounts1.get(0).getDepositFlag())) {
			updateWrapper1.set(Account::getDepositFlag, 1);
		}
		// 判断出账用户是否定期账户
		int i = -100;
		if ("1".equals(accounts2.get(0).getDepositType())) {
			// 转账
			try {
				i = accountMapper.update(null, updateWrapper1);
				i = accountMapper.update(null, updateWrapper2);
			} catch (DataIntegrityViolationException e) {
				e.printStackTrace();
				return "-14";
			}
			// 添加流水信息
			i = translogMapper.insert(translog);
		} else if ("2".equals(accounts2.get(0).getDepositType()) || "3".equals(accounts2.get(0).getDepositType())) {
			// 获取到期时间
			String fixEndTime = accounts2.get(0).getFixEndTime();
			// 判断今天是否大于到期时间
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			LocalDateTime EndTime = LocalDateTime.parse(fixEndTime, formatter);
			if (!EndTime.isBefore(now)) {
				// 定期存款还没有到期
				return "-13";
			}
			// 转账
			try {
				i = accountMapper.update(null, updateWrapper1);
				i = accountMapper.update(null, updateWrapper2);
			} catch (DataIntegrityViolationException e) {
				e.printStackTrace();
				return "-14";
			}
			// 添加流水信息
			i = translogMapper.insert(translog);
		}
		return String.valueOf(i);
	}
}
