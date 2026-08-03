package com.tpe.model;

import jakarta.persistence.*;

@Entity
@Table(name = "dataflows") // Matches the table name in your Flyway script
public class Dataflow {

    public Dataflow() {}
    @Id
    @Column(name = "rule_id")
    private Long ruleId; // This acts as both the Primary Key and Foreign Key

    @OneToOne
    @MapsId
    @JoinColumn(name = "rule_id")
    private Rule rule;

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }
}
