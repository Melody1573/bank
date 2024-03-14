package com.banksys.accountsys.dao;

import com.banksys.accountsys.model.Account;
import com.banksys.accountsys.model.VO.AccountVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface AccountMapper extends BaseMapper<Account> {

    List<AccountVO> queryAccountByPage(Map map);

    int queryAccountCount(Map map);
}
