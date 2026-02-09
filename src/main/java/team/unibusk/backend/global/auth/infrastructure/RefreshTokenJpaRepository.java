package team.unibusk.backend.global.auth.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import team.unibusk.backend.global.auth.domain.refreshtoken.RefreshToken;

import java.util.Optional;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findFirstByMemberIdOrderByIdDesc(Long id);

    void deleteByMemberId(Long memberId);

}
