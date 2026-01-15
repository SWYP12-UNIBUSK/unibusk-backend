package team.unibusk.backend.domain.PerformanceLocation.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import team.unibusk.backend.domain.PerformanceLocation.domain.PerformanceLocation;
import team.unibusk.backend.domain.PerformanceLocation.domain.PerformanceLocationRepository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class PerformanceLocationRepositoryImpl implements PerformanceLocationRepository {

    private final PerformanceLocationJpaRepository performanceLocationJpaRepository;

    @Override
    public Optional<PerformanceLocation> findById(Long performanceLocationId) {
        return performanceLocationJpaRepository.findById(performanceLocationId);
    }

    @Override
    public Page<PerformanceLocation> findByNameContaining(String name, Pageable pageable){
        return performanceLocationJpaRepository.findByNameContaining(name, pageable);
    }

    @Override
    public Page<PerformanceLocation> findAll(Pageable pageable){
        return performanceLocationJpaRepository.findAll(pageable);
    }
}
