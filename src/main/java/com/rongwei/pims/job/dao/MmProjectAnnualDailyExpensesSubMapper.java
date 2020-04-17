package com.rongwei.pims.job.dao;

import com.rongwei.pims.job.domain.MmProjectAnnualDailyExpenses;
import com.rongwei.pims.job.domain.MmProjectAnnualDailyExpensesVO;

import java.util.List;
import java.util.Map;

public interface MmProjectAnnualDailyExpensesSubMapper extends MmProjectAnnualDailyExpensesMapper{
    List<MmProjectAnnualDailyExpensesVO> listProjectContractInfo(Map<String,Object> map);

    void deleteByYearAndCode(Map<String,Object> map);
    int insertBatch(List<MmProjectAnnualDailyExpensesVO> dailyExpensesVO);
}