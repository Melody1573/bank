package com.banksys.accountsys.controller;

import com.banksys.accountsys.component.result.ResultData;
import com.banksys.accountsys.component.utils.QueryIdByToken;
import com.banksys.accountsys.model.Account;
import com.banksys.accountsys.model.VO.AccountVO;
import com.banksys.accountsys.model.VO.TransferVO;
import com.banksys.accountsys.model.VO.WithdrawVO;
import com.banksys.accountsys.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 功能：
 * 作者：Luo。
 * 日期：2024/2/26 10:06
 */

@RestController
@RequestMapping("/Account")
public class AccountController {
	@Autowired
	private AccountService accountService;

	@Autowired
	private QueryIdByToken queryIdByToken;

	/**
	 * 开户
	 * @param account
	 * @return
	 */
	@PostMapping("/openAccount")
	public ResultData openAccount(@RequestBody Account account) {
		int i = 0;
		try {
			i = accountService.openAccount(account);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultData.error("未知异常");
		}
		if (i > 0) {
			return ResultData.success("开户成功");
		}
		if (i == -1) {
			return ResultData.error("该用户已存在基本户");
		}
		if (i == -2) {
			return ResultData.error("开户用户异常");
		}
		if (i == -3) {
			return ResultData.error("开户类型异常");
		}
		if (i == -4) {
			return ResultData.error("存款类型异常");
		}
		if (i == -5) {
			return ResultData.error("开户账户状态异常");
		}
		if (i == -6) {
			return ResultData.error("柜员权限不能为该用户开户");
		}
		if (i == -7) {
			return ResultData.error("该用户还没有基本户,不能开一般户");
		}
		if (i == -8) {
			return ResultData.error("用户密码不能为空");
		}
		if (i == -9) {
			return ResultData.error("客户信息查询失败");
		}
		if (i == -10) {
			return ResultData.error("客户查询出现异常");
		}
		if (i == -11) {
			return ResultData.error("客户被加入黑名单无法开户");
		}
		return ResultData.error("未知异常");
	}

	/**
	 * 分页查询账户
	 *
	 * @param map
	 * @return
	 */
	@PostMapping("/queryAccountByPage")
	public ResultData queryAccountByPage(@RequestBody Map map) {
		Integer id = queryIdByToken.queryIdByToken();
		map.put("id", String.valueOf(id));
		Integer pageNo = (Integer) map.get("pageNo");
		Integer size = (Integer) map.get("size");
		map.put("pageNo", (pageNo - 1) * size);
		String userName = (String) map.get("userName");
		String no = (String) map.get("no");
		String accountType = (String) map.get("accountType");
		String depositType = (String) map.get("depositType");
		if ("".equals(userName)) {
			map.put("userName", null);
		}
		if ("".equals(no)) {
			map.put("no", null);
		}
		if ("".equals(accountType)) {
			map.put("accountType", null);
		}
		if ("".equals(depositType)) {
			map.put("depositType", null);
		}
		List<AccountVO> accountVOS = accountService.queryAccountByPage(map);
		String count = String.valueOf(accountService.queryAccountCount(map));
		return ResultData.success(200, count, accountVOS);
	}

	/**
	 * 根据用户id查询账户
	 *
	 * @param id
	 * @return
	 */
	@GetMapping("/queryAccountByUserId")
	public ResultData queryAccountByUserId(int id) {
		List<Account> accounts = accountService.queryAccountByUserId(id);
		if (accounts != null) {
			return ResultData.success(accounts);
		}
		return ResultData.error("未知错误");
	}

	/**
	 * 根据卡号查询账户
	 *
	 * @param no
	 * @return
	 */
	@GetMapping("/queryAccountByNo")
	public ResultData queryAccountByNo(String no) {
		List<Account> accounts = accountService.queryAccountByNo(no);
		if (accounts != null) {
			return ResultData.success(accounts);
		}
		return ResultData.error("未知错误");
	}

