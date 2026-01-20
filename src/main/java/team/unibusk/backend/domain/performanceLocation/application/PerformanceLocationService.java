package team.unibusk.backend.domain.performanceLocation.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.unibusk.backend.domain.performanceLocation.application.dto.request.PerformanceLocationSearchServiceRequest;
import team.unibusk.backend.domain.performanceLocation.application.dto.response.PerformanceLocationNameResponse;
import team.unibusk.backend.domain.performanceLocation.application.dto.response.PerformanceLocationSearchResponse;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;
import team.unibusk.backend.domain.performanceLocation.presentation.exception.PerformanceLocationNotFoundException;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocationImage;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocationRepository;


import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PerformanceLocationService {

    private final PerformanceLocationRepository performanceLocationRepository;

    //name으로 공연 장소 검색
    public PerformanceLocationNameResponse searchByNamePerformanceLocationService(
            String name
    ){
        //이름으로 장소 검색
        PerformanceLocation location = performanceLocationRepository.findByName(name)
                        .orElseThrow(PerformanceLocationNotFoundException::new);

        //이미지 가져오기
        List<String> imageUrls = location.getImages().stream()
                .map(PerformanceLocationImage::getImageUrl)
                .toList();

        return PerformanceLocationNameResponse.from(location, imageUrls);
    }



    //키워드로 공연 장소 조회
    public  Page<PerformanceLocationSearchResponse> searchByKeywordPerformanceLocationsService(
            PerformanceLocationSearchServiceRequest serviceRequest
    ){

        //페이징 정보 생성
        Pageable pageable = PageRequest.of(serviceRequest.page(), serviceRequest.size(), Sort.by("name").ascending());

        //장소 검색
        Page<PerformanceLocation> locations =
                performanceLocationRepository.findByKeywordContaining(serviceRequest.keyword(), pageable);

        //각 장소 엔티티가 들고 있는 images를 활용해 DTO로 변환
        return locations.map(location -> PerformanceLocationSearchResponse.from(
                location,
                location.getImages().stream()
                        .map(PerformanceLocationImage::getImageUrl)
                        .toList()
        ));
    }

}
