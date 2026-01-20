package team.unibusk.backend.domain.performance.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;
import team.unibusk.backend.domain.performance.application.dto.request.PerformanceRegisterServiceRequest;
import team.unibusk.backend.domain.performance.application.dto.response.PerformanceRegisterResponse;
import team.unibusk.backend.domain.performance.domain.*;
import team.unibusk.backend.domain.performance.domain.repository.PerformanceImageRepository;
import team.unibusk.backend.domain.performance.domain.repository.PerformanceRepository;
import team.unibusk.backend.domain.performance.domain.repository.PerformerRepository;
import team.unibusk.backend.domain.performance.presentation.exception.PerformanceRegisterFailedException;

import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional
public class PerformanceService {

    private final PerformanceRepository performanceRepository;
    private final PerformerRepository performerRepository;
    private final PerformanceImageRepository performanceImageRepository;
    private final TransactionTemplate transactionTemplate;

    //전체 등록 프로세스 관리
    //물리 파일 업로드(S3)를 먼저 수행
    public PerformanceRegisterResponse register(
            PerformanceRegisterServiceRequest request,
            Long memberId
    ) {
        // S3 파일 업로드
        List<String> imageUrls = performanceImageRepository.upload(request.images());

        try {
            // DB 저장 로직 실행 (트랜잭션 영역)
            return transactionTemplate.execute(status -> {
                // 롤백 시 S3 파일 삭제 동기화
                registerRollbackSynchronization(imageUrls);

                return savePerformanceProcess(request, imageUrls, memberId);
            });
        } catch (Exception e) {
            // 트랜잭션 외부 예외(DB 연결 실패 등) 발생 시 S3 파일 보상 삭제
            performanceImageRepository.deleteFiles(imageUrls);
            throw new PerformanceRegisterFailedException();
        }
    }


     //순수 DB 저장 로직 (MySQL)
     //Performance -> Performer -> PerformanceImage 순서로 저장
    private PerformanceRegisterResponse savePerformanceProcess(
            PerformanceRegisterServiceRequest request,
            List<String> imageUrls,
            Long memberId
    ) {
        // Step 1: Performance(부모) 저장
        Performance performance = performanceRepository.save(
                Performance.create(
                        memberId,
                        request.performanceLocationId(),
                        request.title(),
                        request.summary(),
                        request.description(),
                        request.performanceDate(),
                        request.startTime(),
                        request.endTime()
                )
        );

        // Step 2: Performer(자식) 저장
        performerRepository.save(
                Performer.create(
                        request.name(),
                        request.email(),
                        request.phone(),
                        request.instagram(),
                        performance // 영속화된 부모 주입
                )
        );

        // Step 3: PerformanceImage(자식들) 저장 (S3 URL 리스트 활용)
        if (imageUrls != null && !imageUrls.isEmpty()) {
            List<PerformanceImage> images = IntStream.range(0, imageUrls.size())
                    .mapToObj(i -> PerformanceImage.create(
                            imageUrls.get(i),
                            (long) i + 1,
                            performance // 영속화된 부모 주입
                    ))
                    .toList();
            performanceImageRepository.saveAll(images);
        }

        return PerformanceRegisterResponse.from(performance);
    }


    //트랜잭션 롤백 시 S3에 업로드된 물리 파일 삭제를 위한 동기화 작업
    private void registerRollbackSynchronization(List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) return;

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCompletion(int status) {
                        if (status == STATUS_ROLLED_BACK) {
                            performanceImageRepository.deleteFiles(imageUrls);
                        }
                    }
                }
        );
    }
}