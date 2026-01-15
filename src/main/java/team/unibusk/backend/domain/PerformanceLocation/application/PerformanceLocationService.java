package team.unibusk.backend.domain.PerformanceLocation.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.unibusk.backend.domain.PerformanceLocation.application.dto.request.PerformanceLocationListServiceRequest;
import team.unibusk.backend.domain.PerformanceLocation.application.dto.request.PerformanceLocationSearchServiceRequest;
import team.unibusk.backend.domain.PerformanceLocation.application.dto.response.PerformanceLocationListResponse;
import team.unibusk.backend.domain.PerformanceLocation.application.dto.response.PerformanceLocationSearchResponse;
import team.unibusk.backend.domain.PerformanceLocation.domain.PerformanceLocation;
import team.unibusk.backend.domain.PerformanceLocation.domain.PerformanceLocationRepository;
import team.unibusk.backend.domain.PerformanceLocation.presentation.request.PerformanceLocationSearchRequest;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PerformanceLocationService {

    private final PerformanceLocationRepository performanceLocationRepository;

    //경기장을 keyword로 검색. 10개씩 넘김
    public Page<PerformanceLocationSearchResponse> searchLocationsByKeyword(PerformanceLocationSearchServiceRequest request){
        //페이지 번호와 한 페이지당 개수 설정하여 Pageable 객체 생상
        Pageable pageable = PageRequest.of(request.page(), 10, Sort.by("name").ascending());

        String keyword = request.keyword().trim();

        //keyword가 비어있는 경우. 빈 페이지 반환.
        if(keyword == null || keyword.isEmpty()){
            return Page.empty();
        }

        return performanceLocationRepository.findByNameContaining(keyword, pageable)
                .map(PerformanceLocationSearchResponse::from);
    }

    //경기장 리스트 검색. 10개씩 넘김
    public Page<PerformanceLocationListResponse> getAllLocations(PerformanceLocationListServiceRequest request){
        //페이지 번호와 한 페이지당 개수 설정하여 Pageable 객체 생상
        Pageable pageable = PageRequest.of(request.page(), 10, Sort.by("name").ascending());

        return performanceLocationRepository.findAll(pageable)
                .map(PerformanceLocationListResponse::from);
    }

}