	/**
	 * 存款
	 *
	 * @param info
	 * @return
	 */
	@PostMapping("/depositMoney")
	public ResultData depositMoney(@RequestBody Map<String, String> info) {
		int i = accountService.depositMoney(info);
		if (i > 0) {
			return ResultData.success("添加成功");
		}
		switch (i) {
			case -1:
				return ResultData.error("传入账号为空");
			case -2:
				return ResultData.error("传入金额异常");
			case -3:
				return ResultData.error("传入金额为空");
			case -4:
				return ResultData.error("传入金额太大");
			case -5:
				return ResultData.error("传入金额太小");
			case -6:
				return ResultData.error("当前用户异常");
			case -7:
				return ResultData.error("当前账户异常");
			case -8:
				return ResultData.error("存款出现错误");
			case -9:
				return ResultData.error("当前用户名称查询出现异常");
			case -10:
				return ResultData.error("用户存款类型出现异常");
			case -11:
				return ResultData.error("当前为定期用户并已存款");
			case -12:
				return ResultData.error("存入值超出数据库范围");
			case -13:
				return ResultData.error("客户查询出现异常");
			case -14:
				return ResultData.error("客户信息查询失败");
			case -15:
				return ResultData.error("客户被加入黑名单无法存款");

		}
		return ResultData.error(i, "未知异常");
	}

	/**
	 * 取款
	 */
	@PostMapping("/withdrawMoney")
	public ResultData widthMoney(@RequestBody WithdrawVO withdrawVO) {
		ResultData resultData = accountService.widthMoney(withdrawVO);
		if (resultData.getData() != null) {
			return ResultData.error(5001, "取款限额", resultData.getData());
		}
		if (resultData.getMessage().equals("1")) {
			return ResultData.success("添加成功");
		}
		switch (resultData.getMessage()) {
			case "-1":
				return ResultData.error("传入账号为空");
			case "-2":
				return ResultData.error("传入金额异常");
			case "-3":
				return ResultData.error("传入金额为空");
			case "-4":
				return ResultData.error("传入金额太大");
			case "-5":
				return ResultData.error("传入金额太小");
			case "-6":
				return ResultData.error("用户异常");
			case "-7":
				return ResultData.error("账户异常");
			case "-8":
				return ResultData.error("今日已取金额异常");
			case "-9":
				return ResultData.error("取款异常");
			case "-10":
				return ResultData.error("取款异常");
			case "-11":
				return ResultData.error("流水记录添加异常");
			case "-12":
				return ResultData.error("当前余额不足" + withdrawVO.getMoney());
			case "-13":
				return ResultData.error("未知的存款形式");
			case "-14":
				return ResultData.error("存款定期时间未到");
			case "-15":
				return ResultData.error("客户查询出现异常");
			case "-16":
				return ResultData.error("客户信息查询失败");
			case "-17":
				return ResultData.error("客户被加入黑名单无法取款");
		}
		return ResultData.error("未知错误");
	}

	/**
	 * 转账
	 */
	@PostMapping("/transfer")
	public ResultData transfer(@RequestBody TransferVO transferVO) {
		String transfer = accountService.transfer(transferVO);
		if (Integer.parseInt(transfer) > 0) {
			return ResultData.success("转账成功");
		}
		switch (transfer) {
			case "-1":
				return ResultData.error("传入账号为空");
			case "-2":
				return ResultData.error("传入金额异常");
			case "-3":
				return ResultData.error("传入金额为空");
			case "-4":
				return ResultData.error("传入金额太大");
			case "-5":
				return ResultData.error("传入金额太小");
			case "-6":
				return ResultData.error("入账账户异常");
			case "-7":
				return ResultData.error("出账账户异常");
			case "-8":
				return ResultData.error("出账账户密码错误");
			case "-9":
				return ResultData.error("出账余额不足");
			case "-10":
				return ResultData.error("未知账户");
			case "-11":
				return ResultData.error("定期账户无法入账");
			case "-12":
				return ResultData.error("业务信息未知");
			case "-13":
				return ResultData.error("定期存款还没有到期");
			case "-14":
				return ResultData.error("存入值超出数据库范围");
			case "-15":
				return ResultData.error("转入转出账号不能相同");
			case "-16":
				return ResultData.error("密码不能为空");
			case "-17":
				return ResultData.error("客户查询出现异常");
			case "-18":
				return ResultData.error("客户信息查询失败");
			case "-19":
				return ResultData.error("客户被加入黑名单无法转账");
		}
		return ResultData.error("未知异常");
	}
}
