package team.unibusk.backend.global.auth.application.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import team.unibusk.backend.global.auth.application.model.AuthCodePayload;
import team.unibusk.backend.global.auth.presentation.exception.InvalidAuthCodeException;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class AuthCodeRedisService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final Duration TTL = Duration.ofMinutes(3);

    public void save(String code, Long memberId, boolean firstLogin) {

        redisTemplate.opsForValue().set(
                "OAUTH:" + code,
                memberId + ":" + firstLogin,
                TTL
        );

    }

    public AuthCodePayload consume(String code) {
        String key = "OAUTH:" + code;
        String value = redisTemplate.opsForValue().get(key);

        validateNullCheck(value);

        redisTemplate.delete(key);

        String[] split = value.split(":");

        return AuthCodePayload.builder()
                .memberId(Long.valueOf(split[0]))
                .firstLogin(Boolean.parseBoolean(split[1]))
                .build();
    }

    private void validateNullCheck(String value) {
        if (value == null) {
            throw new InvalidAuthCodeException();
        }
    }

}