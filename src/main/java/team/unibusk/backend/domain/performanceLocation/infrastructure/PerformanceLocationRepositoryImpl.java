package team.unibusk.backend.domain.performanceLocation.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocationRepository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PerformanceLocationRepositoryImpl implements PerformanceLocationRepository {

    private final PerformanceLocationJpaRepository jpaRepository;

    @Override
    public Page<PerformanceLocation> findByKeywordContaining(String keyword, Pageable pageable) {
        return jpaRepository.findByNameContaining(keyword, pageable);
    }

    @Override
    public Optional<PerformanceLocation> findByName(String name) {
        // 서비스에서 호출 시 Fetch Join이 적용된 최적화 메서드 사용
        return jpaRepository.findByNameWithImages(name);
    }
}