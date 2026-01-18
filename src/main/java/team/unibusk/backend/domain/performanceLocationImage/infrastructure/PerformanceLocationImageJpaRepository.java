package team.unibusk.backend.domain.performanceLocationImage.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import team.unibusk.backend.domain.performanceLocationImage.domain.PerformanceLocationImage;

import java.util.List;


public interface PerformanceLocationImageJpaRepository extends JpaRepository<PerformanceLocationImage, Long> {

    List<PerformanceLocationImage> findAllByPerformanceLocationIdIn(List<Long> performanceLocationIds);

}
