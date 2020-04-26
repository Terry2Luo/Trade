package com.rongwei.pims.job.common.task;

import com.rongwei.pims.job.common.workflow.WorkFlowManager;
import com.rongwei.pims.job.domain.AcOperator;
import com.rongwei.pims.job.domain.AcOperatorVO;
import com.rongwei.pims.job.domain.PrFinanceCapitalAnnualDeptVO;
import com.rongwei.pims.job.service.AcOperatorService;
import com.rongwei.pims.job.service.PrFinanceCapitalAnnualDeptService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
@Component
public class DeptDailyAmountWarnJob implements Job {

    @Autowired
    private PrFinanceCapitalAnnualDeptService financeCapitalAnnualDeptService;
    @Autowired
    private WorkFlowManager workFlowManager;
    @Autowired
    private AcOperatorService acOperatorService;

    private static String[] roleIds = {"YGBZG","JYZG"};

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
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
        if(list != null && list.size() > 0){
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
                 }else if(cashBalance.compareTo(BigDecimal.ZERO) != 0){
                     redWarnList.add(vo);
                 }
             }
            this.warn(yellowWarnList,redWarnList);
        }
    }

    /**
     * 处理预警部门信息以及需要通知的人员
     * */
    public void warn(List<PrFinanceCapitalAnnualDeptVO> yellowWarnList,List<PrFinanceCapitalAnnualDeptVO> redWarnList){
        //部门长角色
        String[] roleId = {"bmz"};
        Map<String,PrFinanceCapitalAnnualDeptVO> ywmap = new HashMap<>();
        Map<String,PrFinanceCapitalAnnualDeptVO> rwmap = new HashMap<>();
        //找出各个部门部门长
        List<AcOperatorVO> bmzOperator = acOperatorService.getOperatorByRoleIds(roleId);
        if(bmzOperator != null && bmzOperator.size() > 0){
            //黄色预警部门
            for(PrFinanceCapitalAnnualDeptVO ywvo : yellowWarnList){
                Integer deptId = ywvo.getDeptId();
                Long deptid = deptId.longValue();
                //匹配部门长信息
                for(AcOperatorVO ac:bmzOperator){
                    Long orgId = ac.getOrgId();
                    if(orgId.equals(deptid)){
                        String yUserId = ac.getUserid();
                        ywmap.put(yUserId,ywvo);
                    }
                }
            }
            //红色预警部门
            for(PrFinanceCapitalAnnualDeptVO rwvo : redWarnList){
                Integer deptId = rwvo.getDeptId();
                Long deptid = deptId.longValue();
                //匹配部门长信息
                for(AcOperatorVO ac:bmzOperator){
                    Long orgId = ac.getOrgId();
                    if(orgId.equals(deptid)){
                        String rUserId = ac.getUserid();
                        rwmap.put(rUserId,rwvo);
                    }
                }
            }
            //发送预警信息给各部门部门长
            this.sendMessageToBMZ(ywmap,rwmap);
        }
        //找出运管部和财务部主管
        List<AcOperatorVO> zglist = acOperatorService.getOperatorByRoleIds(roleIds);
        //所有主管empId
        StringBuffer userIdsBuffer = new StringBuffer();
        if(zglist != null && zglist.size() > 0){
            for(AcOperatorVO ac:zglist){
                String zgUserId = ac.getUserid();
                userIdsBuffer.append(zgUserId).append(",");
            }
            String allUserIds = userIdsBuffer.substring(0, userIdsBuffer.length() - 1);
            this.sendMessageToZG(allUserIds,yellowWarnList,redWarnList);
        }
    }

    /**
     * 通知部门长
     **/
    public void sendMessageToBMZ( Map<String,PrFinanceCapitalAnnualDeptVO> ywmap,Map<String,PrFinanceCapitalAnnualDeptVO> rwmap ){
        //获取当前日期
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(date);
        //黄色预警部门通知部门长
        StringBuffer deptMesBuffer = new StringBuffer();
        for(String userId:ywmap.keySet()){
            //预警信息重置
            int bufferlen = deptMesBuffer.length();
            deptMesBuffer.delete(0,bufferlen);
            //根据用户名获取map中的信息
            PrFinanceCapitalAnnualDeptVO deptVO = ywmap.get(userId);
            String deptName = deptVO.getDeptName();
            //部门人工日常资金年度余额
            BigDecimal deptdailyremaindamount = deptVO.getDeptdailyremaindamount();
            deptMesBuffer.append("\"截止至【").append(today).append("】，【")
                    .append(deptName).append("】人工日常资金年度剩余余额为：【").append(deptdailyremaindamount)
                    .append("】万元【黄色预警】，小于【初始资金/4】【模拟法人】\"");
            String contenttext = deptMesBuffer.toString();
            this.sendMessage("luxing",contenttext);
        }

        //红色预警部门通知部门长
        for(String userId:rwmap.keySet()){
            //预警信息重置
            int bufferlen = deptMesBuffer.length();
            deptMesBuffer.delete(0,bufferlen);
            PrFinanceCapitalAnnualDeptVO deptVO = rwmap.get(userId);
            String deptName = deptVO.getDeptName();
            //部门人工日常资金年度余额
            BigDecimal deptdailyremaindamount = deptVO.getDeptdailyremaindamount();
            deptMesBuffer.append("\"截止至【").append(today).append("】，【")
                    .append(deptName).append("】人工日常资金年度剩余余额为：【").append(deptdailyremaindamount)
                    .append("】万元【红色预警】，小于【初始资金/8】【模拟法人】\"");
            String contenttext = deptMesBuffer.toString();
            this.sendMessage("luxing",contenttext);
        }
    }

    /**
     * 通知主管
     **/
    public void sendMessageToZG(String allUserIds,List<PrFinanceCapitalAnnualDeptVO> yellowWarnList,List<PrFinanceCapitalAnnualDeptVO> redWarnList){
        //获取当前日期
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(date);
        //预警部门信息
        StringBuffer deptMesBuffer = new StringBuffer();
        //最后不满10条黄色预警部门的信息数量
        int ydleaveNum =  yellowWarnList.size();
        for(int i=0;i<yellowWarnList.size();i++){
            //预警信息
            PrFinanceCapitalAnnualDeptVO deptVO = yellowWarnList.get(i);
            String deptName = deptVO.getDeptName();
            //部门人工日常资金年度余额
            BigDecimal deptdailyremaindamount = deptVO.getDeptdailyremaindamount();
            if(deptMesBuffer.length() == 0){
                deptMesBuffer.append("\"截止至【").append(today).append("】\n");
            }
            deptMesBuffer.append("【")
                        .append(deptName).append("】人工日常资金年度剩余余额为：【").append(deptdailyremaindamount)
                        .append("】万元【黄色预警】，小于【初始资金/4】【模拟法人】\n");

            //每10条信息发送一次
            if(yellowWarnList.size() < 10){
                continue;
            }else if(i%10 == 0){
                deptMesBuffer.append("\"");
                //发送信息
                this.sendMessage("luxing",deptMesBuffer.toString());
                //预警信息重置
                int bufferlen = deptMesBuffer.length();
                deptMesBuffer.delete(0,bufferlen);
                ydleaveNum = ydleaveNum - i;
            }
        }
        //红色预警部门数量和黄色预警部门剩余数量总和
        int len = redWarnList.size()+ydleaveNum;
        //红色预警部门信息
        for(int j=0;j<redWarnList.size();j++){
            //预警信息
            PrFinanceCapitalAnnualDeptVO deptVO = redWarnList.get(j);
            String deptName = deptVO.getDeptName();
            //部门人工日常资金年度余额
            BigDecimal deptdailyremaindamount = deptVO.getDeptdailyremaindamount();
            if(deptMesBuffer.length() == 0){
                deptMesBuffer.append("\"截止至【").append(today).append("】\n");
            }
            deptMesBuffer.append("【")
                    .append(deptName).append("】人工日常资金年度剩余余额为：【").append(deptdailyremaindamount)
                    .append("】万元【红色预警】，小于【初始资金/8】【模拟法人】\n");

            if(len < 10){
                continue;
            }else if((j+ydleaveNum)%10 == 0){
                deptMesBuffer.append("\"");
                //发送信息
                this.sendMessage("luxing",deptMesBuffer.toString());
                //预警信息重置
                int bufferlen = deptMesBuffer.length();
                deptMesBuffer.delete(0,bufferlen);
            }
        }
        //发送剩余消息
        if(deptMesBuffer.length() != 0){
            deptMesBuffer.append("\"");
            String contenttext = deptMesBuffer.toString();
            //发送信息
            this.sendMessage("luxing",contenttext);
        }
    }

    public void sendMessage(String userIds,String warndept){
        workFlowManager.sendMessage(userIds,warndept);
    }
}
