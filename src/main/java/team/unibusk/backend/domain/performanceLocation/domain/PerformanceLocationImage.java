package team.unibusk.backend.domain.performanceLocation.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.unibusk.backend.global.domain.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PerformanceLocationImage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 512)
    private String imageUrl;

    @Column(nullable = false)
    private int sortOrder;

    @Builder
    private PerformanceLocationImage(String imageUrl, int sortOrder) {
        this.imageUrl = imageUrl;
        this.sortOrder = sortOrder;
    }
}
