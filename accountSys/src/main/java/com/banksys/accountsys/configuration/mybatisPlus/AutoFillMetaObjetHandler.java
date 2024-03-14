package com.banksys.accountsys.configuration.mybatisPlus;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

/**
 * 功能：在添加和修改时为数据初始化
 * 作者：Luo。
 * 日期：2024/1/18 17:51
 */
@Component
public class AutoFillMetaObjetHandler implements MetaObjectHandler {
    @Override
    public void insertFill(final MetaObject metaObject) {
        this.setFieldValByName("createBy", 1, metaObject);
        this.setFieldValByName("createTime", new Timestamp(System.currentTimeMillis()), metaObject);
    }

    @Override
    public void updateFill(final MetaObject metaObject) {
        this.setFieldValByName("updateBy", 1, metaObject);
        this.setFieldValByName("updateTime", new Timestamp(System.currentTimeMillis()), metaObject);
    }
}
