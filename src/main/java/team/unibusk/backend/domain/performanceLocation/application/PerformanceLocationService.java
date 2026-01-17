package team.unibusk.backend.domain.performanceLocation.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.unibusk.backend.domain.performanceLocation.application.dto.request.PerformanceLocationSearchServiceRequest;
import team.unibusk.backend.domain.performanceLocation.application.dto.response.PerformanceLocationIdResponse;
import team.unibusk.backend.domain.performanceLocation.application.dto.response.PerformanceLocationListResponse;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocationRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PerformanceLocationService {

    private final PerformanceLocationRepository performanceLocationRepository;

    //id로 공연 장소 검색
    public PerformanceLocationIdResponse getPlbyId(Long id ){

        PerformanceLocation performanceLocation =  performanceLocationRepository.findWithImagesById(id);

        return PerformanceLocationIdResponse.from(performanceLocation);
    }

    //공연 장소 전부 조회
    public Page<PerformanceLocationListResponse> getAllPl(Pageable pageable){
        Page<PerformanceLocation> plPage = performanceLocationRepository.findAll(pageable);

        return plPage.map(PerformanceLocationListResponse::from);
    }

    //키워드로 공연 장소 조회
    public  Page<PerformanceLocationListResponse> searchPlByKeyword(
            PerformanceLocationSearchServiceRequest serviceRequset,
            Pageable pageable
    ){
        Page<PerformanceLocation> plPage = performanceLocationRepository.findByKeyword(serviceRequset.keyword(), pageable);

        return plPage.map(PerformanceLocationListResponse::from);
    }

}
