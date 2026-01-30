package team.unibusk.backend.domain.performanceLocation.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import team.unibusk.backend.domain.performance.presentation.exception.PerformanceLocationNotFoundException;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocationRepository;

import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class PerformanceLocationRepositoryImpl implements PerformanceLocationRepository {

    private final PerformanceLocationJpaRepository performanceLocationJpaRepository;
    private final PerformanceLocationQueryDslRepository performanceLocationQueryDslRepository;

    @Override
    public Page<PerformanceLocation> searchByKeyword(String keyword, Pageable pageable){
        return performanceLocationJpaRepository.searchByNameOrAddress(keyword, pageable);
    }

    @Override
    public PerformanceLocation save(PerformanceLocation performanceLocation){
        return performanceLocationJpaRepository.save(performanceLocation);
    }

    @Override
    public boolean existsByName(String name) {
        return performanceLocationJpaRepository.existsByName(name);
    }

    @Override
    public List<PerformanceLocation> findByIds(Set<Long> locationIds) {
        return performanceLocationJpaRepository.findByIdIn(locationIds);
    }

    @Override
    public PerformanceLocation findById(Long id) {
        return performanceLocationJpaRepository.findById(id)
                .orElseThrow(PerformanceLocationNotFoundException::new);
    }

    @Override
    public List<PerformanceLocation> findInMapBounds(
            Double north,
            Double south,
            Double east,
            Double west
    ) {
        return performanceLocationQueryDslRepository.findInMapBounds(north, south, east, west);
    }

    @Override
    public List<PerformanceLocation> searchByNameOrAddress(String keyword) {
        return performanceLocationQueryDslRepository.searchByNameOrAddress(keyword);
    }

}
