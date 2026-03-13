package com.arthuurdp.e_commerce.infrastructure.security;

import com.arthuurdp.e_commerce.shared.StandardError;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {
    private static final Map<String, Bucket> BUCKETS = new ConcurrentHashMap<>();

    private static final Map<String, int[]> LIMITS = Map.of(
            "/auth/login",          new int[]{5, 1},
            "/password/forgot",     new int[]{5, 1},
            "/verify-email/send",   new int[]{5, 1}
    );

    private final ObjectMapper objectMapper;

    public RateLimitFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        int[] limit = LIMITS.get(path);

        if (limit == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String ip = getClientIp(request);
        String key = path + ":" + ip;

        Bucket bucket = BUCKETS.computeIfAbsent(key, k -> Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(limit[0])
                        .refillGreedy(limit[0], Duration.ofMinutes(limit[1]))
                        .build())
                .build());

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            StandardError error = new StandardError(
                    Instant.now(),
                    HttpStatus.TOO_MANY_REQUESTS.value(),
                    "Too Many Requests",
                    "You have exceeded the rate limit for this resource"
                    );
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(error));
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}