package com.banksys.accountsys.service;

import com.banksys.accountsys.component.result.ResultData;
import com.banksys.accountsys.component.utils.QueryIdByToken;
import com.banksys.accountsys.dao.TranslogMapper;
import com.banksys.accountsys.model.Translog;
import com.banksys.accountsys.model.VO.TranslogPageVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 功能：
 * 作者：Luo。
 * 日期：2024/2/26 10:06
 */
@Service
@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED)
public class TranslogService extends ServiceImpl<TranslogMapper, Translog> {

	@Autowired
	private TranslogMapper translogMapper;

	@Autowired
	private QueryIdByToken queryIdByToken;

	public String queryUserWithdrawMoneyByTodayAndNo(String no) {
		// 查询今天取了多少钱了
		QueryWrapper<Translog> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("acc_no", no)
				.eq("trans_type", "2")
				.apply("DATE(trans_time) = CURDATE()");
		queryWrapper.select("SUM(amount) AS sumMoney");
		List<Map<String, Object>> maps = translogMapper.selectMaps(queryWrapper);
		// 检查查询结果是否非空
		if (maps != null && maps.size() > 0 && maps.get(0) != null && maps.get(0).get("sumMoney") != null) {
			// 获取查询结果中的第一条记录的 sumMoney 值
			System.out.println(maps.get(0));
			Object sumMoney1 = maps.get(0).get("sumMoney");
			if (sumMoney1 != null) {
				// 将 sumMoney 值转换为需要的类型（例如 BigDecimal）
				return sumMoney1.toString();
			} else {
				// 如果查询结果中 sumMoney 为 null，则进行相应处理
				return null;
			}
		} else {
			// 如果查询结果为空，则进行相应处理
			return null;
		}
	}

	public ResultData queryTransByPage(Map<String, Object> map) {
		Integer id = queryIdByToken.queryIdByToken();
		if (id == null) {
			return ResultData.error("-3");
		}
		map.put("id", String.valueOf(id));
		Integer pageNo = 0;
		Integer size = 10;
		try {
			if (map.get("size") != null) {
				size = (Integer) map.get("size");
				map.put("size", size);
			}
			if (map.get("pageNo") != null) {
				pageNo = (Integer) map.get("pageNo");
				map.put("pageNo", (pageNo - 1) * size);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return ResultData.error("-1");
		}
		String seqNo = (String) map.get("seqNo");
		String no = (String) map.get("no");
		String transType = (String) map.get("transType");
		map.put("seqNo", "".equals(seqNo) || seqNo == null ? null : seqNo.trim());
		map.put("no", "".equals(no) || no == null ? null : no.trim());
		map.put("transType", "".equals(transType) || transType == null ? null : transType.trim());
		List<TranslogPageVO> translogPageVOList = null;
		int count = 0;
		try {
			translogPageVOList = translogMapper.queryTransByPage(map);
			count = translogMapper.queryTransCountByPage(map);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultData.error(500, "-2", e);
		}
		return ResultData.success(200,count + "",translogPageVOList);
	}
}
