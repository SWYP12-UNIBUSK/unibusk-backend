package team.unibusk.backend.global.auth.domain.refreshtoken;

import jakarta.persistence.*;
import lombok.*;
import team.unibusk.backend.global.auth.presentation.exception.RefreshTokenNotValidException;
import team.unibusk.backend.global.domain.BaseTimeEntity;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class RefreshToken extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_id")
    private Long id;

    private Long memberId;

    private String token;

    private LocalDateTime expiredAt;

    public static RefreshToken of(Long memberId, String token, long expiredSeconds) {
        LocalDateTime expiredAt = LocalDateTime.now().plusSeconds(expiredSeconds);

        return RefreshToken.builder()
                .memberId(memberId)
                .token(token)
                .expiredAt(expiredAt)
                .build();
    }

    public void rotate(String token) {
        this.token = token;
    }

    public void updateExpiration(long expiredSeconds) {
        expiredAt = LocalDateTime.now().plusSeconds(expiredSeconds);
    }

    public void validateWith(String token, Long memberId) {
        if (isNotMatchedToken(token) || isExpired() || isNotMatchedUserId(memberId)) {
            throw new RefreshTokenNotValidException();
        }
    }

    private boolean isNotMatchedToken(String token) {
        return !Objects.equals(this.token, token);
    }

    private boolean isExpired() {
        return expiredAt.isBefore(LocalDateTime.now());
    }

    private boolean isNotMatchedUserId(Long memberId) {
        return !Objects.equals(this.memberId, memberId);
    }

}
