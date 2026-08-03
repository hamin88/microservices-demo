package com.tpe.service;

import com.tpe.model.Dataflow;
import com.tpe.model.JobConfig;
import com.tpe.repository.DataflowRepository;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class DataflowService {

    @Autowired
    private DataflowRepository dataflowrepository;

    public List<Dataflow> findAll() {
        return dataflowrepository.findAll();
    }

    public Dataflow findById(Long id) {
        return dataflowrepository.findByRuleId(id);
    }
}
