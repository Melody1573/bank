package com.banksys.accountsys.service;

import com.banksys.accountsys.dao.InterestdicMapper;
import com.banksys.accountsys.model.Interestdic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 功能：
 * 作者：Luo。
 * 日期：2024/2/26 15:13
 */
@Service
@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED)
public class InterestdicService {

    @Autowired
    private InterestdicMapper interestdicMapper;

    public List<Interestdic> queryInterestdic() {
        List<Interestdic> interestdics = interestdicMapper.selectList(null);
        return interestdics;
    }
}
