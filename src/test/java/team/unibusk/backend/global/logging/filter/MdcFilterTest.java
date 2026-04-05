package team.unibusk.backend.global.logging.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class MdcFilterTest {

    private final MdcFilter mdcFilter = new MdcFilter();

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void 유효한_X_Request_ID_헤더가_있으면_해당_값을_traceId로_사용한다() throws Exception {
        var request = mock(HttpServletRequest.class);
        var response = mock(HttpServletResponse.class);
        var capturedTraceId = new String[1];

        given(request.getHeader("X-Request-ID")).willReturn("valid-trace-id123");
        given(request.getRequestURI()).willReturn("/test");
        given(request.getMethod()).willReturn("GET");

        mdcFilter.doFilter(request, response, (req, res) -> {
            capturedTraceId[0] = MDC.get(MdcKey.TRACE_ID);
        });

        assertThat(capturedTraceId[0]).isEqualTo("valid-trace-id123");
    }

    @Test
    void X_Request_ID_헤더가_없으면_UUID로_traceId가_생성된다() throws Exception {
        var request = mock(HttpServletRequest.class);
        var response = mock(HttpServletResponse.class);
        var capturedTraceId = new String[1];

        given(request.getHeader("X-Request-ID")).willReturn(null);
        given(request.getRequestURI()).willReturn("/test");
        given(request.getMethod()).willReturn("GET");

        mdcFilter.doFilter(request, response, (req, res) -> {
            capturedTraceId[0] = MDC.get(MdcKey.TRACE_ID);
        });

        assertThat(capturedTraceId[0]).isNotNull();
        assertThat(capturedTraceId[0]).matches("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}");
    }

    @Test
    void X_Request_ID가_blank이면_UUID로_traceId가_생성된다() throws Exception {
        var request = mock(HttpServletRequest.class);
        var response = mock(HttpServletResponse.class);
        var capturedTraceId = new String[1];

        given(request.getHeader("X-Request-ID")).willReturn("   ");
        given(request.getRequestURI()).willReturn("/test");
        given(request.getMethod()).willReturn("GET");

        mdcFilter.doFilter(request, response, (req, res) -> {
            capturedTraceId[0] = MDC.get(MdcKey.TRACE_ID);
        });

        assertThat(capturedTraceId[0]).matches("[a-f0-9\\-]{36}");
    }

    @Test
    void X_Request_ID가_8자_미만이면_UUID로_traceId가_생성된다() throws Exception {
        var request = mock(HttpServletRequest.class);
        var response = mock(HttpServletResponse.class);
        var capturedTraceId = new String[1];

        given(request.getHeader("X-Request-ID")).willReturn("abc");
        given(request.getRequestURI()).willReturn("/test");
        given(request.getMethod()).willReturn("GET");

        mdcFilter.doFilter(request, response, (req, res) -> {
            capturedTraceId[0] = MDC.get(MdcKey.TRACE_ID);
        });

        assertThat(capturedTraceId[0]).matches("[a-f0-9\\-]{36}");
    }

    @Test
    void X_Request_ID가_64자_초과이면_UUID로_traceId가_생성된다() throws Exception {
        var request = mock(HttpServletRequest.class);
        var response = mock(HttpServletResponse.class);
        var capturedTraceId = new String[1];

        given(request.getHeader("X-Request-ID")).willReturn("a".repeat(65));
        given(request.getRequestURI()).willReturn("/test");
        given(request.getMethod()).willReturn("GET");

        mdcFilter.doFilter(request, response, (req, res) -> {
            capturedTraceId[0] = MDC.get(MdcKey.TRACE_ID);
        });

        assertThat(capturedTraceId[0]).matches("[a-f0-9\\-]{36}");
    }

    @Test
    void X_Request_ID가_정확히_8자이면_해당_값을_traceId로_사용한다() throws Exception {
        var request = mock(HttpServletRequest.class);
        var response = mock(HttpServletResponse.class);
        var capturedTraceId = new String[1];

        given(request.getHeader("X-Request-ID")).willReturn("abcd1234");
        given(request.getRequestURI()).willReturn("/test");
        given(request.getMethod()).willReturn("GET");

        mdcFilter.doFilter(request, response, (req, res) -> {
            capturedTraceId[0] = MDC.get(MdcKey.TRACE_ID);
        });

        assertThat(capturedTraceId[0]).isEqualTo("abcd1234");
    }

    @Test
    void X_Request_ID가_정확히_64자이면_해당_값을_traceId로_사용한다() throws Exception {
        var request = mock(HttpServletRequest.class);
        var response = mock(HttpServletResponse.class);
        var capturedTraceId = new String[1];
        var traceId64 = "a".repeat(64);

        given(request.getHeader("X-Request-ID")).willReturn(traceId64);
        given(request.getRequestURI()).willReturn("/test");
        given(request.getMethod()).willReturn("GET");

        mdcFilter.doFilter(request, response, (req, res) -> {
            capturedTraceId[0] = MDC.get(MdcKey.TRACE_ID);
        });

        assertThat(capturedTraceId[0]).isEqualTo(traceId64);
    }

    @Test
    void X_Request_ID에_특수문자가_포함되면_UUID로_traceId가_생성된다() throws Exception {
        var request = mock(HttpServletRequest.class);
        var response = mock(HttpServletResponse.class);
        var capturedTraceId = new String[1];

        given(request.getHeader("X-Request-ID")).willReturn("invalid@trace#id!");
        given(request.getRequestURI()).willReturn("/test");
        given(request.getMethod()).willReturn("GET");

        mdcFilter.doFilter(request, response, (req, res) -> {
            capturedTraceId[0] = MDC.get(MdcKey.TRACE_ID);
        });

        assertThat(capturedTraceId[0]).matches("[a-f0-9\\-]{36}");
    }

    @Test
    void 필터_실행_후_MDC가_초기화된다() throws Exception {
        var request = mock(HttpServletRequest.class);
        var response = mock(HttpServletResponse.class);
        var chain = mock(FilterChain.class);

        given(request.getHeader("X-Request-ID")).willReturn("valid-trace-id123");
        given(request.getRequestURI()).willReturn("/test");
        given(request.getMethod()).willReturn("GET");

        mdcFilter.doFilter(request, response, chain);

        assertThat(MDC.get(MdcKey.TRACE_ID)).isNull();
    }

}
