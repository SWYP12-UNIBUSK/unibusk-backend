package team.unibusk.backend.domain.performanceLocation.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocationRepository;

@Repository
@RequiredArgsConstructor
public class PerformanceLocationRepositoryImpl implements PerformanceLocationRepository {

    private final PerformanceLocationJpaRepository performanceLocationJpaRepository;

    @Override
    public Page<PerformanceLocation> searchByKeyword(String keyword, Pageable pageable){
        return performanceLocationJpaRepository.searchByNameOrAddress(keyword, pageable);
    }

    @Override
    public PerformanceLocation save(PerformanceLocation performanceLocation){
        return performanceLocationJpaRepository.save(performanceLocation);
    }

    @Override
    public boolean existsByName(String name){
        return performanceLocationJpaRepository.existsByName(name);
    }

    @Override
    public boolean existsByAddress(String address){
        return performanceLocationJpaRepository.existsByAddress(address);
    }
}
