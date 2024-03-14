package com.banksys.accountsys.model;

import com.banksys.accountsys.component.model.Base;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 功能：
 * 作者：Luo。
 * 日期：2024/2/26 9:48
 */
@Data
public class Account extends Base {
    private String no;
    private String accountType;
    private String depositType;
    private BigDecimal rate;
    private String userId;
    private String status;
    private String balance;
    private String balanceUpdate;
    private BigDecimal interest;
    private String fixBeginTime;
    private String fixEndTime;
    private String createTime;
    private String depositFlag;
    private String accountPassword;
}

