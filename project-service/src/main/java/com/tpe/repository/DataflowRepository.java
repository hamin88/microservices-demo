package com.tpe.repository;

import com.tpe.model.Dataflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataflowRepository extends JpaRepository<Dataflow, Long> {
    List<Dataflow> findAll();
    Dataflow  findByRuleId(Long ruleId);

}
