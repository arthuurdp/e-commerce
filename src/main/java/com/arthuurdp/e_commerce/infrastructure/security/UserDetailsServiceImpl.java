package com.arthuurdp.e_commerce.infrastructure.security;

import com.arthuurdp.e_commerce.modules.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository repo;

    public UserDetailsServiceImpl(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String credential) throws UsernameNotFoundException {
        boolean isCpf = credential.matches("^\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}$");

        return isCpf ?
                repo.findByCpf(normalizeCpf(credential))
                        .map(UserAuthenticated::new)
                        .orElseThrow(() -> new UsernameNotFoundException("Invalid credentials")) :
                repo.findByEmail(credential.toLowerCase())
                        .map(UserAuthenticated::new)
                        .orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));
    }

    private String normalizeCpf(String cpf) {
        return cpf.replaceAll("[^\\d]", "");
    }
}
