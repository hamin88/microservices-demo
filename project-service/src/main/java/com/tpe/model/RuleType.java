package com.tpe.model;

import jakarta.persistence.*;

@Entity
@Table(name = "rule_types") // Matches the table name in your Flyway script
public class RuleType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // This is the "id" referenced by jobConfig.ruleId

    @Column(name = "name", nullable = false)
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
