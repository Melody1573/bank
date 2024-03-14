package com.banksys.accountsys.component.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 功能：
 * 作者：Luo。
 * 日期：2024/2/18 17:23
 */
public class IsChineseFont {
    public static boolean isChineseFont(String str){
        // 定义一个正则表达式，匹配中文字符
        String regex = "[\\u4e00-\\u9fa5]";
        // 编译正则表达式
        Pattern pattern = Pattern.compile(regex);
        // 创建 Matcher 对象，用于匹配字符串
        Matcher matcher = pattern.matcher(str);
        // 查找字符串中是否存在中文字符
        if (matcher.find()) {
            return false;
        } else {
            return true;
        }
    }
}
