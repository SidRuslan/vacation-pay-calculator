package com.example.vacationPayCalculator.service;

import com.example.vacationPayCalculator.dto.CalculationType;
import com.example.vacationPayCalculator.dto.VacationCalculationRequest;
import com.example.vacationPayCalculator.dto.VacationCalculationResponse;
import com.example.vacationPayCalculator.exception.InvalidRequestException;
import com.example.vacationPayCalculator.validation.VacationCalculatorValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class VacationCalculatorServiceTest {

    @InjectMocks
    private VacationCalculatorService calculatorService;

    private final LocalDate START_DATE = LocalDate.of(2025, 5, 1);
    private final LocalDate END_DATE = LocalDate.of(2025, 5, 10);
    private final int DAYS_COUNT = 10;
    private final double AVERAGE_SALARY_PER_YEAR = 600_000.0;

    @Test
    void calculate_WhenValidDatesProvided_ReturnsCorrectAmountIncludingWeekendsAndHolidays() {

        VacationCalculationRequest request = new VacationCalculationRequest();
        request.setStartVacation(START_DATE);
        request.setFinishVacation(END_DATE);
        request.setAverageSalaryPerYear(AVERAGE_SALARY_PER_YEAR);

        VacationCalculationResponse response = calculatorService.calculate(request);

        assertEquals(CalculationType.BY_DATES, response.getCalculationType());
        assertEquals("RUB", response.getCurrency());
        assertTrue(response.getAmount().compareTo(BigDecimal.ZERO) > 0);
        assertEquals(8219.18, response.getAmount().doubleValue());
    }

    @Test
    void calculate_WhenValidDaysCountProvided_ReturnsCorrectAmount() {

        VacationCalculationRequest request = new VacationCalculationRequest();
        request.setVacationDaysCount(DAYS_COUNT);
        request.setAverageSalaryPerYear(AVERAGE_SALARY_PER_YEAR);

        VacationCalculationResponse response = calculatorService.calculate(request);

        assertEquals(CalculationType.BY_DAYS, response.getCalculationType());
        assertEquals("RUB", response.getCurrency());
        assertEquals(16438.36, response.getAmount().doubleValue());
    }


    @Test
    void isValidData_WithInvalidSalary_ThrowsException() {
        VacationCalculationRequest request = new VacationCalculationRequest(
                0.0,
                10,
                null, null
        );

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> VacationCalculatorValidator.isValidData(request)
        );

        assertEquals("Average Salary must be positive!", exception.getMessage());
    }

    @Test
    void isValidData_WithInvalidDaysCount_ThrowsException() {
        VacationCalculationRequest request = new VacationCalculationRequest(
                AVERAGE_SALARY_PER_YEAR,
                0,
                null, null
        );

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> VacationCalculatorValidator.isValidData(request)
        );

        assertEquals("Vacation Days Count must be positive!", exception.getMessage());
    }

    @Test
    void isValidData_WithInvalidDateRange_ThrowsException() {
        VacationCalculationRequest request = new VacationCalculationRequest(
                AVERAGE_SALARY_PER_YEAR,
                0,
                LocalDate.of(2025, 5, 10),
                LocalDate.of(2025, 5, 1)
        );

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> VacationCalculatorValidator.isValidData(request)
        );

        assertEquals("Start date must be before end date!", exception.getMessage());
    }

    @Test
    void isValidData_WithMissingRequiredParams_ThrowsException() {
        VacationCalculationRequest request = new VacationCalculationRequest(
                AVERAGE_SALARY_PER_YEAR,
                null,
                null,
                null
        );

        assertThrows(
                InvalidRequestException.class,
                () -> VacationCalculatorValidator.isValidData(request)
        );
    }

}