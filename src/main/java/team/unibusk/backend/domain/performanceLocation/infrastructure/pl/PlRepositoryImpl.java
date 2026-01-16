package team.unibusk.backend.domain.performanceLocation.infrastructure.pl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import team.unibusk.backend.domain.performanceLocation.domain.Pl;
import team.unibusk.backend.domain.performanceLocation.domain.PlRepository;
import team.unibusk.backend.domain.performanceLocation.presentation.exception.PlNotFoundException;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PlRepositoryImpl implements PlRepository {

    private final PlJpaRepository jpaRepository;

    //--- 이미지 미포함 ---
    @Override
    public Page<Pl> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable);
    }

    @Override
    public Page<Pl> findByKeyword(String keyword, Pageable pageable) {
        return jpaRepository.findByNameContainingOrLocationContaining(keyword, pageable);
    }

    @Override
    public Pl findWithImagesById(Long id) {
        return jpaRepository.findWithImagesById(id)
                .orElseThrow(PlNotFoundException::new);
    }



}
