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

    //name으로 키워드 검색 (대소문자 구분 없음)
    @Override
    public Page<PerformanceLocation> findByNameContatinin(String name, Pageable pageable){
        return performanceLocationJpaRepository.findByNameContainingIgnoreCase(name, pageable);
    }


}
