package com.rongwei.pims.job.dao;

import com.rongwei.pims.job.domain.AcOperator;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AcOperatorSubMapper extends AcOperatorMapper{
    List<AcOperator> getEMPIdByUserId(String userId);
}
