package com.aldhafara.genealogicalTree.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    public static final String CORRELATION_ID_MDC_KEY = "correlationId";
    public static final String CORRELATION_ID_REQUEST_ATTRIBUTE = "correlationId";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String correlationId = (String) request.getAttribute(CORRELATION_ID_REQUEST_ATTRIBUTE);

        if (correlationId == null || correlationId.isBlank()) {
            correlationId = Optional.ofNullable(request.getHeader(CORRELATION_ID_HEADER))
                    .filter(value -> !value.isBlank())
                    .orElseGet(() -> UUID.randomUUID().toString());
        }

        MDC.put(CORRELATION_ID_MDC_KEY, correlationId);
        request.setAttribute(CORRELATION_ID_REQUEST_ATTRIBUTE, correlationId);
        response.setHeader(CORRELATION_ID_HEADER, correlationId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(CORRELATION_ID_MDC_KEY);
        }
    }
}
