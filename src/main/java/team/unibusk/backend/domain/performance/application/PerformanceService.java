package team.unibusk.backend.domain.performance.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import team.unibusk.backend.domain.performance.application.dto.request.PerformanceRegisterServiceRequest;
import team.unibusk.backend.domain.performance.application.dto.response.PerformanceRegisterResponse;
import team.unibusk.backend.domain.performance.application.dto.response.PerformanceResponse;
import team.unibusk.backend.domain.performance.application.dto.response.PerformerResponse;
import team.unibusk.backend.domain.performance.domain.Performance;
import team.unibusk.backend.domain.performance.domain.PerformanceImage;
import team.unibusk.backend.domain.performance.domain.PerformanceRepository;
import team.unibusk.backend.domain.performance.domain.Performer;
import team.unibusk.backend.domain.performance.presentation.exception.PerformanceRegistrationFailedException;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocationRepository;
import team.unibusk.backend.global.file.application.FileUploadService;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class PerformanceService {

    private final PerformanceRepository performanceRepository;
    private final FileUploadService fileUploadService;
    private final PerformanceLocationRepository performanceLocationRepository;

    private static final String PERFORMANCE_FOLDER = "performances";

    @Transactional
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

    @Transactional(readOnly = true)
    public List<PerformanceResponse> getUpcomingPerformances() {
        LocalDateTime now = LocalDateTime.now();

        List<Performance> performances =
                performanceRepository.findUpcomingPerformances(now);

        List<Long> locationIds = performances.stream()
                .map(Performance::getPerformanceLocationId)
                .distinct()
                .toList();

        Map<Long, String> locationNameMap =
                performanceLocationRepository.findAllById(locationIds)
                        .stream()
                        .collect(Collectors.toMap(
                                PerformanceLocation::getId,
                                PerformanceLocation::getName
                        ));

        return performances.stream()
                .map(p -> PerformanceResponse.from(
                        p,
                        locationNameMap.get(p.getPerformanceLocationId())
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PerformanceResponse> getUpcomingPerformancesPreview() {
        LocalDateTime now = LocalDateTime.now();

        List<Performance> performances =
                performanceRepository.findTop8ByEndTimeGreaterThanEqualOrderByStartTimeAsc(now);


        return performances.stream()
                .map(this::toResponse)
                .toList();
    }

    private PerformanceResponse toResponse(Performance performance) {
        String locationName = performanceLocationRepository
                .findById(performance.getPerformanceLocationId())
                .map(PerformanceLocation::getName)
                .orElse("공연 장소 정보가 없습니다.");

        return PerformanceResponse.builder()
                .performanceId(performance.getId())
                .title(performance.getTitle())
                .performanceDate(performance.getPerformanceDate())
                .startTime(performance.getStartTime())
                .endTime(performance.getEndTime())
                .locationName(locationName)
                .images(
                        performance.getImages().stream()
                                .map(image -> image.getImageUrl())
                                .toList()
                )
                .build();
    }
}
