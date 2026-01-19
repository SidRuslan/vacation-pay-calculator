package com.example.vacationPayCalculator.validation;

import com.example.vacationPayCalculator.dto.VacationCalculationRequest;
import com.example.vacationPayCalculator.exception.InvalidRequestException;
import org.springframework.stereotype.Component;


@Component
public class VacationCalculatorValidator {

    public boolean isValidData(VacationCalculationRequest vacationCalculationRequest) {
        if (!hasValidSalary(vacationCalculationRequest)) {
            throw new InvalidRequestException("Average Salary must be positive!");
        }
        if (!hasValidVacationDates(vacationCalculationRequest)
                && hasValidSalary(vacationCalculationRequest)) {
            if (!hasValidVacationDaysCount(vacationCalculationRequest)) {
                throw new InvalidRequestException("Vacation Days Count must be positive!");
            }
        }
        if (hasValidVacationDates(vacationCalculationRequest)) {
            if (!hasValidDatesRange(vacationCalculationRequest)) {
                throw new InvalidRequestException("Start date must be before end date!");
            }
        }
        return true;
    }
    public boolean shouldCalculateByDaysOnly(VacationCalculationRequest vacationCalculationRequest) {
        return hasValidSalary(vacationCalculationRequest)
                && hasValidVacationDaysCount(vacationCalculationRequest)
                && !hasValidVacationDates(vacationCalculationRequest);
    }

    public boolean shouldCalculateByDates(VacationCalculationRequest vacationCalculationRequest) {
        return hasValidSalary(vacationCalculationRequest)
                && hasValidVacationDates(vacationCalculationRequest)
                && hasValidDatesRange(vacationCalculationRequest);
    }

    public boolean hasValidSalary(VacationCalculationRequest vacationCalculationRequest) {
        return vacationCalculationRequest.getAverageSalaryPerYear() != null
                && vacationCalculationRequest.getAverageSalaryPerYear() > 0.0;
    }

    public boolean hasValidVacationDates(VacationCalculationRequest vacationCalculationRequest) {
        return vacationCalculationRequest.getStartVacation() != null
                && vacationCalculationRequest.getFinishVacation() != null;
    }

    public boolean hasValidVacationDaysCount(VacationCalculationRequest vacationCalculationRequest) {
        return vacationCalculationRequest.getVacationDaysCount() != null
                && vacationCalculationRequest.getVacationDaysCount() > 0;
    }

    private boolean hasValidDatesRange(VacationCalculationRequest vacationCalculationRequest) {
        return vacationCalculationRequest.getStartVacation()
                .isBefore(vacationCalculationRequest.getFinishVacation());
    }
}
