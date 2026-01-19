package team.unibusk.backend.domain.perfromance.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.unibusk.backend.domain.performanceImage.domain.PerformanceImageUploadRepository;
import team.unibusk.backend.domain.perfromance.application.dto.requset.PerformanceRegisterServiceRequest;
import team.unibusk.backend.domain.perfromance.application.dto.response.PerformanceRegisterResponse;
import team.unibusk.backend.domain.perfromance.domain.Performance;
import team.unibusk.backend.domain.perfromance.domain.PerformanceRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PerformanceService {

    private final PerformanceRepository performanceRepository;
    private final PerformanceImageUploadRepository imageUploadRepository;

    @Transactional
    public PerformanceRegisterResponse registerPerformance(
            PerformanceRegisterServiceRequest request,
            Long memberId
    ){

        //동일한 공연 이름 존재하는지 확인

        //공연 예정 시간에 이미 공연이 잡혀 있는지

        //이미지 저장
        List<String> imageUrls = imageUploadRepository.uploadFiles(request.images());

        //Performance, Performer, PerformanceImage 객체 생성
        Performance performance = Performance.create(request, imageUrls, memberId);

        Performance savedPerformance = performanceRepository.save(performance);

        return PerformanceRegisterResponse.from(savedPerformance);
    }



}
