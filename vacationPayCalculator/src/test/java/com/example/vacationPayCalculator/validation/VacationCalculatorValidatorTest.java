package com.example.vacationPayCalculator.validation;

import com.example.vacationPayCalculator.dto.VacationCalculationRequest;
import com.example.vacationPayCalculator.exception.InvalidRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class VacationCalculatorValidatorTest {

    private VacationCalculatorValidator validator;

    @BeforeEach
    void setUp() {
        validator = new VacationCalculatorValidator();
    }

    @Test
    void isValidData_WithValidDatesRequest_ShouldNotThrowException() {
        VacationCalculationRequest request = VacationCalculationRequest.builder()
                .averageSalaryPerYear(600000.0)
                .startVacation(LocalDate.of(2025, 5, 1))
                .finishVacation(LocalDate.of(2025, 5, 10))
                .build();

        assertDoesNotThrow(() -> validator.isValidData(request));
    }

    @Test
    void isValidData_WithValidDaysRequest_ShouldNotThrowException() {
        VacationCalculationRequest request = VacationCalculationRequest.builder()
                .averageSalaryPerYear(600000.0)
                .vacationDaysCount(10)
                .build();

        assertDoesNotThrow(() -> validator.isValidData(request));
    }

    @Test
    void isValidData_WithNullSalary_ShouldThrowException() {
        VacationCalculationRequest request = VacationCalculationRequest.builder()
                .averageSalaryPerYear(null)
                .vacationDaysCount(10)
                .build();

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> validator.isValidData(request)
        );

        assertEquals("Average Salary must be positive!", exception.getMessage());
    }

    @Test
    void isValidData_WithZeroSalary_ShouldThrowException() {
        VacationCalculationRequest request = VacationCalculationRequest.builder()
                .averageSalaryPerYear(0.0)
                .vacationDaysCount(10)
                .build();

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> validator.isValidData(request)
        );

        assertEquals("Average Salary must be positive!", exception.getMessage());
    }

    @Test
    void isValidData_WithNegativeSalary_ShouldThrowException() {
        VacationCalculationRequest request = VacationCalculationRequest.builder()
                .averageSalaryPerYear(-1000.0)
                .vacationDaysCount(10)
                .build();

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> validator.isValidData(request)
        );

        assertEquals("Average Salary must be positive!", exception.getMessage());
    }

    @Test
    void isValidData_WithNullDaysCount_ShouldThrowException() {
        VacationCalculationRequest request = VacationCalculationRequest.builder()
                .averageSalaryPerYear(600000.0)
                .vacationDaysCount(null)
                .startVacation(null)
                .finishVacation(null)
                .build();

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> validator.isValidData(request)
        );

        assertEquals("Vacation Days Count must be positive!", exception.getMessage());
    }

    @Test
    void isValidData_WithZeroDaysCount_ShouldThrowException() {
        VacationCalculationRequest request = VacationCalculationRequest.builder()
                .averageSalaryPerYear(600000.0)
                .vacationDaysCount(0)
                .build();

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> validator.isValidData(request)
        );

        assertEquals("Vacation Days Count must be positive!", exception.getMessage());
    }

    @Test
    void isValidData_WithNegativeDaysCount_ShouldThrowException() {
        VacationCalculationRequest request = VacationCalculationRequest.builder()
                .averageSalaryPerYear(1000.0)
                .vacationDaysCount(-10)
                .build();

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> validator.isValidData(request)
        );

        assertEquals("Vacation Days Count must be positive!", exception.getMessage());
    }

    @Test
    void isValidData_WithInvalidDateRange_ShouldThrowException() {
        VacationCalculationRequest request = VacationCalculationRequest.builder()
                .averageSalaryPerYear(600000.0)
                .startVacation(LocalDate.of(2025, 5, 10))
                .finishVacation(LocalDate.of(2025, 5, 1))
                .build();

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> validator.isValidData(request)
        );

        assertEquals("Start date must be before end date!", exception.getMessage());
    }

    @Test
    void shouldCalculateByDates_WhenDatesProvided_ShouldReturnTrue() {
        VacationCalculationRequest request = VacationCalculationRequest.builder()
                .averageSalaryPerYear(600000.0)
                .startVacation(LocalDate.of(2025, 5, 1))
                .finishVacation(LocalDate.of(2025, 5, 10))
                .build();

        boolean result = validator.shouldCalculateByDates(request);

        assertTrue(result);
    }

    @Test
    void shouldCalculateByDates_WhenOnlySalaryProvided_ShouldReturnFalse() {
        VacationCalculationRequest request = VacationCalculationRequest.builder()
                .averageSalaryPerYear(600000.0)
                .build();

        boolean result = validator.shouldCalculateByDates(request);

        assertFalse(result);
    }

    @Test
    void shouldCalculateByDaysOnly_WhenDaysProvided_ShouldReturnTrue() {
        VacationCalculationRequest request = VacationCalculationRequest.builder()
                .averageSalaryPerYear(600000.0)
                .vacationDaysCount(10)
                .build();

        boolean result = validator.shouldCalculateByDaysOnly(request);

        assertTrue(result);
    }

    @Test
    void shouldCalculateByDaysOnly_WhenDatesProvided_ShouldReturnFalse() {
        VacationCalculationRequest request = VacationCalculationRequest.builder()
                .averageSalaryPerYear(600000.0)
                .startVacation(LocalDate.of(2025, 5, 1))
                .finishVacation(LocalDate.of(2025, 5, 10))
                .build();

        boolean result = validator.shouldCalculateByDaysOnly(request);

        assertFalse(result);
    }

    @Test
    void hasValidSalary_WithPositiveSalary_ShouldReturnTrue() {
        VacationCalculationRequest request = VacationCalculationRequest.builder()
                .averageSalaryPerYear(600000.0)
                .build();

        boolean result = validator.hasValidSalary(request);

        assertTrue(result);
    }

    @Test
    void hasValidVacationDates_WithBothDates_ShouldReturnTrue() {
        VacationCalculationRequest request = VacationCalculationRequest.builder()
                .startVacation(LocalDate.of(2025, 5, 1))
                .finishVacation(LocalDate.of(2025, 5, 10))
                .build();

        boolean result = validator.hasValidVacationDates(request);

        assertTrue(result);
    }

    @Test
    void hasValidVacationDaysCount_WithPositiveDays_ShouldReturnTrue() {
        VacationCalculationRequest request = VacationCalculationRequest.builder()
                .vacationDaysCount(10)
                .build();

        boolean result = validator.hasValidVacationDaysCount(request);

        assertTrue(result);
    }
}
