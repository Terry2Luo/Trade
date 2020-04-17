package com.rongwei.pims.job;


import com.rongwei.pims.job.common.task.PurchaseRemainAmountJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * User: hzwangxx
 * Date: 14-2-26
 * Time: 0:16
 */
public class QuartzTest {
    public static void main(String[] args) {
        try {
            //1.从StdSchedulerFactory工厂中获取一个任务调度器
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

            //2. 启动调度器
            scheduler.start();
            System.out.println("scheduler is start...");
            //3. 添加定时任务
            //  3.1 定义job
            JobDetail job = newJob(PurchaseRemainAmountJob.class)
                    .withIdentity("job1", "group1")
                    .build();

            //  3.2 定义Trigger，使得job现在就运行，并每隔3s中运行一次，重复运行5次, withRepeatCount(number)设定job运行次数为number+1
            Trigger trigger = newTrigger()
                    .withIdentity("trigger1", "group1")
                    .startNow()
                    .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(3)
                            .withRepeatCount(4))
                    .build();

            //  3.3 交给scheduler去调度
            scheduler.scheduleJob(job, trigger);
            //4. 关闭调度器
            //scheduler.shutdown();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }


}
/*
  console output:
2014-02-26 01:05:25,766 0    [main] INFO  - Using default implementation for ThreadExecutor
2014-02-26 01:05:25,794 28   [main] INFO  - Initialized Scheduler Signaller of type: class org.quartz.core.SchedulerSignalerImpl
2014-02-26 01:05:25,795 29   [main] INFO  - Quartz Scheduler v.2.2.0 created.
2014-02-26 01:05:25,797 31   [main] INFO  - RAMJobStore initialized.
2014-02-26 01:05:25,798 32   [main] INFO  - Scheduler meta-data: Quartz Scheduler (v2.2.0) 'MyScheduler' with instanceId 'NON_CLUSTERED'
  Scheduler class: 'org.quartz.core.QuartzScheduler' - running locally.
  NOT STARTED.
  Currently in standby mode.
  Number of jobs executed: 0
  Using thread pool 'org.quartz.simpl.SimpleThreadPool' - with 3 threads.
  Using job-store 'org.quartz.simpl.RAMJobStore' - which does not support persistence. and is not clustered.

2014-02-26 01:05:25,798 32   [main] INFO  - Quartz scheduler 'MyScheduler' initialized from default resource file in Quartz package: 'quartz.properties'
2014-02-26 01:05:25,799 33   [main] INFO  - Quartz scheduler version: 2.2.0
2014-02-26 01:05:25,799 33   [main] INFO  - Scheduler MyScheduler_$_NON_CLUSTERED started.
scheduler is start...
execute job at Wed Feb 26 01:05:25 CST 2014 by trigger group1.job1
execute job at Wed Feb 26 01:05:28 CST 2014 by trigger group1.job1
execute job at Wed Feb 26 01:05:31 CST 2014 by trigger group1.job1
execute job at Wed Feb 26 01:05:34 CST 2014 by trigger group1.job1
execute job at Wed Feb 26 01:05:37 CST 2014 by trigger group1.job1

*/
