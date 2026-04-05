package team.unibusk.backend.global.logging.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;

@Component
@Order(1)
public class MdcFilter implements Filter {

    private static final String HEADER = "X-Request-ID";
    private static final Pattern VALID_TRACE_ID = Pattern.compile("^[A-Za-z0-9_-]{8,64}$");

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        try {
            MDC.put(MdcKey.TRACE_ID, resolveTraceId(httpRequest));
            MDC.put(MdcKey.URI, httpRequest.getRequestURI());
            MDC.put(MdcKey.METHOD, httpRequest.getMethod());
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }

    private String resolveTraceId(HttpServletRequest request) {
        String traceId = request.getHeader(HEADER);
        if (traceId != null && !traceId.isBlank() && VALID_TRACE_ID.matcher(traceId).matches()) {
            return traceId;
        }
        return UUID.randomUUID().toString();
    }

}
