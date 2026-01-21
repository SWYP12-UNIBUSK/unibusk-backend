package team.unibusk.backend.domain.performanceLocation.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.unibusk.backend.domain.performanceLocation.application.dto.request.PerformanceLocationSearchByNameServiceRequest;
import team.unibusk.backend.domain.performanceLocation.application.dto.response.PerformanceLocationSearchByNameResponse;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocationRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class PerformanceLocationService {

    private final PerformanceLocationRepository performanceLocationRepository;

    //name으로 키워드 검색 (대소문자 구분 없음)
    public Page<PerformanceLocationSearchByNameResponse> searchByNameContaining(
            PerformanceLocationSearchByNameServiceRequest request
    ) {

        return performanceLocationRepository.findByNameContatinin(request.name(), request.pageable())
                .map(PerformanceLocationSearchByNameResponse::from);
    }

}
