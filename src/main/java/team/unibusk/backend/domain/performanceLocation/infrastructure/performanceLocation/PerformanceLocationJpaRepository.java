package team.unibusk.backend.domain.performanceLocation.infrastructure.performanceLocation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;

import java.util.Optional;


public interface PerformanceLocationJpaRepository extends JpaRepository<PerformanceLocation, Long> {

    Optional<PerformanceLocation> findByName(@Param("name") String name);

    Page<PerformanceLocation> findByNameContaining(String keyword, Pageable pageable);

}
