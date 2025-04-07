package com.example.vacationPayCalculator.controller;

import com.example.vacationPayCalculator.dto.ErrorResponse;
import com.example.vacationPayCalculator.dto.VacationCalculationRequest;
import com.example.vacationPayCalculator.dto.VacationCalculationResponse;
import com.example.vacationPayCalculator.exception.InvalidRequestException;
import com.example.vacationPayCalculator.service.VacationCalculatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class VacationCalculatorController {

    private final VacationCalculatorService vacationCalculatorService;

    @GetMapping("/calculate")
    public ResponseEntity<?> calculateVacation(@ModelAttribute VacationCalculationRequest vacationCalculationRequest) {
       try {
           VacationCalculationResponse response = vacationCalculatorService.calculate(vacationCalculationRequest);
           return ResponseEntity.ok(response);
       } catch (InvalidRequestException e) {
           return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
       }

    }
}
