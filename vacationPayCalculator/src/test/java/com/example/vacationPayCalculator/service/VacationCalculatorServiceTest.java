package com.example.vacationPayCalculator.service;

import com.example.vacationPayCalculator.dto.CalculationType;
import com.example.vacationPayCalculator.dto.VacationCalculationRequest;
import com.example.vacationPayCalculator.dto.VacationCalculationResponse;
import com.example.vacationPayCalculator.exception.InvalidRequestException;
import com.example.vacationPayCalculator.validation.VacationCalculatorValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VacationCalculatorServiceTest {

    @Mock
    private VacationCalculatorValidator validator;
    @InjectMocks
    private VacationCalculatorService calculatorService;

    private final LocalDate START_DATE = LocalDate.of(2025, 5, 1);
    private final LocalDate END_DATE = LocalDate.of(2025, 5, 10);
    private final int VACATION_DAYS_COUNT = 10;
    private final double AVERAGE_SALARY_PER_YEAR = 600_000.0;

    @Test
    void calculate_WithValidDatesRequest_ShouldReturnCorrectAmount() {
        VacationCalculationRequest request = VacationCalculationRequest.builder()
                .averageSalaryPerYear(AVERAGE_SALARY_PER_YEAR)
                .startVacation(START_DATE)
                .finishVacation(END_DATE)
                .build();

        when(validator.isValidData(request)).thenReturn(true);
        when(validator.shouldCalculateByDates(request)).thenReturn(true);
        when(validator.shouldCalculateByDaysOnly(request)).thenReturn(false);

        VacationCalculationResponse response = calculatorService.calculate(request);

        assertEquals(CalculationType.BY_DATES, response.getCalculationType());
        assertEquals("RUB", response.getCurrency());

        assertEquals(new BigDecimal("8219.18"), response.getAmount());
    }

    @Test
    void calculate_WithValidDaysRequest_ShouldReturnCorrectAmount() {
        VacationCalculationRequest request = VacationCalculationRequest.builder()
                .averageSalaryPerYear(AVERAGE_SALARY_PER_YEAR)
                .vacationDaysCount(VACATION_DAYS_COUNT)
                .build();

        when(validator.isValidData(request)).thenReturn(true);
        when(validator.shouldCalculateByDates(request)).thenReturn(false);
        when(validator.shouldCalculateByDaysOnly(request)).thenReturn(true);

        VacationCalculationResponse response = calculatorService.calculate(request);

        assertEquals(CalculationType.BY_DAYS, response.getCalculationType());
        assertEquals("RUB", response.getCurrency());
        assertEquals(16438.36, response.getAmount().doubleValue());
    }

    @Test
    void calculate_WithLeapYearDates_ShouldUseCorrectDaysInYear() {
        LocalDate leapYearStart = LocalDate.of(2024, 2, 26);
        LocalDate leapYearEnd = LocalDate.of(2024, 3, 1);

        VacationCalculationRequest request = VacationCalculationRequest.builder()
                .averageSalaryPerYear(AVERAGE_SALARY_PER_YEAR)
                .startVacation(leapYearStart)
                .finishVacation(leapYearEnd)
                .build();

        when(validator.isValidData(request)).thenReturn(true);
        when(validator.shouldCalculateByDates(request)).thenReturn(true);
        when(validator.shouldCalculateByDaysOnly(request)).thenReturn(false);

        VacationCalculationResponse response = calculatorService.calculate(request);

        assertEquals(new BigDecimal("8196.72"), response.getAmount());
    }

    @Test
    void calculate_WithValidatorThrowingException_ShouldPropagateException() {
        VacationCalculationRequest request = VacationCalculationRequest.builder()
                .averageSalaryPerYear(-1000.0)
                .vacationDaysCount(10)
                .build();

        when(validator.isValidData(request))
                .thenThrow(new InvalidRequestException("Average Salary must be positive!"));

        assertThrows(InvalidRequestException.class, () -> calculatorService.calculate(request));
    }

    @Test
    void calculate_WithJanuaryHolidays_ShouldExcludeAllHolidays() {
        LocalDate januaryStart = LocalDate.of(2025, 1, 1);
        LocalDate januaryEnd = LocalDate.of(2025, 1, 10);

        VacationCalculationRequest request = VacationCalculationRequest.builder()
                .averageSalaryPerYear(AVERAGE_SALARY_PER_YEAR)
                .startVacation(januaryStart)
                .finishVacation(januaryEnd)
                .build();

        when(validator.isValidData(request)).thenReturn(true);
        when(validator.shouldCalculateByDates(request)).thenReturn(true);
        when(validator.shouldCalculateByDaysOnly(request)).thenReturn(false);

        VacationCalculationResponse response = calculatorService.calculate(request);

        assertEquals(new BigDecimal("3287.67"), response.getAmount());
    }

}