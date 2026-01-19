package team.unibusk.backend.domain.performance.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;
import team.unibusk.backend.domain.performanceImage.domain.PerformanceImageUploadRepository;
import team.unibusk.backend.domain.performance.application.dto.request.PerformanceRegisterServiceRequest;
import team.unibusk.backend.domain.performance.application.dto.response.PerformanceRegisterResponse;
import team.unibusk.backend.domain.performance.domain.Performance;
import team.unibusk.backend.domain.performance.domain.PerformanceRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PerformanceService {

    private final PerformanceRepository performanceRepository;
    private final PerformanceImageUploadRepository imageUploadRepository;
    private final TransactionTemplate transactionTemplate;

    //공연 등록
    public PerformanceRegisterResponse registerPerformance(
            PerformanceRegisterServiceRequest request,
            Long memberId
    ){
        // 1. 파일 업로드 (트랜잭션 시작 전 수행: DB 커넥션 점유 효율화)
        List<String> imageUrls = imageUploadRepository.uploadFiles(request.images());

        try {
            // 2. DB 트랜잭션 영역 시작
            return transactionTemplate.execute(status -> {
                // 트랜잭션 동기화 등록 (롤백 시 파일 삭제)
                registerRollbackSynchronization(imageUrls);

                // 엔티티 생성 및 저장
                Performance performance = Performance.create(request, imageUrls, memberId);
                Performance savedPerformance = performanceRepository.save(performance);

                return PerformanceRegisterResponse.from(savedPerformance);
            });
        } catch (Exception e) {
            // 트랜잭션 외부에서 발생할 수 있는 예외에 대한 보상 로직 (필요시)
            // TransactionSynchronization이 롤백 시 삭제를 처리하지만,
            // 혹시 모를 상황을 대비해 한 번 더 체크할 수 있습니다.
            throw e;
        }
    }

    //롤백 시 파일 삭제 콜백 등록
    private void registerRollbackSynchronization(List<String> imageUrls) {
        if (imageUrls.isEmpty()) return;

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCompletion(int status) {
                        // DB 작업 실패(롤백) 시 저장된 물리 파일 삭제
                        if (status == TransactionSynchronization.STATUS_ROLLED_BACK) {
                            imageUploadRepository.deleteFiles(imageUrls);
                        }
                    }
                }
        );
    }


}
