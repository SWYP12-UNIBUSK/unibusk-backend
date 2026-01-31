package team.unibusk.backend.domain.applicationguide.domain;

import java.util.List;

public interface ApplicationGuideRepository {

    void saveAll(List<ApplicationGuide> applicationGuides);

    List<ApplicationGuide> findAllByPerformanceLocationId(Long performanceLocationId);

}
