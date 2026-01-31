package team.unibusk.backend.domain.applicationguide.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team.unibusk.backend.domain.applicationguide.domain.ApplicationGuide;
import team.unibusk.backend.domain.applicationguide.domain.ApplicationGuideRepository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class ApplicationGuideRepositoryImpl implements ApplicationGuideRepository {

    private final ApplicationGuideJpaRepository applicationGuideJpaRepository;

    @Override
    public void saveAll(List<ApplicationGuide> applicationGuides) {
        applicationGuideJpaRepository.saveAll(applicationGuides);
    }

}
