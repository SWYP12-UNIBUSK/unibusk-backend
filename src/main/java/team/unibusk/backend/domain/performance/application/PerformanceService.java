package team.unibusk.backend.domain.performance.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import team.unibusk.backend.domain.performance.application.dto.request.PerformanceRegisterServiceRequest;
import team.unibusk.backend.domain.performance.application.dto.response.PerformanceRegisterResponse;
import team.unibusk.backend.domain.performance.domain.Performance;
import team.unibusk.backend.domain.performance.domain.PerformanceImage;
import team.unibusk.backend.domain.performance.domain.PerformanceRepository;
import team.unibusk.backend.domain.performance.domain.Performer;
import team.unibusk.backend.domain.performance.presentation.exception.PerformanceRegistrationFailedException;
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

    public PerformanceRegisterResponse register(PerformanceRegisterServiceRequest request) {
        // 이미지 업로드
        List<PerformanceImage> images = uploadImages(request.images());

        try{
            // 공연자 생성
            List<Performer> performers = (request.performers() == null || request.performers().isEmpty())
                    ? List.of()
                    : request.performers().stream()
                    .map(p -> Performer.builder()
                            .name(p.name())
                            .email(p.email())
                            .phoneNumber(p.phoneNumber())
                            .instagram(p.instagram())
                            .build())
                    .toList();

            // 애그리거트 루트 조립
            Performance performance = Performance.builder()
                    .memberId(request.memberId())
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

            // 저장
            Performance saved = performanceRepository.save(performance);
            return PerformanceRegisterResponse.builder()
                    .performanceId(saved.getId())
                    .build();

        }catch(Exception e){
            //실패하면 저장됐던 이미지 삭제
            images.forEach(img -> fileUploadService.delete(img.getImageUrl()));
            throw new PerformanceRegistrationFailedException();
        }
    }

    private List<PerformanceImage> uploadImages(List<org.springframework.web.multipart.MultipartFile> files) {
        //파일이 비어있는 경우
        if (files == null || files.isEmpty() || files.stream().allMatch(MultipartFile::isEmpty)) {
            return List.of();
        }

        List<PerformanceImage> uploaded = new java.util.ArrayList<>();
        try {
            IntStream.range(0, files.size())
                    .forEach(i -> {
                        String url = fileUploadService.upload(files.get(i), PERFORMANCE_FOLDER);
                        uploaded.add(PerformanceImage.builder()
                                .imageUrl(url)
                                .sortOrder(i + 1)
                                .build());
                    });
            return uploaded;
        } catch (Exception e) {
            uploaded.forEach(img -> fileUploadService.delete(img.getImageUrl()));
            throw e;
        }
    }
}
