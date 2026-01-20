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
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocationImageRepository;
import team.unibusk.backend.domain.performanceLocation.presentation.exception.PerformanceLocationNotFoundException;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocationImage;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocationRepository;


import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PerformanceLocationService {

    private final PerformanceLocationRepository performanceLocationRepository;
    private final PerformanceLocationImageRepository performanceLocationImageRepository;

    //name으로 공연 장소 검색
    public PerformanceLocationNameResponse searchByNamePerformanceLocationService(
            String name
    ){
        //이름으로 장소 검색
        PerformanceLocation location = performanceLocationRepository.findByName(name)
                        .orElseThrow(PerformanceLocationNotFoundException::new);

        //이미지 가져오기
        List<PerformanceLocationImage> images = performanceLocationImageRepository.findAllByPerformanceLocationId(location.getId());

        List<String> imageUrls = images.stream()
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

        //검색 결과 없으면 즉시 반환
        if (locations.isEmpty()) {
            return Page.empty(pageable);
        }

        //조회된 모든 장소 ID 추출해서 이미지 일괄 조회
        List<Long> locationIds = locations.getContent().stream()
                .map(PerformanceLocation::getId)
                .toList();
        List<PerformanceLocationImage> allImages =
                performanceLocationImageRepository.findAllByPerformanceLocationIdIn(locationIds);


        //장소 ID별로 이미지 그룹화
        Map<Long, List<String>> imageMap = allImages.stream()
                .collect(Collectors.groupingBy(
                        img -> img.getPerformanceLocation().getId(),
                        Collectors.mapping(PerformanceLocationImage::getImageUrl, Collectors.toList())
                ));
        return locations.map(location -> PerformanceLocationSearchResponse.from(
                location,
                imageMap.getOrDefault(location.getId(), Collections.emptyList())
        ));
    }

}
