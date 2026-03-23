package team.unibusk.backend.global.auth.application.auth;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import team.unibusk.backend.global.auth.application.model.AuthCodePayload;
import team.unibusk.backend.global.auth.presentation.exception.InvalidAuthCodeException;
import team.unibusk.backend.global.support.UnitTestSupport;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

class AuthCodeRedisServiceTest extends UnitTestSupport {

    @InjectMocks
    private AuthCodeRedisService authCodeRedisService;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Test
    void 인증코드를_저장하면_OAUTH_접두사_키로_TTL_3분과_함께_Redis에_저장된다() {
        given(redisTemplate.opsForValue()).willReturn(valueOperations);

        authCodeRedisService.save("test-code", 1L, true);

        then(valueOperations).should().set(
                eq("OAUTH:test-code"),
                eq("1:true"),
                eq(Duration.ofMinutes(3))
        );
    }

    @Test
    void 인증코드를_사용하면_memberId와_firstLogin이_파싱된_payload가_반환된다() {
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.getAndDelete("OAUTH:valid-code")).willReturn("1:false");

        AuthCodePayload payload = authCodeRedisService.consume("valid-code");

        assertThat(payload.memberId()).isEqualTo(1L);
        assertThat(payload.firstLogin()).isFalse();
    }

    @Test
    void 최초_로그인_인증코드를_사용하면_firstLogin이_true인_payload가_반환된다() {
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.getAndDelete("OAUTH:first-code")).willReturn("2:true");

        AuthCodePayload payload = authCodeRedisService.consume("first-code");

        assertThat(payload.memberId()).isEqualTo(2L);
        assertThat(payload.firstLogin()).isTrue();
    }

    @Test
    void 존재하지_않는_인증코드를_사용하면_InvalidAuthCodeException이_발생한다() {
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.getAndDelete("OAUTH:invalid-code")).willReturn(null);

        assertThatThrownBy(() -> authCodeRedisService.consume("invalid-code"))
                .isInstanceOf(InvalidAuthCodeException.class);
    }

    @Test
    void 이미_사용된_인증코드를_재사용하면_InvalidAuthCodeException이_발생한다() {
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.getAndDelete("OAUTH:one-time-code")).willReturn(null);

        assertThatThrownBy(() -> authCodeRedisService.consume("one-time-code"))
                .isInstanceOf(InvalidAuthCodeException.class);
    }

}
