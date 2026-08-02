package com.tpe.model;

import jakarta.persistence.*;

@Entity
@Table(name = "rules") // Matches the table name in your Flyway script
public class Rule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // This is the "id" referenced by jobConfig.ruleId

    @Column(name = "rule_name", nullable = false)
    private String ruleName;

    @Column(name = "rule_condition", length = 1000)
    private String ruleCondition;

    // Default Constructor (Required by JPA)
    public Rule() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }

    public String getRuleCondition() { return ruleCondition; }
    public void setRuleCondition(String ruleCondition) { this.ruleCondition = ruleCondition; }
}
