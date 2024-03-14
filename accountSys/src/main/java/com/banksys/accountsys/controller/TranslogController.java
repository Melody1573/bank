package com.banksys.accountsys.controller;

import com.banksys.accountsys.component.result.ResultData;
import com.banksys.accountsys.component.utils.QueryIdByToken;
import com.banksys.accountsys.model.VO.TranslogPageVO;
import com.banksys.accountsys.service.TranslogService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletResponse;

/**
 * 功能：
 * 作者：Luo。
 * 日期：2024/2/26 15:13
 */
@RestController
@RequestMapping("/Account")
public class TranslogController {

	@Autowired
	private TranslogService translogService;

	@Autowired
	private QueryIdByToken queryIdByToken;

	/**
	 * 分页条件查询流水信息
	 */
	@PostMapping("/queryTransByPage")
	public ResultData queryTransByPage(@RequestBody Map<String, Object> map) {
		ResultData resultData = translogService.queryTransByPage(map);
		if (resultData.getCode() == 200) {
			return resultData;
		}
		if (resultData.getCode() == 500) {
			switch (resultData.getMessage()) {
				case "-1":
					return ResultData.error("传入分页数据有误");
				case "-2":
					return ResultData.error("出现异常" + resultData.getData().toString());
				case "-3":
					return ResultData.error("当前柜员状态异常");
			}
		}
		return ResultData.error("未知异常");
	}

	// 转出到Excel中
	@PostMapping("/outExcel")
	public ResponseEntity<byte[]> outExcel(@RequestBody Map<String, Object> map) {
		// 获取数据
		ResultData resultData = translogService.queryTransByPage(map);
		if (resultData.getCode() != 200) {
			return null;
		}
		// 创建文件
		try {
			// 创建一个新的工作簿
			Workbook workbook = new XSSFWorkbook();
			// 创建一个工作表
			Sheet sheet = workbook.createSheet("Sheet1");
			// 创建表头行
			Row headerRow = sheet.createRow(0);

			// 创建表头单元格
			// 通过类获取对象的所有属性名称作为表头
			Class<TranslogPageVO> translogPageVOClass = TranslogPageVO.class;
			Field[] declaredFields = translogPageVOClass.getDeclaredFields();
			// for (int i = 0; i < declaredFields.length; i++) {
			// headerRow.createCell(i).setCellValue(declaredFields[i].getName());
			// }
			headerRow.createCell(0).setCellValue("交易账号");
			headerRow.createCell(1).setCellValue("用户账号");
			headerRow.createCell(2).setCellValue("用户名称");
			headerRow.createCell(3).setCellValue("交易金额");
			headerRow.createCell(4).setCellValue("交易时间");
			headerRow.createCell(5).setCellValue("交易类型");
			headerRow.createCell(6).setCellValue("对手账号");
			headerRow.createCell(7).setCellValue("创建时间");
			headerRow.createCell(8).setCellValue("账户状态");

			// 示例数据
			List<TranslogPageVO> list = (List<TranslogPageVO>) resultData.getData();

			// 将数据写入单元格
			int rowNum = 1;
			for (TranslogPageVO translogPageVO : list) {
				Row row = sheet.createRow(rowNum++);

				int colNum = 0;
				for (Field field : declaredFields) {
					field.setAccessible(true);
					try {
						Object value = field.get(translogPageVO);
						Cell cell = row.createCell(colNum++);
						if (value instanceof String) {
							if (colNum == 6){
								cell.setCellValue("1".equals((String) value) ? "存款" : "2".equals((String) value) ? "取款" : "3".equals((String) value) ? "转账" : "派息");
								continue;
							}
							if (colNum == 9){
								cell.setCellValue("1".equals((String) value) ? "正常" : "异常");
								continue;
							}
							cell.setCellValue((String) value);
						} else if (value instanceof BigDecimal) {
							cell.setCellValue(((BigDecimal) value).doubleValue());
						} // 可以根据需要添加其他数据类型的处理
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}

			Date date = new Date();
			String time = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(date);
			Integer id = queryIdByToken.queryIdByToken();
			// URL url = getClass().getResource("/");

			// 将工作簿内容写入文件
			FileOutputStream fileOut = new FileOutputStream("流水信息.xlsx");
			workbook.write(fileOut);
			fileOut.close();

			// 关闭工作簿
			workbook.close();
			System.out.println("Excel文件已成功创建！");

			// 读取Excel文件内容到字节数组中
			Path path = Paths.get("流水信息.xlsx");
			byte[] excelData = Files.readAllBytes(path);

			// 将Excel文件内容包装为ByteArrayResource
			// ByteArrayResource resource = new ByteArrayResource(excelData);

			// 构建响应头
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			//headers.setContentDispositionFormData("attachment", "流水信息.xlsx");
			//设置响应内容的类型和文件名
			ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(excelData, headers, HttpStatus.OK);
			// return ResultData.success(responseEntity);
			return responseEntity;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
