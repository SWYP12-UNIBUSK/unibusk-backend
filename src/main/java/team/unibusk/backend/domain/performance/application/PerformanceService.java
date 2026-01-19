package team.unibusk.backend.domain.performance.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import team.unibusk.backend.domain.performanceImage.domain.PerformanceImageUploadRepository;
import team.unibusk.backend.domain.performance.application.dto.requset.PerformanceRegisterServiceRequest;
import team.unibusk.backend.domain.performance.application.dto.response.PerformanceRegisterResponse;
import team.unibusk.backend.domain.performance.domain.Performance;
import team.unibusk.backend.domain.performance.domain.PerformanceRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PerformanceService {

    private final PerformanceRepository performanceRepository;
    private final PerformanceImageUploadRepository imageUploadRepository;

    //메인 서비스 로직
    public PerformanceRegisterResponse registerPerformance(
            PerformanceRegisterServiceRequest request,
            Long memberId
    ) {
        // 1. 파일 업로드 (트랜잭션 밖: DB 커넥션 점유 방지)
        List<String> imageUrls = imageUploadRepository.uploadFiles(request.images());

        // 2. 동기화 매니저 등록 및 DB 저장 수행
        return savePerformanceWithSync(request, imageUrls, memberId);
    }
    //실제 저장 로직
    @Transactional
    public PerformanceRegisterResponse savePerformanceWithSync(
            PerformanceRegisterServiceRequest request,
            List<String> imageUrls,
            Long memberId
    ) {
        // 3. 트랜잭션 동기화 등록 (롤백 시 파일 삭제)
        registerRollbackSynchronization(imageUrls);

        // 4. 엔티티 생성 및 저장
        Performance performance = Performance.create(request, imageUrls, memberId);
        Performance savedPerformance = performanceRepository.save(performance);

        return PerformanceRegisterResponse.from(savedPerformance);
    }


     //롤백 발생 시 실행될 콜백 등록
    private void registerRollbackSynchronization(List<String> imageUrls) {
        if (imageUrls.isEmpty()) return;

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCompletion(int status) {
                        // 트랜잭션이 롤백되었을 때만 파일 삭제 실행
                        if (status == TransactionSynchronization.STATUS_ROLLED_BACK) {
                            imageUploadRepository.deleteFiles(imageUrls);
                        }
                    }
                }
        );
    }


}
