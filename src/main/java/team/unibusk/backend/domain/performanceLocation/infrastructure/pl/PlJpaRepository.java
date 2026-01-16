package team.unibusk.backend.domain.performanceLocation.infrastructure.pl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import team.unibusk.backend.domain.performanceLocation.domain.Pl;

import java.util.Optional;

@Repository
public interface PlJpaRepository extends JpaRepository<Pl, Long> {

    // [이미지 포함] 키워드 검색
    @EntityGraph(attributePaths = {"images"})
    @Query("select p from Pl p where p.name like %:keyword% or p.location like %:keyword%")
    Page<Pl> findByNameContainingOrLocationContaining(@Param("keyword") String keyword,  Pageable pageable);

    // [이미지 포함] 상세 조회
    @EntityGraph(attributePaths = {"images"})
    Optional<Pl> findWithImagesById(Long id);

    // [이미지 포함] 전체 조회
    @EntityGraph(attributePaths = {"images"})
    @Query("select p from Pl p")
    Page<Pl> findAll(Pageable pageable);
}
