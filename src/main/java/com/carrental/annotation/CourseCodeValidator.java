package com.carrental.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Period;

public class CourseCodeValidator implements ConstraintValidator<Age, LocalDate> {

    private int minAge;
    private int maxAge;

    @Override
    public void initialize(Age constraintAnnotation) {
        this.minAge = constraintAnnotation.min();
        this.maxAge = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        if (localDate == null) {
            return true;
        }

        int age = Period.between(localDate, LocalDate.now()).getYears();
        return age >= minAge && age <= maxAge;
    }

    }
