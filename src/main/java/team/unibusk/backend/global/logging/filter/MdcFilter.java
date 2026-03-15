package team.unibusk.backend.global.logging.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1)
@RequiredArgsConstructor
public class MdcFilter implements Filter {

    private final TraceIdResolver traceIdResolver;

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        try {
            MDC.put(MdcKey.TRACE_ID, traceIdResolver.resolve(httpRequest));
            MDC.put(MdcKey.URI, httpRequest.getRequestURI());
            MDC.put(MdcKey.METHOD, httpRequest.getMethod());
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }

}