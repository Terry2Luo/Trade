package com.rongwei.pims.job.service.impl;

import com.rongwei.pims.job.dao.AcOperatorSubMapper;
import com.rongwei.pims.job.domain.AcOperator;
import com.rongwei.pims.job.domain.AcOperatorVO;
import com.rongwei.pims.job.service.AcOperatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AcOperatorServiceImpl implements AcOperatorService {
    @Autowired
    private AcOperatorSubMapper acOperatorMapper;

    @Override
    public String getEMPIdByUserId(String userId) {
        List<AcOperator> AcOperatorList = acOperatorMapper.getEMPIdByUserId(userId);
        String empId = null;
        if(AcOperatorList != null && AcOperatorList.size() > 0){
            empId = AcOperatorList.get(0).getEmpid();
        }
        return empId;
    }

    @Override
    public List<AcOperatorVO> getOperatorByRoleIds(String[] roleIds) {
        return acOperatorMapper.getOperatorByRoleIds(roleIds);
    }
}
