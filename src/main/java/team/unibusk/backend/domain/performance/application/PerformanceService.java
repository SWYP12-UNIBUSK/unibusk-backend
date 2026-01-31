package team.unibusk.backend.domain.performance.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;
import team.unibusk.backend.domain.performance.application.dto.request.PerformanceRegisterServiceRequest;
import team.unibusk.backend.domain.performance.application.dto.request.PerformanceUpdateServiceRequest;
import team.unibusk.backend.domain.performance.application.dto.response.*;
import team.unibusk.backend.domain.performance.domain.Performance;
import team.unibusk.backend.domain.performance.domain.PerformanceImage;
import team.unibusk.backend.domain.performance.domain.PerformanceRepository;
import team.unibusk.backend.domain.performance.domain.Performer;
import team.unibusk.backend.domain.performance.presentation.exception.PerformanceNotFoundException;
import team.unibusk.backend.domain.performance.presentation.exception.PerformanceRegistrationFailedException;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocationRepository;
import team.unibusk.backend.global.file.application.FileUploadService;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;
import team.unibusk.backend.global.response.PageResponse;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
                    ? Collections.emptyList()
                    : request.performers().stream()
                    .map(p -> Performer.builder()
                            .name(p.name())
                            .email(p.email())
                            .phoneNumber(p.phoneNumber())
                            .instagram(p.instagram())
                            .build())
                    .collect(Collectors.toList());

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
    public PageResponse<PerformanceResponse> getUpcomingPerformances(Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();

        Page<Performance> performances =
                performanceRepository.findUpcomingPerformances(now, pageable);

        Map<Long, String> locationNameMap =
                performanceLocationRepository.findByIds(
                                performances.getContent().stream()
                                        .map(Performance::getPerformanceLocationId)
                                        .collect(Collectors.toSet())
                        ).stream()
                        .collect(Collectors.toMap(
                                PerformanceLocation::getId,
                                PerformanceLocation::getName
                        ));

        Page<PerformanceResponse> page = performances.map(p ->
                PerformanceResponse.from(
                        p,
                        locationNameMap.getOrDefault(
                                p.getPerformanceLocationId(),
                                "공연 장소 정보가 없습니다."
                        )
                )
        );

        return PageResponse.from(page);
    }

    @Transactional(readOnly = true)
    public List<PerformancePreviewResponse> getUpcomingPerformancesPreview() {
        LocalDateTime now = LocalDateTime.now();

        Pageable pageable = PageRequest.of(0, 8);

        List<Performance> performances =
                performanceRepository.findUpcomingPreview(now, pageable);

        Set<Long> locationIds = performances.stream()
                .map(Performance::getPerformanceLocationId)
                .collect(Collectors.toSet());

        Map<Long, String> locationNameMap =
                performanceLocationRepository.findByIds(locationIds).stream()
                        .collect(Collectors.toMap(
                                PerformanceLocation::getId,
                                PerformanceLocation::getName
                        ));

        return performances.stream()
                .map(p -> PerformancePreviewResponse.from(p, locationNameMap))
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<PerformanceResponse> getPastPerformances(Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();

        Page<Performance> performances =
                performanceRepository.findPastPerformances(now, pageable);

        Map<Long, String> locationNameMap =
                performanceLocationRepository.findByIds(
                                performances.getContent().stream()
                                        .map(Performance::getPerformanceLocationId)
                                        .collect(Collectors.toSet())
                        ).stream()
                        .collect(Collectors.toMap(
                                PerformanceLocation::getId,
                                PerformanceLocation::getName
                        ));

        Page<PerformanceResponse> page = performances.map(p ->
                PerformanceResponse.from(
                        p,
                        locationNameMap.getOrDefault(
                                p.getPerformanceLocationId(),
                                "공연 장소 정보가 없습니다."
                        )
                )
        );

        return PageResponse.from(page);
    }

    @Transactional(readOnly = true)
    public PerformanceDetailResponse getPerformanceDetail(Long performanceId) {
        Performance performance = performanceRepository.findDetailById(performanceId);

        PerformanceLocation location =
                performanceLocationRepository.findById(performance.getPerformanceLocationId());

        return PerformanceDetailResponse.from(performance, location);
    }

    @Transactional
    public PerformanceDetailResponse updatePerformance(PerformanceUpdateServiceRequest request) {
        Performance performance = performanceRepository.findDetailById(request.performanceId());

        performance.validateOwner(request.memberId());

        performance.updateBasicInfo(
                request.title(),
                request.performanceDate(),
                request.startTime(),
                request.endTime(),
                request.summary(),
                request.description(),
                request.performanceLocationId()
        );

        performance.clearPerformers();
        if (request.performers() != null && !request.performers().isEmpty()) {
            request.performers().forEach(p ->
                    performance.addPerformer(
                            Performer.builder()
                                    .name(p.name())
                                    .email(p.email())
                                    .phoneNumber(p.phoneNumber())
                                    .instagram(p.instagram())
                                    .build()
                    )
            );
        }

        List<PerformanceImage> newImages = uploadImages(request.images());

        if (!newImages.isEmpty()) {
            List<String> deleteTargetUrls = performance.getImages().stream()
                    .map(PerformanceImage::getImageUrl)
                    .toList();
            List<String> newImageUrls = newImages.stream()
                    .map(PerformanceImage::getImageUrl)
                    .toList();

            performance.clearImages();
            newImages.forEach(performance::addImage);

            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            deleteTargetUrls.forEach(fileUploadService::delete);
                        }

                        @Override
                        public void afterCompletion(int status) {
                            if (status != TransactionSynchronization.STATUS_COMMITTED) {
                                newImageUrls.forEach(fileUploadService::delete);
                            }
                        }
                    }
            );
        }

        PerformanceLocation location =
                performanceLocationRepository.findById(performance.getPerformanceLocationId());

        return PerformanceDetailResponse.from(performance, location);
    }

    @Transactional
    public void deletePerformance(Long performanceId, Long memberId) {
        Performance performance = performanceRepository.findById(performanceId);

        performance.validateOwner(memberId);

        performance.getImages().forEach(img ->
                fileUploadService.delete(img.getImageUrl())
        );

        performanceRepository.delete(performance);
    }
}
