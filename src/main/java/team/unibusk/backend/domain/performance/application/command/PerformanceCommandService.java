package team.unibusk.backend.domain.performance.application.command;

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
import team.unibusk.backend.domain.performance.domain.*;
import team.unibusk.backend.domain.performance.presentation.exception.PerformanceRegistrationFailedException;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocationRepository;
import team.unibusk.backend.global.file.application.FileUploadService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class PerformanceCommandService {

    private final MemberRepository memberRepository;
    private final FileUploadService fileUploadService;
    private final PerformerRepository performerRepository;
    private final PerformanceRepository performanceRepository;
    private final PerformanceImageRepository performanceImageRepository;
    private final PerformanceLocationRepository performanceLocationRepository;

    private static final String PERFORMANCE_FOLDER = "performances";

    @Transactional
    public PerformanceRegisterResponse register(PerformanceRegisterServiceRequest request) {
        String uploadedImageUrl = uploadImage(request.image());

        if(uploadedImageUrl != null) {
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCompletion(int status) {
                            if (status != TransactionSynchronization.STATUS_COMMITTED) {
                                fileUploadService.delete(uploadedImageUrl);
                            }
                        }
                    }
            );
        }

        try {
            Performance performance = Performance.builder()
                    .memberId(request.memberId())
                    .performanceLocationId(request.performanceLocationId())
                    .title(request.title())
                    .summary(request.summary())
                    .description(request.description())
                    .performanceDate(request.performanceDate())
                    .startTime(request.startTime())
                    .endTime(request.endTime())
                    .build();

            Performance savedPerformance = performanceRepository.save(performance);
            Long performanceId = savedPerformance.getId();

            if(request.performers() != null && !request.performers().isEmpty()) {
                List<Performer> performers = request.performers().stream()
                        .map(p -> Performer.builder()
                                .performanceId(performanceId)
                                .name(p.name())
                                .email(p.email())
                                .phoneNumber(p.phoneNumber())
                                .instagram(p.instagram())
                                .build())
                        .collect(Collectors.toList());
                performerRepository.saveAll(performers);
            }

            if(uploadedImageUrl != null) {
                PerformanceImage image = PerformanceImage.builder()
                        .performanceId(performanceId)
                        .imageUrl(uploadedImageUrl)
                        .build();
                performanceImageRepository.save(image);
            }

            return PerformanceRegisterResponse.builder()
                    .performanceId(performanceId)
                    .build();
        } catch (Exception e) {
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

        List<Performer> finalPerformers = new ArrayList<>();
        performerRepository.deleteByPerformanceId(performance.getId());

        if (request.performers() != null && !request.performers().isEmpty()) {
            finalPerformers = request.performers().stream()
                    .map(p -> Performer.builder()
                            .performanceId(performance.getId())
                            .name(p.name())
                            .email(p.email())
                            .phoneNumber(p.phoneNumber())
                            .instagram(p.instagram())
                            .build())
                    .collect(Collectors.toList());
            performerRepository.saveAll(finalPerformers);
        }

        String finalImageUrl = null;
        String newImageUrl = uploadImage(request.image());

        if (newImageUrl != null) {
            PerformanceImage oldImage = performanceImageRepository.findByPerformanceId(performance.getId());
            String deleteTargetUrl = oldImage != null ? oldImage.getImageUrl() : null;
            if(oldImage != null) {
                performanceImageRepository.deleteByPerformanceId(performance.getId());
            }

            PerformanceImage newImage = PerformanceImage.builder()
                    .performanceId(performance.getId())
                    .imageUrl(newImageUrl)
                    .build();
            performanceImageRepository.save(newImage);

            finalImageUrl = newImageUrl;

            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            if(deleteTargetUrl != null) {
                                fileUploadService.delete(deleteTargetUrl);
                            }
                        }

                        @Override
                        public void afterCompletion(int status) {
                            if (status != TransactionSynchronization.STATUS_COMMITTED) {
                                fileUploadService.delete(newImageUrl);
                            }
                        }
                    }
            );
        } else {
            PerformanceImage existingImage = performanceImageRepository.findByPerformanceId(performance.getId());
            if (existingImage != null) {
                finalImageUrl = existingImage.getImageUrl();
            }
        }

        PerformanceLocation location =
                performanceLocationRepository.findById(performance.getPerformanceLocationId());

        return PerformanceDetailResponse.from(performance, location, finalImageUrl, finalPerformers);
    }

    public void deletePerformance(Long performanceId, Long memberId) {
        Performance performance = performanceRepository.findById(performanceId);
        Member member = memberRepository.findByMemberId(memberId);

        performance.validateOwner(member.getId());

        PerformanceImage image = performanceImageRepository.findByPerformanceId(performanceId);

        if(image != null) {
            String deleteTargetUrl = image.getImageUrl();

            performanceImageRepository.deleteByPerformanceId(performanceId);

            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            fileUploadService.delete(deleteTargetUrl);
                        }
                    }
            );
        }

        performerRepository.deleteByPerformanceId(performanceId);

        performanceRepository.delete(performance);
    }

    private String uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            return fileUploadService.upload(file, PERFORMANCE_FOLDER);
        } catch (Exception e) {
            throw e;
        }
    }

}
