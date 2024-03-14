package com.banksys.accountsys.dao;

import com.banksys.accountsys.model.Translog;
import com.banksys.accountsys.model.VO.TranslogPageVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TranslogMapper extends BaseMapper<Translog> {
	List<TranslogPageVO> queryTransByPage(Map map);
	Integer queryTransCountByPage(Map map);
}
