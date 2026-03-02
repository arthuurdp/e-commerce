package com.arthuurdp.e_commerce.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

/**
 * O Stripe valida a assinatura do webhook contra o body RAW da requisição.
 * Este filtro garante que o body pode ser lido mais de uma vez sem ser consumido.
 */
@Configuration
public class WebhookConfig {

    @Bean
    public OncePerRequestFilter contentCachingFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain filterChain)
                    throws ServletException, IOException {
                if (request.getRequestURI().startsWith("/webhook/")) {
                    filterChain.doFilter(new ContentCachingRequestWrapper(request), response);
                } else {
                    filterChain.doFilter(request, response);
                }
            }
        };
    }
}