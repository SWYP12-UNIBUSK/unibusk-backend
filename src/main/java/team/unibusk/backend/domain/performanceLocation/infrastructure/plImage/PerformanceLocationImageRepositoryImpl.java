package team.unibusk.backend.domain.performanceLocation.infrastructure.plImage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocationImageRepository;

@Repository
@RequiredArgsConstructor
public class PerformanceLocationImageRepositoryImpl implements PerformanceLocationImageRepository {

    private final PerformanceLocationImageJpaRepository jpaRepository;

}
