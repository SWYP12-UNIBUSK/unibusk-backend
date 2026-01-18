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
import team.unibusk.backend.domain.performanceLocationImage.domain.PerformanceLocationImage;
import team.unibusk.backend.domain.performanceLocationImage.domain.PerformanceLocationImageRepository;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocationRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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

        List<Long> locationIds = List.of(location.getId());

        //해당 장소에 속한 모든 이미지 엔티티 조회
        List<PerformanceLocationImage> allImages =
                performanceLocationImageRepository.findAllByPerformanceLocationIdIn(locationIds);

        //이미지 엔티티 리스트를 URL 문자열 리스트로 변환
        List<String> imageUrls = allImages.stream()
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
        List<Long> locationIds = locations.getContent().stream().map(PerformanceLocation::getId).toList();
        //조회된 장소들의 이미지 조회
        List<PerformanceLocationImage> allImages =
                performanceLocationImageRepository.findAllByPerformanceLocationIdIn(locationIds);

        //장소 ID 별로 이미지 그룹화
        Map<Long, List<String>> imageMap = allImages.stream()
                .collect(Collectors.groupingBy(
                        img -> img.getPerformanceLocation().getId(),
                        Collectors.mapping(PerformanceLocationImage::getImageUrl, Collectors.toList())
                ));

        //Map에서 꺼내서 DTO로 저장
        return locations.map(location -> PerformanceLocationSearchResponse.from(
                location,
                imageMap.getOrDefault(location.getId(), Collections.emptyList())
        ));
    }

}
