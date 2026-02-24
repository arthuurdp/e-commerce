package com.arthuurdp.e_commerce.infrastructure.security;

import com.arthuurdp.e_commerce.infrastructure.security.annotations.ValidCpf;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfValidator implements ConstraintValidator<ValidCpf, String> {
    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext constraintValidatorContext) {
        if (cpf == null) return true;

        cpf = cpf.replaceAll("[^0-9]", ""); // remove formatação se vier com pontos/traço

        if (cpf.length() != 11) return false;
        if (cpf.matches("(\\d)\\1{10}")) return false; // rejeita "11111111111", "00000000000", etc.

        // Valida primeiro dígito verificador
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += (cpf.charAt(i) - '0') * (10 - i);
        }
        int first = 11 - (sum % 11);
        if (first >= 10) first = 0;
        if (first != (cpf.charAt(9) - '0')) return false;

        // Valida segundo dígito verificador
        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += (cpf.charAt(i) - '0') * (11 - i);
        }
        int second = 11 - (sum % 11);
        if (second >= 10) second = 0;
        return second == (cpf.charAt(10) - '0');
    }
}
