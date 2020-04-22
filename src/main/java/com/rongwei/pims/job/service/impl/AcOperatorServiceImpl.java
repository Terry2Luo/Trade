package com.rongwei.pims.job.service.impl;

import com.rongwei.pims.job.dao.AcOperatorSubMapper;
import com.rongwei.pims.job.domain.AcOperator;
import com.rongwei.pims.job.service.AcOperatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class AcOperatorServiceImpl implements AcOperatorService {
    @Autowired
    private AcOperatorSubMapper acOperatorMapper;

    private static AcOperatorServiceImpl impl;

    /**
     *  静态方法调用自动注入的变量时用
     */
    @PostConstruct
    public void init(){
        impl = this;
        impl.acOperatorMapper = this.acOperatorMapper;
    }

    @Override
    public String getEMPIdByUserId(String userId) {
        List<AcOperator> AcOperatorList = impl.acOperatorMapper.getEMPIdByUserId(userId);
        String empId = null;
        if(AcOperatorList != null && AcOperatorList.size() > 0){
            empId = AcOperatorList.get(0).getEmpid();
        }
        return empId;
    }
}
