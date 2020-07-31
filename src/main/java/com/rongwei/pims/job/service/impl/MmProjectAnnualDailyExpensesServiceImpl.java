package com.rongwei.pims.job.service.impl;

import com.rongwei.pims.job.common.BigDecimalUtil;
import com.rongwei.pims.job.dao.MmProjectAnnualDailyExpensesMapper;
import com.rongwei.pims.job.dao.MmProjectAnnualDailyExpensesSubMapper;
import com.rongwei.pims.job.domain.MmContractProject;
import com.rongwei.pims.job.domain.MmProjectAnnualDailyExpenses;
import com.rongwei.pims.job.domain.MmProjectAnnualDailyExpensesVO;
import com.rongwei.pims.job.service.MmContractProjectService;
import com.rongwei.pims.job.service.MmProjectAnnualDailyExpensesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class MmProjectAnnualDailyExpensesServiceImpl
        implements
 MmProjectAnnualDailyExpensesService {
    private static Logger logger = LoggerFactory.getLogger(MmProjectAnnualDailyExpensesServiceImpl.class);

    //跟踪, 投标,委托待签,谈定待签,中标待签,已签,中标已签
    private static List<Integer> marketStatus = new ArrayList<>();
    static {
        marketStatus.add(2);
        marketStatus.add(4);
        marketStatus.add(6);
        marketStatus.add(7);
        marketStatus.add(8);
        marketStatus.add(9);
        marketStatus.add(10);
    }
    //无收入的分包比例
    private static Double NO_REVENUE_RATIO =  0.95d;
    //到款大于收入的日常比例
    private static Double MORE_RECEIVED_RATIO =  0.05d;

    @Autowired
    private MmProjectAnnualDailyExpensesSubMapper projectAnnualDailyExpensesSubMapper;

    @Autowired
    private MmContractProjectService contractProjectService;

    public MmProjectAnnualDailyExpenses get(Integer id){
        MmProjectAnnualDailyExpenses projectAnnualDailyExpenses = projectAnnualDailyExpensesSubMapper.selectByPrimaryKey(id);
        return projectAnnualDailyExpenses;
    }

    @Override
    public List<MmProjectAnnualDailyExpensesVO> listProjectContractInfo(Map<String,Object> map){
        return projectAnnualDailyExpensesSubMapper.listProjectContractInfo(map);
    }
    @Override
    public void deleteByYearAndCode(Map<String,Object> map){
        projectAnnualDailyExpensesSubMapper.deleteByYearAndCode(map);
    }

    @Override
    @Transactional
    public void calcPrjPurchaseAmount(Integer year, String projectCode){
        Calendar start = Calendar.getInstance();
        Boolean isUpdateRemainPruchase = false;
        if(start.get(Calendar.YEAR) == year){
            isUpdateRemainPruchase = true;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("year", year);
        map.put("lastYear", year-1);

        //制定项目编号时，无需项目状态限制
        if(projectCode != null && !projectCode.equals("")){
            map.put("projectCode", projectCode);
        }else{
            //默认的经营项目状态才可以更新
            map.put("marketStatus", marketStatus);
        }

        //删除历史数据
        deleteByYearAndCode(map);
        //查询项目数据
        List<MmProjectAnnualDailyExpensesVO> list = listProjectContractInfo(map);
        // 保存的日常费用
        List<MmProjectAnnualDailyExpensesVO> insertList = new ArrayList();
        //
        List<MmContractProject> contractProjectList = new ArrayList();
        //计算分包余额
        if(list != null){
            //批插入的数量处理，1000条数据提交数据库一次
            int count =0;

            for(MmProjectAnnualDailyExpensesVO dailyExpensesVO:list){
                try{
                    dailyExpensesVO.setYear(year);
                    if(dailyExpensesVO.getTotalreceivemoneyamount() == null || dailyExpensesVO.getTotalreceivemoneyamount().doubleValue() ==0
                       ){
                        continue;
                    }
                    count ++;
                    //分批提交数据 MSSQL 更新数据时，只支持2100个参赛，需要分批提交
                    if(count == 100){
                        this.insertBatch(insertList);
                        //重置list数据为空，count ==1
                        insertList.clear();
                        count = 0;
                    }
                    //有到款，无项目营业额时，年度日常费用按照5%计算；分包余额为95%；
                    if(dailyExpensesVO.getTotalrevenusamount() == null || dailyExpensesVO.getTotalrevenusamount().doubleValue() ==0 ){
                        BigDecimal annualpurchaseremaindamount = new BigDecimal(0);
                        annualpurchaseremaindamount = dailyExpensesVO.getTotalreceivemoneyamount().multiply(new BigDecimal(NO_REVENUE_RATIO));
                        //减去付款
                        annualpurchaseremaindamount = BigDecimalUtil.doSub(annualpurchaseremaindamount,dailyExpensesVO.getTotalpaidmoneyamount());
                        dailyExpensesVO.setAnnualpurchaseremaindamount(annualpurchaseremaindamount);

                        BigDecimal dailycostamount = new BigDecimal(0);
                        dailycostamount = BigDecimalUtil.doSub(dailyExpensesVO.getTotalreceivemoneyamount(),annualpurchaseremaindamount );
                        dailycostamount = BigDecimalUtil.doSub(dailycostamount, dailyExpensesVO.getTotalpaidmoneyamount());
                        dailyExpensesVO.setDailycostamount(dailycostamount);


                    } else
                    //但项目到款＞项目营业额的，超额部分的95%按照分包资金余额计算，超额部分的5%计入部门的日常费用
                    if(dailyExpensesVO.getTotalrevenusamount().doubleValue() < dailyExpensesVO.getTotalreceivemoneyamount().doubleValue()
                    ){
                        //项目营业额部分的年度日常费用 = 营业额- 已使用日常成本 -分包成本
                        BigDecimal dailycostByRevenue = BigDecimalUtil.doSub(BigDecimalUtil.doSub(dailyExpensesVO.getTotalrevenusamount(),dailyExpensesVO.getPredailycostamount()),dailyExpensesVO.getTotalpurchasecostamount());

                        //超额到款部门的日常费用
                        BigDecimal dailycostByReceive = BigDecimalUtil.doSub(dailyExpensesVO.getTotalreceivemoneyamount(),dailyExpensesVO.getTotalrevenusamount())
                                .multiply(new BigDecimal(MORE_RECEIVED_RATIO));
                        BigDecimal dailycostamount = BigDecimalUtil.doAdd(dailycostByRevenue, dailycostByReceive);
                        dailyExpensesVO.setDailycostamount(dailycostamount);

                        //分包余额 = 到款-付款-累计日常费用
                        BigDecimal annualpurchaseremaindamount = BigDecimalUtil.doSub(dailyExpensesVO.getTotalreceivemoneyamount(),dailyExpensesVO.getTotalpaidmoneyamount());
                        annualpurchaseremaindamount = BigDecimalUtil.doSub(annualpurchaseremaindamount,dailyExpensesVO.getPredailycostamount());
                        annualpurchaseremaindamount = BigDecimalUtil.doSub(annualpurchaseremaindamount, dailycostamount);
                        dailyExpensesVO.setAnnualpurchaseremaindamount(annualpurchaseremaindamount);

                    } else {

                        //历年已使用日常资金
                        BigDecimal predailycostamount = new BigDecimal(0);
                        if (dailyExpensesVO.getPredailycostamount() != null) {
                            predailycostamount = dailyExpensesVO.getPredailycostamount();
                        }
                        //已付款金额
                        BigDecimal totalpaidmoneyamount = new BigDecimal(0);
                        if (dailyExpensesVO.getTotalpaidmoneyamount() != null) {
                            totalpaidmoneyamount = dailyExpensesVO.getTotalpaidmoneyamount();
                        }

                        //年度剩余分包资金
                        BigDecimal annualpurchaseremaindamount = new BigDecimal(0);
                        if (dailyExpensesVO.getAnnualpurchaseremaindamount() != null) {
                            annualpurchaseremaindamount = dailyExpensesVO.getAnnualpurchaseremaindamount();
                        }

                        //已付款金额+已使用日常费用 > 已收款金额时；剩余分包资金为0

                        //无收入时，分包余额为0
                        if (dailyExpensesVO.getTotalrevenusamount() == null || dailyExpensesVO.getTotalrevenusamount().doubleValue() == 0
                                || dailyExpensesVO.getTotalpurchasecostamount() == null || dailyExpensesVO.getTotalpurchasecostamount().doubleValue() == 0
                        ) {
                            annualpurchaseremaindamount = new BigDecimal(0);
                        } else {
                            //分包余额=Σ项目到款×（项目累计分包产值/项目累计项目营业额）-项目累计付款
                            annualpurchaseremaindamount = dailyExpensesVO.getTotalreceivemoneyamount()
                                    .multiply(dailyExpensesVO.getTotalpurchasecostamount()) // getTotalpurchasecostamount
                                    .divide(dailyExpensesVO.getTotalrevenusamount(), 6, BigDecimal.ROUND_HALF_UP)
                                    .subtract(totalpaidmoneyamount);
                        }

                        dailyExpensesVO.setAnnualpurchaseremaindamount(annualpurchaseremaindamount);
//                    }
                        //年度日常费用 = 项目到款（累计值）- 历年日常费用 – 已付款-年度分包余额
                        dailyExpensesVO.setDailycostamount(dailyExpensesVO.getTotalreceivemoneyamount()
                                .subtract(totalpaidmoneyamount)
                                .subtract(predailycostamount)
                                .subtract(annualpurchaseremaindamount)
                        );
                    }
                }catch (Exception e){
                    logger.error("projectcode:     "+dailyExpensesVO.getMmprojectcode());
                    logger.error(e.getMessage());
                }

                //当年新增分包余额 年度新增分包预留资金=分包余额-上年分包余额+年度付款
                BigDecimal annualaddpurchaseremaindamount = new BigDecimal(0);

                annualaddpurchaseremaindamount = BigDecimalUtil.doSub(dailyExpensesVO.getAnnualpurchaseremaindamount() , dailyExpensesVO.getLastannualpurchaseremaindamount());
                // -上年分包预留资金-年度付款
                // annualaddpurchaseremaindamount = BigDecimalUtil.doSub(annualaddpurchaseremaindamount, dailyExpensesVO.getLastannualpurchaseremaindamount());
                annualaddpurchaseremaindamount = BigDecimalUtil.doAdd(annualaddpurchaseremaindamount, dailyExpensesVO.getPaidmoneyamount());
                dailyExpensesVO.setAnnualaddpurchaseremaindamount(annualaddpurchaseremaindamount);
                insertList.add(dailyExpensesVO);
            }

            if(insertList != null && insertList.size() > 0){
                this.insertBatch(insertList);
            }
//            //分包资金余额更新
//            if(contractProjectList != null && contractProjectList.size() > 0){
//                contractProjectService.updateBatch(contractProjectList);
//            }
        }

        Calendar end = Calendar.getInstance();
        logger.info("处理时间（秒）："+(end.getTimeInMillis()-start.getTimeInMillis())/1000);


    }

    @Override
    public int insertBatch(List<MmProjectAnnualDailyExpensesVO> dailyExpensesVO){
        return projectAnnualDailyExpensesSubMapper.insertBatch(dailyExpensesVO);
    }

    /**
     * 封装项目合同的相关信息
     */
    private List<MmContractProject> doPopulateContractProject(List<MmContractProject> contractProjectList, MmProjectAnnualDailyExpensesVO  dailyExpenses){
        MmContractProject contractProject = new MmContractProject();
        contractProject.setContractprojectsplitid(dailyExpenses.getContractprojectsplitid());
        contractProject.setPurchasecontractamount(dailyExpenses.getAnnualpurchaseremaindamount());
        contractProjectList.add(contractProject);
        return contractProjectList;
    }

}
