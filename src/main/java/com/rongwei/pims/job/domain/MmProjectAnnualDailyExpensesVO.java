package com.rongwei.pims.job.domain;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class MmProjectAnnualDailyExpensesVO  extends MmProjectAnnualDailyExpenses implements Serializable {
    //上一年度累计日常费用
    private BigDecimal predailycostamount;
    //合同项目数据主键ID
    private Integer contractprojectsplitid;


}