package com.example.vacationPayCalculator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VacationCalculationResponse {
    private BigDecimal amount;
    private String currency;
    private CalculationType calculationType;
}
