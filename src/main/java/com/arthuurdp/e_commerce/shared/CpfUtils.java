package com.arthuurdp.e_commerce.shared;

public class CpfUtils {
    public static String normalize(String cpf) {
        return cpf.replaceAll("[^\\d]", "");
    }
}