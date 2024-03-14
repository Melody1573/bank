package com.banksys.accountsys.component.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 功能：
 * 作者：Luo。
 * 日期：2024/1/19 14:05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultData {
    private Integer code;
    private String message;
    private Object data;

    public void setInfo(ResultCode resultCode){
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    public static ResultData success(Integer code,String message,Object data){
        ResultData resultData = new ResultData();
        resultData.setCode(code);
        resultData.setMessage(message);
        resultData.setData(data);
        return resultData;
    }

    public static ResultData success(Integer code,String message){
        ResultData resultData = new ResultData();
        resultData.setCode(code);
        resultData.setMessage(message);
        return resultData;
    }

    public static ResultData success(){
        ResultData resultData = new ResultData();
        resultData.setInfo(ResultCode.SUCCESS);
        return resultData;
    }

    public static ResultData success(String message){
        ResultData resultData = new ResultData();
        resultData.setCode(200);
        resultData.setMessage(message);
        return resultData;
    }

    public static ResultData success(Object data){
        ResultData resultData = new ResultData();
        resultData.setInfo(ResultCode.SUCCESS);
        resultData.setData(data);
        return resultData;
    }

    public static ResultData error(){
        ResultData resultData = new ResultData();
        resultData.setInfo(ResultCode.ERROR);
        return resultData;
    }

    public static ResultData error(String message){
        ResultData resultData = new ResultData();
        resultData.setCode(500);
        resultData.setMessage(message);
        return resultData;
    }

    public static ResultData error(Object data){
        ResultData resultData = new ResultData();
        resultData.setInfo(ResultCode.ERROR);
        resultData.setData(data);
        return resultData;
    }

    public static ResultData error(Integer code){
        ResultData resultData = new ResultData();
        resultData.setCode(code);
        return resultData;
    }

    public static ResultData error(Integer code,String message){
        ResultData resultData = new ResultData();
        resultData.setCode(code);
        resultData.setMessage(message);
        return resultData;
    }

    public static ResultData error(Integer code,String message,Object data){
        ResultData resultData = new ResultData();
        resultData.setCode(code);
        resultData.setMessage(message);
        resultData.setData(data);
        return resultData;
    }

    public static ResultData expire(){
        ResultData resultData = new ResultData();
        resultData.setInfo(ResultCode.EXPIRE);
        return resultData;
    }
}
