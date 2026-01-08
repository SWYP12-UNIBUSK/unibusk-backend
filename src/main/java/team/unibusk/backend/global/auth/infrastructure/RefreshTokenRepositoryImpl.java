package team.unibusk.backend.global.auth.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team.unibusk.backend.global.auth.domain.refreshtoken.RefreshToken;
import team.unibusk.backend.global.auth.domain.refreshtoken.RefreshTokenRepository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private final RefreshTokenJpaRepository refreshTokenJpaRepository;

    @Override
    public void save(RefreshToken refreshToken) {
        refreshTokenJpaRepository.save(refreshToken);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenJpaRepository.findByToken(token);
    }

    @Override
    public Optional<RefreshToken> findFirstByMemberIdOrderByIdDesc(Long id) {
        return refreshTokenJpaRepository.findFirstByMemberIdOrderByIdDesc(id);
    }

}
