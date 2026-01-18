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
        return jpaRepository.findByNameContainingOrLocationContaining(keyword, pageable);
    }

    @Override
    public Optional<PerformanceLocation> findByName(String name){
        return jpaRepository.findByName(name);
    }




}
