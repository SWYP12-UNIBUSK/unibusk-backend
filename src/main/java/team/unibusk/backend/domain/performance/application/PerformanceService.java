package team.unibusk.backend.domain.performance.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;
import team.unibusk.backend.domain.member.domain.Member;
import team.unibusk.backend.domain.member.domain.MemberRepository;
import team.unibusk.backend.domain.performance.application.dto.request.PerformanceRegisterServiceRequest;
import team.unibusk.backend.domain.performance.application.dto.request.PerformanceUpdateServiceRequest;
import team.unibusk.backend.domain.performance.application.dto.response.*;
import team.unibusk.backend.domain.performance.domain.*;
import team.unibusk.backend.domain.performance.presentation.exception.InvalidPerformanceStartTimeException;
import team.unibusk.backend.domain.performance.presentation.exception.InvalidPerformanceTimeRangeException;
import team.unibusk.backend.domain.performance.presentation.exception.PerformanceRegistrationFailedException;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocationRepository;
import team.unibusk.backend.global.file.application.FileUploadService;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;
import team.unibusk.backend.global.response.CursorResponse;
import team.unibusk.backend.global.response.PageResponse;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class PerformanceService {

    private final MemberRepository memberRepository;
    private final FileUploadService fileUploadService;
    private final PerformanceRepository performanceRepository;
    private final PerformanceLocationRepository performanceLocationRepository;

    private static final String PERFORMANCE_FOLDER = "performances";

    @Transactional
    public PerformanceRegisterResponse register(PerformanceRegisterServiceRequest request) {
        //공연 시간 검증
        validatePerformanceTime(request.startTime(), request.endTime());

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

    private void validatePerformanceTime(LocalDateTime start, LocalDateTime end) {
        LocalDateTime now = LocalDateTime.now();

        // 시작 시간이 현재보다 과거인지 검증
        if (start.isBefore(now)) {
            throw new InvalidPerformanceStartTimeException();
        }

        // 종료 시간이 시작 시간보다 빠른지 검증
        if (end.isBefore(start)) {
            throw new InvalidPerformanceTimeRangeException();
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

        Member member = memberRepository.findByMemberId(request.memberId());

        performance.validateOwner(member.getId());

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

        Member member = memberRepository.findByMemberId(memberId);

        performance.validateOwner(member.getId());

        performance.getImages().forEach(img ->
                fileUploadService.delete(img.getImageUrl())
        );

        performanceRepository.delete(performance);
    }

    @Transactional(readOnly = true)
    public PageResponse<PerformanceResponse> searchPerformances(
            PerformanceStatus status,
            String keyword,
            Pageable pageable
    ) {

        Page<PerformanceResponse> page = performanceRepository.searchByCondition(status, keyword, pageable);

        return PageResponse.from(page);
    }

    @Transactional(readOnly = true)
    public CursorResponse<PerformanceCursorResponse> getUpcomingByLocationWithCursor(
            Long performanceLocationId,
            LocalDateTime cursorTime,
            Long cursorId,
            int size
    ) {

        List<Performance> performances =
                performanceRepository.findUpcomingByPerformanceLocationWithCursor(
                        performanceLocationId,
                        cursorTime,
                        cursorId,
                        size
                );

        boolean hasNext = performances.size() > size;

        if (hasNext) {
            performances.remove(size);
        }

        List<PerformanceCursorResponse> contents =
                performances.stream()
                        .map(PerformanceCursorResponse::from)
                        .toList();

        LocalDateTime nextCursorTime = null;
        Long nextCursorId = null;

        if (hasNext && !performances.isEmpty()) {
            Performance last = performances.get(performances.size() - 1);
            nextCursorTime = last.getStartTime();
            nextCursorId = last.getId();
        }

        return CursorResponse.of(
                contents,
                nextCursorTime,
                nextCursorId,
                hasNext
        );
    }

    @Transactional(readOnly = true)
    public CursorResponse<PerformanceCursorResponse> getPastByLocationWithCursor(
            Long performanceLocationId,
            LocalDateTime cursorTime,
            Long cursorId,
            int size
    ) {

        List<Performance> performances =
                performanceRepository.findPastByPerformanceLocationWithCursor(
                        performanceLocationId,
                        cursorTime,
                        cursorId,
                        size
                );

        boolean hasNext = performances.size() > size;

        if (hasNext) {
            performances.remove(size);
        }

        List<PerformanceCursorResponse> contents =
                performances.stream()
                        .map(PerformanceCursorResponse::from)
                        .toList();

        LocalDateTime nextCursorTime = null;
        Long nextCursorId = null;

        if (hasNext && !performances.isEmpty()) {
            Performance last = performances.get(performances.size() - 1);
            nextCursorTime = last.getStartTime();
            nextCursorId = last.getId();
        }

        return CursorResponse.of(
                contents,
                nextCursorTime,
                nextCursorId,
                hasNext
        );
    }

    @Transactional(readOnly = true)
    public CursorResponse<MyPerformanceSummaryResponse> getMyPerformances(
            Long memberId,
            Long cursorId,
            int size
    ) {
        List<Performance> performances =
                performanceRepository.findMyPerformancesWithCursor(
                        memberId,
                        cursorId,
                        size
                );

        boolean hasNext = performances.size() > size;

        if (hasNext) {
            performances = performances.subList(0, size);
        }

        Set<Long> locationIds = performances.stream()
                .map(Performance::getPerformanceLocationId)
                .collect(Collectors.toSet());

        Map<Long, PerformanceLocation> locationMap =
                performanceLocationRepository.findByIds(locationIds)
                        .stream()
                        .collect(Collectors.toMap(
                                PerformanceLocation::getId,
                                Function.identity()
                        ));

        List<MyPerformanceSummaryResponse> content =
                performances.stream()
                        .map(p ->
                                MyPerformanceSummaryResponse.from(
                                        p,
                                        locationMap.get(p.getPerformanceLocationId())
                                )
                        )
                        .toList();

        Long nextCursorId = hasNext
                ? performances.get(performances.size() - 1).getId()
                : null;

        return CursorResponse.of(
                content,
                null,
                nextCursorId,
                hasNext
        );
    }

}

