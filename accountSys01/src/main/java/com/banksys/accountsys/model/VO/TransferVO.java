package com.banksys.accountsys.model.VO;

import lombok.Data;

/**
 * 功能：
 * 作者：Luo。
 * 日期：2024/3/4 14:38
 */
@Data
public class TransferVO{
	private String money;
	private String password;
	private String inputNo;
	private String outputNo;
	private String type;
}
