package team.unibusk.backend.domain.performance.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.unibusk.backend.domain.performance.application.dto.request.PerformanceRegisterServiceRequest;
import team.unibusk.backend.domain.performance.application.dto.response.PerformanceRegisterResponse;
import team.unibusk.backend.domain.performance.domain.Performance;
import team.unibusk.backend.domain.performance.domain.PerformanceImage;
import team.unibusk.backend.domain.performance.domain.PerformanceRepository;
import team.unibusk.backend.domain.performance.domain.Performer;
import team.unibusk.backend.global.file.application.FileUploadService;

import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional
public class PerformanceService {

    private final PerformanceRepository performanceRepository;
    private final FileUploadService fileUploadService;

    private static final String PERFORMANCE_FOLDER = "performances";

    public PerformanceRegisterResponse register(PerformanceRegisterServiceRequest request, Long memberId) {
        // 1. 이미지 업로드 (외부 도메인 서비스 활용)
        List<PerformanceImage> images = uploadImages(request.images());

        try{
            // 2. 공연자 생성 (Local Entity)
            List<Performer> performers = request.performers().stream()
                    .map(p -> Performer.builder()
                            .name(p.name())
                            .email(p.email())
                            .phoneNumber(p.phoneNumber())
                            .instagram(p.instagram())
                            .build())
                    .toList();

            // 3. 애그리거트 루트 조립 (Builder 활용)
            Performance performance = Performance.builder()
                    .memberId(memberId)
                    .performanceLocationId(request.performanceLocationId())
                    .title(request.title())
                    .summary(request.summary())
                    .description(request.description())
                    .performanceDate(request.performanceDate())
                    .startTime(request.startTime())
                    .endTime(request.endTime())
                    .images(images)
                    .performers(performers)
                    .build();

            // 4. 저장 및 ID 반환
            Performance saved = performanceRepository.save(performance);
            return PerformanceRegisterResponse.from(saved);
        }catch(Exception e){
            //실패하면 저장됐던 이미지 삭제
            images.forEach(img -> fileUploadService.delete(img.getImageUrl()));
            throw e;
        }



    }

    private List<PerformanceImage> uploadImages(List<org.springframework.web.multipart.MultipartFile> files) {
        if (files == null || files.isEmpty()) return List.of();

        return IntStream.range(0, files.size())
                .mapToObj(i -> {
                    String url = fileUploadService.upload(files.get(i), PERFORMANCE_FOLDER);
                    return PerformanceImage.builder()
                            .imageUrl(url)
                            .sortOrder(i + 1)
                            .build();
                }).toList();
    }
}
