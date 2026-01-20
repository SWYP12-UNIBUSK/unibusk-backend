package team.unibusk.backend.domain.performance.infrastructure.performanceImage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import team.unibusk.backend.domain.performance.domain.PerformanceImage;
import team.unibusk.backend.domain.performance.domain.repository.PerformanceImageRepository;
import team.unibusk.backend.domain.performance.presentation.exception.PerformanceRegisterFailedException;
import team.unibusk.backend.global.file.application.FileUploadService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PerformainceImageRepositoryImpl implements PerformanceImageRepository {

    private final PerformanceImageJpaRepository performanceImageJpaRepository;
    private final FileUploadService fileUploadService;

    private static final String FOLDER = "performance";

    @Override
    public List<String> upload(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return List.of();
        }

        List<String> uploadedUrls = new ArrayList<>();

        try {
            for (MultipartFile file : files) {
                // 파일이 비어있는 경우 스킵하거나 예외 처리를 할 수 있습니다.
                if (file == null || file.isEmpty()) continue;

                String url = fileUploadService.upload(file, FOLDER);
                uploadedUrls.add(url);
            }
            return uploadedUrls;
        } catch (Exception e) {
            // 업로드 중 예외 발생 시, 지금까지 업로드된 파일들 즉시 삭제
            deleteFiles(uploadedUrls);

            // 에러를 다시 던져 서비스 레이어에서 인지할 수 있게 함
            throw new PerformanceRegisterFailedException();
        }
    }

    @Override
    public void deleteFiles(List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) return;

        // 개별 삭제 시 예외가 발생해도 전체 루프가 깨지지 않도록 처리
        imageUrls.forEach(url -> {
            try {
                fileUploadService.delete(url);
            } catch (Exception e) {
                // 삭제 실패 시 로그를 남기거나 별도의 후처리 로직(Dead Letter Queue 등)을 고려할 수 있습니다.
                // 여기서는 우선 개별 삭제 실패가 전체 프로세스에 영향을 주지 않도록 합니다.
            }
        });
    }

    @Override
    public void saveAll(List<PerformanceImage> performanceImages) {
        // MySQL에 이미지 정보(URL 포함) 저장
        performanceImageJpaRepository.saveAll(performanceImages);
    }

    @Override
    public List<PerformanceImage> findAllByPerformanceId(Long performanceId) {
        return performanceImageJpaRepository.findAllByPerformanceIdOrderBySortOrderAsc(performanceId);
    }
}
