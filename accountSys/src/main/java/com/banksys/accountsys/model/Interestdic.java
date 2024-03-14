package com.banksys.accountsys.model;

import com.banksys.accountsys.component.model.Base;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 功能：
 * 作者：Luo。
 * 日期：2024/2/26 9:49
 */
@Data
public class Interestdic extends Base {
    private String depositType;
    private String depositName;
    private BigDecimal interest;
    private String transfer;
    private String createTime;
}
