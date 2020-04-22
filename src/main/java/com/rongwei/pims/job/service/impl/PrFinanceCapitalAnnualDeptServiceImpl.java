package com.rongwei.pims.job.service.impl;

import com.rongwei.pims.job.dao.PrFinanceCapitalAnnualDeptSubMapper;
import com.rongwei.pims.job.domain.PrFinanceCapitalAnnualDeptVO;
import com.rongwei.pims.job.service.PrFinanceCapitalAnnualDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
@Service
public class PrFinanceCapitalAnnualDeptServiceImpl implements PrFinanceCapitalAnnualDeptService {
    @Autowired
    private PrFinanceCapitalAnnualDeptSubMapper subMapper;

    @Override
    public List<PrFinanceCapitalAnnualDeptVO> list(Map<String, Object> param) {
        return subMapper.list(param);
    }
}
