package com.banksys.accountsys.component.utils;

import java.util.Objects;

/**
 * 功能：
 * 作者：Luo。
 * 日期：2024/2/18 16:55
 */
public class NotNullUtil {
    public static Object isNotNull(Object o){
        if (o == null){
            throw new ProgramStoppedException("空指针异常");
        }
        if (o instanceof String){
            if (Objects.equals(o,"")) {
                throw new ProgramStoppedException("空指针异常");
            }
        }
        return null;
    }

}

class ProgramStoppedException extends RuntimeException {
    public ProgramStoppedException(String message) {
        super(message);
    }
}
