package team.unibusk.backend.domain.PerformanceLocation.infrastructure;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import team.unibusk.backend.domain.PerformanceLocation.domain.PerformanceLocation;



public interface PerformanceLocationJpaRepository extends JpaRepository<PerformanceLocation, Long> {

    // name이 포함된 장소 이름이 포함된 데이터를 페이징 처리
    Page<PerformanceLocation> findByNameContaining(String name, Pageable pageable);

}
