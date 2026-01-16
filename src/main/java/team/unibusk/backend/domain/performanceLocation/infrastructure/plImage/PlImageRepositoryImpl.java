package team.unibusk.backend.domain.performanceLocation.infrastructure.plImage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team.unibusk.backend.domain.performanceLocation.domain.PlImageRepository;

@Repository
@RequiredArgsConstructor
public class PlImageRepositoryImpl implements PlImageRepository {

    private final PlImageJpaRepository jpaRepository;

}
