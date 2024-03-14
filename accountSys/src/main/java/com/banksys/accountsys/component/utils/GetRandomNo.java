package com.banksys.accountsys.component.utils;

import java.util.Random;

/**
 * 功能：
 * 作者：Luo。
 * 日期：2024/2/26 10:18
 */
public class GetRandomNo {
    public static String getRandomNo(int number) {
        StringBuffer stringBuffer = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < number; i++) {
            int num = random.nextInt(10);
            stringBuffer.append(num);
        }
        return stringBuffer.toString();
    }
}
