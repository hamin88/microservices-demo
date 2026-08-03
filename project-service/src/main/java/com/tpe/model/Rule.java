package com.tpe.model;

import jakarta.persistence.*;
@Entity
@Table(name = "rules") // Matches the table name in your Flyway script
public class Rule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // This is the "id" referenced by jobConfig.ruleId

    @Column(name = "name", nullable = false)
    private String ruleName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_type_id", referencedColumnName = "id", nullable = true)
    private RuleType ruleType;

    // Default Constructor (Required by JPA)
    public Rule() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }

    public RuleType getRuleType() { return ruleType; }
    public void setRuleType(RuleType ruleType) { this.ruleType = ruleType; }
}
