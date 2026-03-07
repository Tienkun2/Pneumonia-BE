package com.medical.pneumonia.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
   validatedBy = {DobValidator.class}
)
public @interface DobConstraint {
    String message() default "Invalid date of birth";

    int min();
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
