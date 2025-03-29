package com.carrental.annotation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CourseCodeValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Age {
    int min() default 18;
    int max() default 70;
    public String message() default "Age must be between {min} and {max}";
    public Class<?>[] groups() default {};
    public Class<? extends Payload>[] payload() default {};
}


