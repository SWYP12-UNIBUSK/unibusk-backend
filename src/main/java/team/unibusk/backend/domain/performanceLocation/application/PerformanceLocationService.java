package team.unibusk.backend.domain.performanceLocation.application;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.unibusk.backend.domain.performanceLocation.application.dto.response.PerformanceLocationListResponse;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocationRepository;

@Service
@RequiredArgsConstructor
public class PerformanceLocationService {

    private final PerformanceLocationRepository performanceLocationRepository;

    @Transactional(readOnly = true)
    public PerformanceLocationListResponse findByKeyword(String keyword, Pageable pageable){

        return PerformanceLocationListResponse.from(performanceLocationRepository.searchByKeyword(keyword, pageable));
    }



}
