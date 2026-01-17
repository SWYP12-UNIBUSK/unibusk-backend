package team.unibusk.backend.domain.performanceLocation.infrastructure.plImage;

import org.springframework.data.jpa.repository.JpaRepository;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocationImage;


public interface PerformanceLocationImageJpaRepository extends JpaRepository<PerformanceLocationImage, Long> {

}
