package com.tgerstel.quizmaster.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class LoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        ContentCachingRequestWrapper wrappedRequest =
                new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse =
                new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } catch (Exception ex) {
            log.error("Exception during request processing", ex);
            throw ex;
        } finally {
            long duration = System.currentTimeMillis() - startTime;

            logRequest(wrappedRequest, duration);
            logResponse(wrappedResponse);

            wrappedResponse.copyBodyToResponse();
        }
    }

    private void logRequest(ContentCachingRequestWrapper request, long duration) {
        String rawBody = getPayload(request.getContentAsByteArray());
        String safeBody = LogSanitizer.sanitize(rawBody);
        log.info(
                "➡️ {} {} | params={} | body={} | time={}ms",
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString(),
                safeBody,
                duration
        );
    }

    private void logResponse(ContentCachingResponseWrapper response) {
        log.info(
                "⬅️ status={}",
                response.getStatus()
        );
    }

    private String getPayload(byte[] buf) {
        if (buf == null || buf.length == 0) {
            return "";
        }
        return new String(buf, StandardCharsets.UTF_8);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/actuator")
                || path.startsWith("/swagger")
                || path.startsWith("/v3/api-docs");
    }
}
