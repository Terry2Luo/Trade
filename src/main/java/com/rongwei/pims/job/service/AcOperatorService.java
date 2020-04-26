package com.rongwei.pims.job.service;


import com.rongwei.pims.job.domain.AcOperator;
import com.rongwei.pims.job.domain.AcOperatorVO;

import java.util.List;

public interface AcOperatorService {
    String getEMPIdByUserId(String userId);

    List<AcOperatorVO> getOperatorByRoleIds(String[] roleIds);
}
