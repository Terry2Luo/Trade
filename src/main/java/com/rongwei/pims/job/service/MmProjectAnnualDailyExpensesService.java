package com.rongwei.pims.job.service;

import com.rongwei.pims.job.domain.MmProjectAnnualDailyExpenses;
import com.rongwei.pims.job.domain.MmProjectAnnualDailyExpensesVO;

import java.util.List;
import java.util.Map;

public interface MmProjectAnnualDailyExpensesService {
    MmProjectAnnualDailyExpenses get(Integer id);
    List<MmProjectAnnualDailyExpensesVO> listProjectContractInfo(Map<String,Object> map);

    void calcPrjPurchaseAmount(Integer year, String projectCode);

    void deleteByYearAndCode(Map<String,Object> map);

    int insertBatch(List<MmProjectAnnualDailyExpensesVO> dailyExpensesVO);
}
