package com.banksys.accountsys.model;

import com.banksys.accountsys.component.model.Base;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 功能：
 * 作者：Luo。
 * 日期：2024/2/28 17:18
 */
@Data
public class Translog extends Base {
	private String seqNo;
	private String accNo;
	private String userName;
	private String amount;
	private String transTime;
	private String transType;
	private String counterPart;
	private String createTime;
}
