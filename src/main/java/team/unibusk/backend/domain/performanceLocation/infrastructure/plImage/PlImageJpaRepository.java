package team.unibusk.backend.domain.performanceLocation.infrastructure.plImage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team.unibusk.backend.domain.performanceLocation.domain.PlImage;

@Repository
public interface PlImageJpaRepository extends JpaRepository<PlImage, Long> {

}
