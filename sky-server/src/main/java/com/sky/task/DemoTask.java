package com.sky.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
//@Component
public class DemoTask {
    @Scheduled(cron = "0/5 * * * * ?")
    public void printLog(){
        log.info("执行定时任务{}", LocalDateTime.now());
    }
}
