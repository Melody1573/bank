package com.banksys.accountsys.model.VO;

import com.banksys.accountsys.component.model.Base;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 功能：
 * 作者：Luo。
 * 日期：2024/3/6 9:20
 */
@Data
public class TranslogPageVO extends Base {
	private String seqNo;
	private String accNo;
	private String userName;
	private String amount;
	private String transTime;
	private String transType;
	private String counterPart;
	private String createTime;
	private String status;
}
