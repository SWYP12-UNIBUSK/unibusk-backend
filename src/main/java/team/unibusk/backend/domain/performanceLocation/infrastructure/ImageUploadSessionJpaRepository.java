package team.unibusk.backend.domain.performanceLocation.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import team.unibusk.backend.domain.performanceLocation.domain.ImageUploadSession;

public interface ImageUploadSessionJpaRepository extends JpaRepository<ImageUploadSession, Long> {
}