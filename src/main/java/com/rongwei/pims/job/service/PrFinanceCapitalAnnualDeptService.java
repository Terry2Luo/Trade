package com.rongwei.pims.job.service;

import com.rongwei.pims.job.domain.PrFinanceCapitalAnnualDeptVO;

import java.util.List;
import java.util.Map;

public interface PrFinanceCapitalAnnualDeptService {
    List<PrFinanceCapitalAnnualDeptVO> list(Map<String,Object> param);
}
