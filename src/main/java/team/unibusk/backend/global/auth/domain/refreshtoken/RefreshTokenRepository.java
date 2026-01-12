package team.unibusk.backend.global.auth.domain.refreshtoken;

import java.util.Optional;

public interface RefreshTokenRepository {

    void save(RefreshToken refreshToken);

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findFirstByMemberIdOrderByIdDesc(Long id);

    void deleteByMemberId(Long memberId);

}
