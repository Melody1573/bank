package com.banksys.accountsys.controller;

import com.banksys.accountsys.component.result.ResultData;
import com.banksys.accountsys.model.Interestdic;
import com.banksys.accountsys.service.InterestdicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 功能：
 * 作者：Luo。
 * 日期：2024/2/26 15:13
 */
@RestController
@RequestMapping("/Account")
public class InterestdicController {

    @Autowired
    private InterestdicService interestdicService;

    @GetMapping("/queryInterestdic")
    public ResultData queryInterestdic() {
        List<Interestdic> interestdics = interestdicService.queryInterestdic();
        return ResultData.success(interestdics);
    }
}
