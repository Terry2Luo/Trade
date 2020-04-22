package com.rongwei.pims.job.common.task;

import com.rongwei.pims.job.common.HttpRequestUtils;
import com.rongwei.pims.job.common.PimsProperties;
import com.rongwei.pims.job.service.MmProjectAnnualDailyExpensesService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Calendar;

/**
 * 分包资金余额定时任务
 **/
@Component
public class PimsEveryDayJob implements Job {

    @Autowired
    private PimsProperties roperties;

    private static Logger logger = LoggerFactory.getLogger(PimsEveryDayJob.class);


    public static PimsEveryDayJob pimsEveryDayJob;

    private static String everyDayJobUrl="com.rongwei.pims.pub.select.common.ManageDaySchedule..biz.ext";

    public PimsEveryDayJob(){

    }

    // 关键
    @PostConstruct
    public void init() {
        pimsEveryDayJob = this;
        pimsEveryDayJob.roperties = this.roperties;
    }


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("结束调用定时开始");
        //判断要查询的记录对应年份是否有对应表，没有则创建
        Calendar today = Calendar.getInstance();
        //当天预测明天的数据
        //today.set(Calendar.HOUR_OF_DAY,24);

        try{
            HttpRequestUtils.sendPost(everyDayJobUrl, roperties.getServerIp(), roperties.getServerPort(), roperties.getUserID(), roperties.getPassWord());
        }catch (Exception e){
            logger.error("开始调用定时任务   "+e.getMessage());
        }

        logger.info("结束调用定时任务");
    }





}
