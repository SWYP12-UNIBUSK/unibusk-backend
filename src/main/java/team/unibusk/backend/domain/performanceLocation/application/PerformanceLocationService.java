package team.unibusk.backend.domain.performanceLocation.application;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.unibusk.backend.domain.performanceLocation.application.dto.response.PerformanceLocationListResponse;
import team.unibusk.backend.domain.performanceLocation.application.dto.response.PerformanceLocationMapListResponse;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocationRepository;
import team.unibusk.backend.domain.performanceLocation.presentation.exception.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PerformanceLocationService {

    private final PerformanceLocationRepository performanceLocationRepository;

    private static final double MIN_LATITUDE = -90.0;
    private static final double MAX_LATITUDE = 90.0;
    private static final double MIN_LONGITUDE = -180.0;
    private static final double MAX_LONGITUDE = 180.0;

    @Transactional(readOnly = true)
    public PerformanceLocationListResponse findByKeyword(String keyword, Pageable pageable){

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
        validateAll(north, south, east, west);

        List<PerformanceLocation> performanceLocations = performanceLocationRepository.findInMapBounds(north, south, east, west);

        return PerformanceLocationMapListResponse.from(performanceLocations);
    }

    //keyword에 대한 검증. (길이, null 검사)
    private void validateKeyword(String keyword){
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new EmptyKeywordException();
        }
        if (keyword.length() > 255) {
            throw new InvalidKeywordLengthException();
        }
    }

    public static void validateAll(Double north, Double south, Double east, Double west) {
        validateNotNull(north, south, east, west);
        validateOrder(north, south, east, west);
        validateRange(north, south, east, west);
    }

    private static void validateNotNull(Double north, Double south, Double east, Double west) {
        if (north == null || south == null || east == null || west == null) {
            throw new NullMapBoundsException();
        }
    }

    private static void validateOrder(Double north, Double south, Double east, Double west) {
        if (north < south || east < west) {
            throw new InvalidMapBoundsException();
        }
    }

    private static void validateRange(Double north, Double south, Double east, Double west) {
        if (north > MAX_LATITUDE || south < MIN_LATITUDE) {
            throw new OutOfRangeLatitudeException();
        }
        if (east > MAX_LONGITUDE || west < MIN_LONGITUDE) {
            throw new OutOfRangeLongitudeException();
        }
    }

}
