package com.tpe.service;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.*;
import org.quartz.*;
import org.springframework.stereotype.Component;

@Component
public class DataflowJob extends QuartzJobBean {

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getMergedJobDataMap();
        String ruleIdStr = dataMap.getString("ruleId");
        System.out.println("DataflowJob Job executed for the ruleId: "+ ruleIdStr+" successfully at: " + context.getFireTime());
    }
}
