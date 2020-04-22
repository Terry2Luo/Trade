package com.rongwei.pims.job.common.task;

import com.rongwei.pims.job.common.workflow.WorkFlowManager;
import com.rongwei.pims.job.domain.PrFinanceCapitalAnnualDeptVO;
import com.rongwei.pims.job.service.PrFinanceCapitalAnnualDeptService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
@Component
public class DeptDailyAmountWarnJob implements Job {

    @Autowired
    private PrFinanceCapitalAnnualDeptService financeCapitalAnnualDeptService;
    @Autowired
    private WorkFlowManager workFlowManager;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Map<String,Object> params = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        params.put("year",year);
        this.countDeptDailyAmount(params);
    }

    public void testwarn(){
        Map<String,Object> params = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        params.put("year",year);
        this.countDeptDailyAmount(params);
    }

    /**
     * 统计部门日常资金余额（也叫人工日常资金余额？）
     * */
    public void countDeptDailyAmount(Map<String,Object> params){
        //获取所有部门数据
        List<PrFinanceCapitalAnnualDeptVO> list = financeCapitalAnnualDeptService.list(params);
        //存放需要黄色预警部门数据
        List<PrFinanceCapitalAnnualDeptVO> yellowWarnList = new LinkedList<>();
        //存放需要红色预警部门数据
        List<PrFinanceCapitalAnnualDeptVO> redWarnList = new LinkedList<>();
        //判断哪些部门需要预警
        for(PrFinanceCapitalAnnualDeptVO vo : list){
            //初始资金
            BigDecimal cashBalance = vo.getCashBalance()!=null?vo.getCashBalance():new BigDecimal('0');
            //部门日常资金余额
            BigDecimal deptdailyremaindamount = vo.getDeptdailyremaindamount()!=null?vo.getDeptdailyremaindamount():new BigDecimal('0');
            //四分之一初始资金
            BigDecimal cashBalanceQuarter = cashBalance.divide(new BigDecimal('4'),6,BigDecimal.ROUND_HALF_UP);
            //八分之一初始资金
            BigDecimal oneeighthcashBalance = cashBalance.divide(new BigDecimal('8'),6,BigDecimal.ROUND_HALF_UP);

            if(deptdailyremaindamount.compareTo(cashBalanceQuarter) > 1){
                continue;
            }else if(deptdailyremaindamount.compareTo(oneeighthcashBalance) > 1){
                yellowWarnList.add(vo);
            }else {
                redWarnList.add(vo);
            }
        }
        this.warn(yellowWarnList,redWarnList);
    }

    /**
     * 发送信息至交建通
     * */
    public void warn(List<PrFinanceCapitalAnnualDeptVO> yellowWarnList,List<PrFinanceCapitalAnnualDeptVO> redWarnList){
        //对需要预警的部门进行处理
        StringBuffer buffer = new StringBuffer();
        for(PrFinanceCapitalAnnualDeptVO vo : yellowWarnList){
            buffer = buffer.append("\n").append(vo.getDeptName()).append(":").append(vo.getDeptdailyremaindamount());
        }
        for(PrFinanceCapitalAnnualDeptVO vo : redWarnList){
            buffer = buffer.append("\n").append(vo.getDeptName()).append(":").append(vo.getDeptdailyremaindamount());
        }
        this.test(buffer.toString());
    }

    public void test(String warndept){
        workFlowManager.sendMessage("luxing",warndept);
    }
}
