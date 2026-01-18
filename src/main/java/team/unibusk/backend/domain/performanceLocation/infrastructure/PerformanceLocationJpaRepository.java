package team.unibusk.backend.domain.performanceLocation.infrastructure;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;

import java.util.Optional;


public interface PerformanceLocationJpaRepository extends JpaRepository<PerformanceLocation, Long> {

    // 키워드 검색
    @Query("select p from PerformanceLocation p where p.name like %:keyword% or p.location like %:keyword%")
    Page<PerformanceLocation> findByNameContainingOrLocationContaining(@Param("keyword") String keyword, Pageable pageable);

    //name으로 검색
    Optional<PerformanceLocation> findByName(String name);

}
