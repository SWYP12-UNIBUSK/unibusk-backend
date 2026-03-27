package team.unibusk.backend.global.auth.domain.refreshtoken;

import org.junit.jupiter.api.Test;
import team.unibusk.backend.global.auth.presentation.exception.RefreshTokenNotValidException;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RefreshTokenTest {

    @Test
    void 유효한_토큰과_memberId로_검증하면_예외가_발생하지_않는다() {
        RefreshToken refreshToken = RefreshToken.of(1L, "valid-token", 86400L);

        assertThatCode(() -> refreshToken.validateWith("valid-token", 1L))
                .doesNotThrowAnyException();
    }

    @Test
    void 저장된_토큰과_다른_값으로_검증하면_RefreshTokenNotValidException이_발생한다() {
        RefreshToken refreshToken = RefreshToken.of(1L, "valid-token", 86400L);

        assertThatThrownBy(() -> refreshToken.validateWith("wrong-token", 1L))
                .isInstanceOf(RefreshTokenNotValidException.class);
    }

    @Test
    void 만료된_토큰으로_검증하면_RefreshTokenNotValidException이_발생한다() {
        RefreshToken expiredToken = RefreshToken.of(1L, "valid-token", -1L);

        assertThatThrownBy(() -> expiredToken.validateWith("valid-token", 1L))
                .isInstanceOf(RefreshTokenNotValidException.class);
    }

    @Test
    void 다른_memberId로_검증하면_RefreshTokenNotValidException이_발생한다() {
        RefreshToken refreshToken = RefreshToken.of(1L, "valid-token", 86400L);

        assertThatThrownBy(() -> refreshToken.validateWith("valid-token", 999L))
                .isInstanceOf(RefreshTokenNotValidException.class);
    }

    @Test
    void rotate_호출_시_토큰이_새_값으로_교체되고_이전_토큰은_유효하지_않다() {
        RefreshToken refreshToken = RefreshToken.of(1L, "old-token", 86400L);

        refreshToken.rotate("new-token");

        assertThatCode(() -> refreshToken.validateWith("new-token", 1L))
                .doesNotThrowAnyException();
        assertThatThrownBy(() -> refreshToken.validateWith("old-token", 1L))
                .isInstanceOf(RefreshTokenNotValidException.class);
    }

}
