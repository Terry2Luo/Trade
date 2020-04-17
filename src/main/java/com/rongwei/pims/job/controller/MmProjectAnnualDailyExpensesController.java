package com.rongwei.pims.job.controller;

import com.rongwei.pims.job.common.BaseController;
import com.rongwei.pims.job.domain.MmProjectAnnualDailyExpenses;
import com.rongwei.pims.job.service.MmProjectAnnualDailyExpensesService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(value="经营项目年度日常费用")
@RequestMapping("/Mm/annualDailyExpenses")
@RestController

public class MmProjectAnnualDailyExpensesController extends BaseController {

	@Autowired
	MmProjectAnnualDailyExpensesService mmProjectAnnualDailyExpensesService;



	@ApiOperation(value="经营项目年度日常费用查询")
    @RequestMapping(value="/{id}", method = RequestMethod.PUT)
	public MmProjectAnnualDailyExpenses get(@PathVariable("id") Integer id){
		MmProjectAnnualDailyExpenses projectAnnualDailyExpenses = mmProjectAnnualDailyExpensesService.get(id);
		return projectAnnualDailyExpenses;
	}

	@RequestMapping(value="/indexPageDay",method = RequestMethod.GET)
	@ApiOperation(value="分包余额计算",notes="数据格式::：projectCode——经营项目编号（精确查询）")
	public void	calcPrjPurchaseAmount(@RequestParam(value = "year", required = true) Integer year
			,@RequestParam(value = "projectCode", required = false) String projectCode
	) throws Exception{
		mmProjectAnnualDailyExpensesService.calcPrjPurchaseAmount(year, projectCode);
	}




}
