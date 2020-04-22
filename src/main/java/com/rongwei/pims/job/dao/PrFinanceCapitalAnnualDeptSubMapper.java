package com.rongwei.pims.job.dao;

import com.rongwei.pims.job.domain.PrFinanceCapitalAnnualDeptVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface PrFinanceCapitalAnnualDeptSubMapper extends PrFinanceCapitalAnnualDeptMapper {
    List<PrFinanceCapitalAnnualDeptVO> list(Map<String,Object> param);

}
