package com.zhm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by 赵红明 on 2019/5/30.
 * 配置定时任务，如果不配置，设置的定时任务就没法启动
 */
@Configuration
@EnableScheduling
public class ScheduledConfig implements SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());
    }

    /**ScheduleTimer
     * 设置定时任务的线程(四种线程池)
     * @return
     */
    @Bean(destroyMethod="shutdown")
    public Executor taskExecutor() {
        //1:固定大小线程池: 设置固定值会造成高并发线程排队等待空闲线程，尤其是当读取大数据量时线程处理时间长而不释放线程，导致无法创建新线程。
        //Executors.newFixedThreadPool(10);
        //2:可缓存线程池 线程池无限大，而系统资源（内存等）有限，会导致机器内存溢出OOM。
        //Executors.newCachedThreadPool();
        //3:定长且可定时、周期线程池
        //Executors.newScheduledThreadPool(5);
        //4:单线程线程池
        //Executors.newSingledThreadPool();
        return Executors.newScheduledThreadPool(5);

    }
}