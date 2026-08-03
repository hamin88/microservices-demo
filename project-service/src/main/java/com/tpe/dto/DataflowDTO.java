package com.tpe.dto;

import jakarta.validation.constraints.NotNull;

public record DataflowDTO (@NotNull(message = "Rule ID is required")  Long ruleId) {

}
