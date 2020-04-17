package com.rongwei.pims.job.service;

import com.rongwei.pims.job.domain.MmContractProject;

import java.util.List;

public interface MmContractProjectService {
    int updateBatch(List<MmContractProject> contractProjectList);
}
