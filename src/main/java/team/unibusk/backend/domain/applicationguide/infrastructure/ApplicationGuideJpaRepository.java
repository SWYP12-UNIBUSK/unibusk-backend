package team.unibusk.backend.domain.applicationguide.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import team.unibusk.backend.domain.applicationguide.domain.ApplicationGuide;

import java.util.List;

public interface ApplicationGuideJpaRepository extends JpaRepository<ApplicationGuide, Long> {

    List<ApplicationGuide> findAllByPerformanceLocationIdOrderById(Long performanceLocationId);

}
