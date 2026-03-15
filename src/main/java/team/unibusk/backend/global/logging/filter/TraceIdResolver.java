package team.unibusk.backend.global.logging.filter;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.regex.Pattern;

@Component
public class TraceIdResolver {

    private static final String HEADER = "X-Request-ID";
    private static final Pattern VALID_TRACE_ID = Pattern.compile("^[a-zA-Z0-9\\-]{8,32}$");

    public String resolve(HttpServletRequest request) {
        String traceId = request.getHeader(HEADER);
        if (isValid(traceId)) {
            return traceId;
        }
        return UUID.randomUUID().toString();
    }

    private boolean isValid(String traceId) {
        if (traceId == null || traceId.isBlank()) {
            return false;
        }
        return VALID_TRACE_ID.matcher(traceId).matches();
    }

}