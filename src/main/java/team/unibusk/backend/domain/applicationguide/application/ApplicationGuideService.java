package team.unibusk.backend.domain.applicationguide.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.unibusk.backend.domain.applicationguide.application.dto.response.ApplicationGuideListResponse;
import team.unibusk.backend.domain.applicationguide.domain.ApplicationGuide;
import team.unibusk.backend.domain.applicationguide.domain.ApplicationGuideRepository;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocationRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ApplicationGuideService {

    private final ApplicationGuideRepository applicationGuideRepository;
    private final PerformanceLocationRepository performanceLocationRepository;

    @Transactional(readOnly = true)
    public ApplicationGuideListResponse getApplicationGuides(Long performanceLocationId) {
        performanceLocationRepository.findById(performanceLocationId);

        List<ApplicationGuide> guides =
                applicationGuideRepository.findAllByPerformanceLocationId(performanceLocationId);

        return ApplicationGuideListResponse.from(guides);
    }

}
