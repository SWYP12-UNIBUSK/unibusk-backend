package team.unibusk.backend.domain.performance.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;
import team.unibusk.backend.domain.member.domain.Member;
import team.unibusk.backend.domain.member.domain.MemberRepository;
import team.unibusk.backend.domain.performance.application.dto.request.PerformanceRegisterServiceRequest;
import team.unibusk.backend.domain.performance.application.dto.request.PerformanceUpdateServiceRequest;
import team.unibusk.backend.domain.performance.application.dto.response.PerformanceDetailResponse;
import team.unibusk.backend.domain.performance.application.dto.response.PerformanceRegisterResponse;
import team.unibusk.backend.domain.performance.domain.Performance;
import team.unibusk.backend.domain.performance.domain.PerformanceImage;
import team.unibusk.backend.domain.performance.domain.PerformanceRepository;
import team.unibusk.backend.domain.performance.domain.Performer;
import team.unibusk.backend.domain.performance.presentation.exception.PerformanceRegistrationFailedException;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocationRepository;
import team.unibusk.backend.global.file.application.FileUploadService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional
@RequiredArgsConstructor
public class PerformanceCommandService {

    private final MemberRepository memberRepository;
    private final FileUploadService fileUploadService;
    private final PerformanceRepository performanceRepository;
    private final PerformanceLocationRepository performanceLocationRepository;

    private static final String PERFORMANCE_FOLDER = "performances";

    @Transactional
    public PerformanceRegisterResponse register(PerformanceRegisterServiceRequest request) {
        List<PerformanceImage> images = uploadImages(request.images());
        List<String> uploadedImageUrls = images.stream()
                .map(PerformanceImage::getImageUrl)
                .toList();

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCompletion(int status) {
                        if (status != TransactionSynchronization.STATUS_COMMITTED) {
                            uploadedImageUrls.forEach(fileUploadService::delete);
                        }
                    }
                }
        );

        try{
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

            Performance saved = performanceRepository.save(performance);
            return PerformanceRegisterResponse.builder()
                    .performanceId(saved.getId())
                    .build();

        }catch(Exception e){
            images.forEach(img -> fileUploadService.delete(img.getImageUrl()));
            throw new PerformanceRegistrationFailedException();
        }
    }

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

    public void deletePerformance(Long performanceId, Long memberId) {
        Performance performance = performanceRepository.findById(performanceId);

        Member member = memberRepository.findByMemberId(memberId);

        performance.validateOwner(member.getId());

        List<String> deleteTargetUrls = performance.getImages().stream()
                .map(PerformanceImage::getImageUrl)
                .toList();

        performanceRepository.delete(performance);

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        deleteTargetUrls.forEach(fileUploadService::delete);
                    }
                }
        );
    }

    private List<PerformanceImage> uploadImages(List<MultipartFile> files) {
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
