package com.arthuurdp.e_commerce.infrastructure.security.annotations;

import com.arthuurdp.e_commerce.infrastructure.security.PhoneValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PhoneValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPhone {
    String message() default "Invalid Phone";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
