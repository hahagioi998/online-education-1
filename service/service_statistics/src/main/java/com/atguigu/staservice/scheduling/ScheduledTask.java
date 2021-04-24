package com.atguigu.staservice.scheduling;

import com.atguigu.staservice.service.StatisticsDailyService;

import com.atguigu.staservice.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ScheduledTask {

    @Autowired
    private StatisticsDailyService dailyService;

    /**
     * 测试
     * 每天七点到二十三点每五秒执行一次
     */
    //@Scheduled(cron = "0/5 * * * * ?")  //七子表达式(七域表达式)
    //public void task1() {
    //    System.out.println("*********++++++++++++*****执行了");
    //}

    /**
     * 每天凌晨1点执行定时
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void task2() {
        //获取上一天的日期l
        String day = DateUtil.formatDate(DateUtil.addDays(new Date(), -1));
        dailyService.registerCount(day);

    }
}
