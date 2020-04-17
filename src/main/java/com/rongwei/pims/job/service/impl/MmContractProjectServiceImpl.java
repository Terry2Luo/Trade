package com.rongwei.pims.job.service.impl;

import com.rongwei.pims.job.common.BigDecimalUtil;
import com.rongwei.pims.job.dao.MmContractProjectSubMapper;
import com.rongwei.pims.job.dao.MmProjectAnnualDailyExpensesSubMapper;
import com.rongwei.pims.job.domain.MmContractProject;
import com.rongwei.pims.job.domain.MmProjectAnnualDailyExpenses;
import com.rongwei.pims.job.domain.MmProjectAnnualDailyExpensesVO;
import com.rongwei.pims.job.service.MmContractProjectService;
import com.rongwei.pims.job.service.MmProjectAnnualDailyExpensesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class MmContractProjectServiceImpl
        implements
        MmContractProjectService {
    private static Logger logger = LoggerFactory.getLogger(MmContractProjectServiceImpl.class);

    @Autowired
    private MmContractProjectSubMapper contractProjectSubMapper;

    public int updateBatch(List<MmContractProject> contractProjectList){
        return contractProjectSubMapper.updateBatch(contractProjectList);

    }


}
