package com.arthuurdp.e_commerce.infrastructure.security;

import com.arthuurdp.e_commerce.repositories.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository repo;

    public CustomUserDetailsService(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String credential) throws UsernameNotFoundException {
        boolean isCpf = credential.matches("^\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}$");

        UserDetails user = isCpf ? repo.findByCpf(normalizeCpf(credential)) : repo.findByEmail(credential.toLowerCase());

        if (user == null) {
            throw new UsernameNotFoundException("Invalid credentials");
        }

        return user;
    }

    public String normalizeCpf(String cpf) {
        return cpf.replaceAll("[^\\d]", "");
    }
}
