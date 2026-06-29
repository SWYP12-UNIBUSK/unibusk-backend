package team.unibusk.backend.domain.performanceLocation.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import team.unibusk.backend.domain.performanceLocation.domain.ImageUploadItem;
import team.unibusk.backend.domain.performanceLocation.domain.ImageUploadItemStatus;

import java.util.List;

public interface ImageUploadItemJpaRepository extends JpaRepository<ImageUploadItem, Long> {

    List<ImageUploadItem> findBySessionId(Long sessionId);

    boolean existsBySessionIdAndOriginalFileName(Long sessionId, String originalFileName);

    long countBySessionIdAndStatus(Long sessionId, ImageUploadItemStatus status);
}