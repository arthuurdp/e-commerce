package com.arthuurdp.e_commerce.infrastructure.security;

import com.arthuurdp.e_commerce.infrastructure.security.annotations.ValidPhone;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneValidator implements ConstraintValidator<ValidPhone, String> {
    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        if (phone == null) return true;

        phone = phone.replaceAll("[^0-9]", ""); // remove formatação

        if (phone.length() < 10 || phone.length() > 11) return false;

        // Valida DDD — existem DDDs de 11 a 99, mas só alguns são válidos
        int ddd = Integer.parseInt(phone.substring(0, 2));
        if (!isValidDdd(ddd)) return false;

        // Celular começa com 9 e tem 11 dígitos
        // Fixo tem 10 dígitos
        if (phone.length() == 11 && phone.charAt(2) != '9') return false;

        return true;
    }

    private boolean isValidDdd(int ddd) {
        int[] validDdds = {
                11, 12, 13, 14, 15, 16, 17, 18, 19, 21, 22, 24, 27, 28, 31, 32, 33, 34, 35, 37, 38, 41, 42, 43, 44, 45, 46, 47, 48, 49, 51, 53, 54, 55, 61, 62, 64, 63, 65, 66, 67, 68, 69, 71, 73, 74, 75, 77, 79, 81, 87, 82, 83, 84, 85, 88, 86, 89, 91, 93, 94, 92, 97, 95, 96, 98, 99
        };

        for (int valid : validDdds) {
            if (ddd == valid) return true;
        }
        return false;
    }
}
