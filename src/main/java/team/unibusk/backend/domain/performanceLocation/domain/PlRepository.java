package team.unibusk.backend.domain.performanceLocation.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Optional;

public interface PlRepository {

    //-----------이미지 url 포함--------------
    //모든 Pl 제이징하여 반환 (이미지 포함)
    Page<Pl> findAll(Pageable pageable);

    //특정 키워드로 검색하여 페이징 반환 (이미지 포함)

    Page<Pl> findByKeyword(String keyword, Pageable pageable);

    //Id 로 상세 정보 조화 (이미지 포함)

    Pl findWithImagesById(Long id);

}
