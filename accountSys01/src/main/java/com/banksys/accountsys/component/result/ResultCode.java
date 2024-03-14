package com.banksys.accountsys.component.result;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 功能：
 * 作者：Luo。
 * 日期：2024/1/19 14:56
 */
@AllArgsConstructor
@NoArgsConstructor
public enum ResultCode {
    SUCCESS(200,"调用接口成功"),
    ERROR(500,"调用接口失败"),
    EXPIRE(501,"token失效");

    private Integer code;
    private String message;

    public Integer getCode(){
        return code;
    }

    public String getMessage(){
        return message;
    }
}
