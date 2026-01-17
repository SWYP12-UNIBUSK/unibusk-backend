package team.unibusk.backend.domain.performanceLocation.infrastructure.pl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocationRepository;
import team.unibusk.backend.domain.performanceLocation.presentation.exception.PerformanceLocationNotFoundException;

@Repository
@RequiredArgsConstructor
public class PerformanceLocationRepositoryImpl implements PerformanceLocationRepository {

    private final PerformanceLocationJpaRepository jpaRepository;

    //--- 이미지 미포함 ---
    @Override
    public Page<PerformanceLocation> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable);
    }

    @Override
    public Page<PerformanceLocation> findByKeyword(String keyword, Pageable pageable) {
        return jpaRepository.findByNameContainingOrLocationContaining(keyword, pageable);
    }

    @Override
    public PerformanceLocation findWithImagesById(Long id) {
        return jpaRepository.findWithImagesById(id)
                .orElseThrow(PerformanceLocationNotFoundException::new);
    }



}
