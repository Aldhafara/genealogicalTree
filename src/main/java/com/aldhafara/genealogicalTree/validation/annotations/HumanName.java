package com.aldhafara.genealogicalTree.validation.annotations;

import com.aldhafara.genealogicalTree.validation.ValidationPatterns;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = {})
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Size(min = 2, max = 255, message = "{validation.humanName.size}")
@Pattern(
        regexp = ValidationPatterns.HUMAN_NAME,
        message = "{validation.humanName.invalid}"
)
public @interface HumanName {

    String message() default "{validation.humanName.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}