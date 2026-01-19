package com.example.vacationPayCalculator.service;

import com.example.vacationPayCalculator.dto.CalculationType;
import com.example.vacationPayCalculator.dto.VacationCalculationRequest;
import com.example.vacationPayCalculator.dto.VacationCalculationResponse;
import com.example.vacationPayCalculator.validation.VacationCalculatorValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class VacationCalculatorService {

    private final VacationCalculatorValidator validator;

    public VacationCalculationResponse calculate(VacationCalculationRequest request) {
        validator.isValidData(request);
        VacationCalculationResponse response = new VacationCalculationResponse();
        if (validator.shouldCalculateByDates(request)) {
            response.setAmount(calculateByDates(calculateAverageSalaryPerDay(request),
                    request));
            response.setCalculationType(CalculationType.BY_DATES);
        }
        if (validator.shouldCalculateByDaysOnly(request)) {
            response.setAmount(calculateByDays(calculateAverageSalaryPerDay(request),
                    request));
            response.setCalculationType(CalculationType.BY_DAYS);
        }
        response.setCurrency("RUB");
        return response;
    }

    private BigDecimal calculateAverageSalaryPerDay(VacationCalculationRequest request) {
        int year;
        if (request.getStartVacation() != null) {
            year = request.getStartVacation().getYear();
        } else {
            year = LocalDate.now().getYear();
        }
        return BigDecimal.valueOf(request.getAverageSalaryPerYear())
                .divide(BigDecimal.valueOf(getDaysInYear(year)), 10, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateByDays(BigDecimal averageSalaryPerDay, VacationCalculationRequest request) {
        return averageSalaryPerDay
                .multiply(BigDecimal.valueOf(request.getVacationDaysCount()))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateByDates(BigDecimal averageSalaryPerDay, VacationCalculationRequest request) {
        return averageSalaryPerDay
                .multiply(
                        BigDecimal.valueOf(
                                calculateEffectiveVacationDays(
                                        request.getStartVacation(),
                                        request.getFinishVacation(),
                                        getHolidays(request.getStartVacation().getYear())
                                )
                        )
                ).setScale(2, RoundingMode.HALF_UP);
    }
    private long calculateEffectiveVacationDays(LocalDate startDate, LocalDate endDate, List<LocalDate> holidays) {
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        long weekends = countWeekends(startDate, endDate);
        long holidayCount = countHolidays(startDate, endDate, holidays);

        return totalDays - weekends - holidayCount;
    }

    private long countWeekends(LocalDate startDate, LocalDate endDate) {
        long weekends = 0;
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                weekends++;
            }
        }
        return weekends;
    }

    private long countHolidays(LocalDate startDate, LocalDate endDate, List<LocalDate> holidays) {
        return holidays.stream()
                .filter(date -> !date.isBefore(startDate) && !date.isAfter(endDate))
                .count();
    }

    public static List<LocalDate> getHolidays(int year) {
        List<LocalDate> holidays = new ArrayList<>();
        holidays.add(LocalDate.of(year, 1, 1));
        holidays.add(LocalDate.of(year, 1, 2));
        holidays.add(LocalDate.of(year, 1, 3));
        holidays.add(LocalDate.of(year, 1, 4));
        holidays.add(LocalDate.of(year, 1, 5));
        holidays.add(LocalDate.of(year, 1, 7));
        holidays.add(LocalDate.of(year, 2, 23));
        holidays.add(LocalDate.of(year, 3, 8));
        holidays.add(LocalDate.of(year, 5, 1));
        holidays.add(LocalDate.of(year, 5, 9));
        holidays.add(LocalDate.of(year, 6, 12));
        holidays.add(LocalDate.of(year, 11, 4));
        return holidays;
    }

    private int getDaysInYear(int year) {
        return Year.of(year).isLeap() ? 366 : 365;
    }


}
