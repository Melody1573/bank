package com.banksys.accountsys.model.VO;

import com.banksys.accountsys.component.model.Base;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 功能：
 * 作者：Luo。
 * 日期：2024/2/27 10:18
 */
@Data
public class AccountVO extends Base {
    private String no;
    private String userName;
    private String accountType;
    private String depositType;
    private String depositName;
    private String transfer;
    private String balance;
    private BigDecimal interest;
    private String createTime;
}
