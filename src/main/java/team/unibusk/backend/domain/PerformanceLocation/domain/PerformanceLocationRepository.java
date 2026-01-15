package team.unibusk.backend.domain.PerformanceLocation.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PerformanceLocationRepository {

    Optional<PerformanceLocation> findById(Long performanceLocationId);

    Page<PerformanceLocation> findByNameContaining(String keyword, Pageable pageable);

    Page<PerformanceLocation> findAll(Pageable pageable);
}
