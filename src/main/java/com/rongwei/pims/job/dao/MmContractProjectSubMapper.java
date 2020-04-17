package com.rongwei.pims.job.dao;

import com.rongwei.pims.job.domain.MmContractProject;
import com.rongwei.pims.job.domain.MmProjectAnnualDailyExpensesVO;

import java.util.List;

public interface MmContractProjectSubMapper extends MmContractProjectMapper{
    int updateBatch(List<MmContractProject> contractProjectList);
}