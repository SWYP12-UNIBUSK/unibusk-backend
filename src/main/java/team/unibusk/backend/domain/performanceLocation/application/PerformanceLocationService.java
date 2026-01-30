package team.unibusk.backend.domain.performanceLocation.application;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.unibusk.backend.domain.performanceLocation.application.dto.response.PerformanceLocationListResponse;
import team.unibusk.backend.domain.performanceLocation.application.dto.response.PerformanceLocationMapListResponse;
import team.unibusk.backend.domain.performanceLocation.domain.MapBounds;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocationRepository;
import team.unibusk.backend.domain.performanceLocation.presentation.exception.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PerformanceLocationService {

    private final PerformanceLocationRepository performanceLocationRepository;

    @Transactional(readOnly = true)
    public PerformanceLocationListResponse findByKeyword(String keyword, Pageable pageable) {

        //keyword에 대한 검증
        validateKeyword(keyword);

        return PerformanceLocationListResponse.from(performanceLocationRepository.searchByKeyword(keyword, pageable));
    }

    @Transactional(readOnly = true)
    public PerformanceLocationMapListResponse findInMapBoundsResponse(
            Double north,
            Double south,
            Double east,
            Double west
    ) {
        MapBounds bounds = new MapBounds(north, south, east, west);

        List<PerformanceLocation> performanceLocations = performanceLocationRepository.findInMapBounds(north, south, east, west);

        return PerformanceLocationMapListResponse.from(performanceLocations);
    }

    //keyword에 대한 검증. (길이, null 검사)
    private void validateKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new EmptyKeywordException();
        }
        if (keyword.length() > 255) {
            throw new InvalidKeywordLengthException();
        }
    }

}
