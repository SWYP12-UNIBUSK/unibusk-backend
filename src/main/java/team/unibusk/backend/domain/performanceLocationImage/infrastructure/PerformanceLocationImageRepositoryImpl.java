package team.unibusk.backend.domain.performanceLocationImage.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team.unibusk.backend.domain.performanceLocationImage.domain.PerformanceLocationImage;
import team.unibusk.backend.domain.performanceLocationImage.domain.PerformanceLocationImageRepository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PerformanceLocationImageRepositoryImpl implements PerformanceLocationImageRepository {

    private final PerformanceLocationImageJpaRepository jpaRepository;

    @Override
    public List<PerformanceLocationImage> findAllByPerformanceLocationIdIn(List<Long> performanceLocationIds) {
        return jpaRepository.findAllByPerformanceLocationIdIn(performanceLocationIds);
    }

}
