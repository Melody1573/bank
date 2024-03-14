package com.banksys.accountsys.component.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 功能：公共类
 * 作者：Luo。
 * 日期：2024/1/31 10:05
 */
@Data
public class Base {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField(exist = false)
    private Integer pageNo = 1;
    @TableField(exist = false)
    private Integer size = 10;
}
