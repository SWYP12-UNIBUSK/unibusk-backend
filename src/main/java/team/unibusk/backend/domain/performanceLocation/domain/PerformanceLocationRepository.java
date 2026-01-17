package team.unibusk.backend.domain.performanceLocation.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PerformanceLocationRepository {

    //-----------이미지 url 포함--------------
    //모든 PerformanceLocation 제이징하여 반환 (이미지 포함)
    Page<PerformanceLocation> findAll(Pageable pageable);

    //특정 키워드로 검색하여 페이징 반환 (이미지 포함)

    Page<PerformanceLocation> findByKeyword(String keyword, Pageable pageable);

    //Id 로 상세 정보 조화 (이미지 포함)

    PerformanceLocation findWithImagesById(Long id);

}
