package team.unibusk.backend.domain.performanceLocation.application;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.unibusk.backend.domain.performanceLocation.application.dto.requset.PerformanceLocationListServiceRequest;
import team.unibusk.backend.domain.performanceLocation.application.dto.response.PerformanceLocationListResponse;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocationRepository;

@Service
@RequiredArgsConstructor
public class PerformanceLocationService {

    private final PerformanceLocationRepository performanceLocationRepository;

    @Transactional(readOnly = true)
    public PerformanceLocationListResponse getLocations(PerformanceLocationListServiceRequest request){
        //direction 설정
        Sort.Direction direction = request.isDesc() ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(
                request.page(),
                request.size(),
                Sort.by(direction, request.sort())
        );

        Page<PerformanceLocation> locations = performanceLocationRepository.findAll(pageable);

        return PerformanceLocationListResponse.from(locations);
    }


}
