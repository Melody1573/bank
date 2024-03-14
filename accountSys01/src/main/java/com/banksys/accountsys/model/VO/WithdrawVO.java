package com.banksys.accountsys.model.VO;

import com.banksys.accountsys.component.model.Base;
import lombok.Data;

/**
 * 功能：
 * 作者：Luo。
 * 日期：2024/3/1 14:48
 */
@Data
public class WithdrawVO extends Base {
	private String no;
	private String money;
}
