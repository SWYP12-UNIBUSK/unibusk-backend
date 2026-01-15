package team.unibusk.backend.domain.performance.application;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.unibusk.backend.domain.PerformanceLocation.domain.PerformanceLocation;
import team.unibusk.backend.domain.PerformanceLocation.domain.PerformanceLocationRepository;
import team.unibusk.backend.domain.PerformanceLocation.presentation.exception.PerformanceLocationException;
import team.unibusk.backend.domain.PerformanceLocation.presentation.exception.PerformanceLocationExceptionCode;
import team.unibusk.backend.domain.performance.application.dto.request.PerformanceRegisterServiceRequest;
import team.unibusk.backend.domain.performance.application.dto.response.PerformanceRegisterResponse;
import team.unibusk.backend.domain.performance.domain.Performance;
import team.unibusk.backend.domain.performance.domain.PerformanceRepository;
import team.unibusk.backend.domain.performance.infrastructure.performanceImage.PerformanceImageStore;
import team.unibusk.backend.domain.performance.presentation.exception.PerformanceException;
import team.unibusk.backend.domain.performance.presentation.exception.PerformanceExceptionCode;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PerformanceService {

    private final PerformanceRepository performanceRepository;
    private final PerformanceLocationRepository performanceLocationRepository;
    private final PerformanceImageStore performanceImageStore;

    @Transactional
    public PerformanceRegisterResponse registerPerformance(
            Long memberId,
            PerformanceRegisterServiceRequest request
    ) {

        // 1. 장소 존재 여부 확인
        PerformanceLocation location = performanceLocationRepository.findById(request.performanceLocationId())
                .orElseThrow(() -> new PerformanceLocationException(PerformanceLocationExceptionCode.PERFORMANCELOCATION_NOT_FOUND));

        // 2. Performance 엔티티 생성 (Aggregate Root)
        Performance performance = Performance.builder()
                .memberId(memberId)
                .title(request.title())
                .summary(request.summary())
                .description(request.description())
                .performanceDate(request.performanceDate())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .performanceLocationId(location.getId())
                .build();

        // 3. 공연자(Performer) 추가
        request.performers().forEach(p ->
                performance.addPerformer(p.name(), p.email(), p.phone(), p.instagramId())
        );

        // 4. 이미지 저장 로직 (PerformanceImageStore 사용)
        try {
            List<String> storedFileNames = performanceImageStore.storeFiles(request.images());
            for (int i = 0; i < storedFileNames.size(); i++) {
                // 저장된 파일명(UUID)과 순서(sort_order)를 엔티티에 추가
                performance.addImage(storedFileNames.get(i), (long) i + 1);
            }
        } catch (IOException e) {
            throw new PerformanceException(PerformanceExceptionCode.IMAGE_SAVE_ERROR);
        }

        // 5. 저장
        Performance savedPerformance = performanceRepository.save(performance);

        return PerformanceRegisterResponse.from(savedPerformance);
    }
}
