package com.tpe.service;

import com.tpe.model.Dataflow;
import com.tpe.model.Rule;
import com.tpe.repository.DataflowRepository;
import com.tpe.repository.RuleRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RuleService {

    @Autowired
    private RuleRepository dataflowrepository;

    public List<Rule> findAll() {
        return dataflowrepository.findAll();
    }

    public Rule findById(Long id) {
        return dataflowrepository.findById(id).orElse(null);
    }

    @Transactional
    public Rule saveAndFlush(Rule rule) {
        return dataflowrepository.saveAndFlush(rule);
    }
}
