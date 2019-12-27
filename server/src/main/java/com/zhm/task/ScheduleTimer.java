package com.zhm.task;

import com.zhm.dao.RandomCodeDao;
import com.zhm.entity.RandomCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 定时任务模拟
 * Created by 赵红明 on 2019/5/30.
 */
@Component
public class ScheduleTimer {
    private static Logger logger= LoggerFactory.getLogger(ScheduleTimer.class);

    @Autowired
    private RandomCodeDao randomCodeDao;
    /**
     * 该定时任务批量查询验证码表里的逻辑状态是未删除的数据。
     */
    @Scheduled(fixedDelay = 180000)//3分钟执行一次
    public void Test(){
        logger.info("查看验证码是否失效的定时任务begin");
        List<RandomCode> randomCodeList=randomCodeDao.findByDeleteFlag(0);
        if(randomCodeList!=null&&randomCodeList.size()>0){
            for(RandomCode randomCode:randomCodeList){
                long nTime=(new Date()).getTime();
                long cTime= randomCode.getCreateTime().getTime();
                if(nTime-cTime<randomCode.getPastTime()){
                    logger.info("验证码有效");
                }else{
                    randomCode.setDeleteFlag(1);
                    randomCode.setUpdateUser("系统");
                    randomCode.setUpdateTime(new Date());
                    randomCodeDao.save(randomCode);
                }
            }
        }
        logger.info("查看验证码是否失效的定时任务end");
    }
    @Scheduled(cron ="0 0 1 * * ?")//每天凌晨一点执行
    public void Test1(){
        logger.info("凌晨一点的定时任务定时任务来了");
    }
}
