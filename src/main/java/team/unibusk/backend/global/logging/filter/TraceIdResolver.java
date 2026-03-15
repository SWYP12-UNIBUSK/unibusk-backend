package team.unibusk.backend.global.logging.filter;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TraceIdResolver {

    private static final String HEADER = "X-Request-ID";

    public String resolve(HttpServletRequest request) {
        String traceId = request.getHeader(HEADER);
        if (traceId == null || traceId.isBlank()) {
            return UUID.randomUUID().toString().substring(0, 8);
        }
        return traceId;
    }

}