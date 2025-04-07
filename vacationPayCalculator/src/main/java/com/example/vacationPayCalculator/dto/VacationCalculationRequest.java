package com.example.vacationPayCalculator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VacationCalculationRequest {
    Double averageSalaryPerYear;
    Integer vacationDaysCount;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate startVacation;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate finishVacation;
}
