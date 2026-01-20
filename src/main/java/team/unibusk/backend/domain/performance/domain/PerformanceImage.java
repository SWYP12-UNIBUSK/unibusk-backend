package team.unibusk.backend.domain.performance.domain;

import jakarta.persistence.*;
import lombok.*;
import team.unibusk.backend.global.domain.BaseTimeEntity;

@Entity
@Getter
@Table(name = "PerformanceImage")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PerformanceImage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String imageUrl;

    @Column(nullable = false)
    private Long sortOrder = 1L; // 기본값 1 설정

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id", nullable = false)
    private Performance performance;

    @Builder
    private PerformanceImage(String imageUrl, Long sortOrder, Performance performance) {
        this.imageUrl = imageUrl;
        this.sortOrder = (sortOrder == null) ? 1L : sortOrder;
        this.performance = performance;
    }


}