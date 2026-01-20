package team.unibusk.backend.domain.performanceLocation.domain;

import jakarta.persistence.*;
import lombok.*;
import team.unibusk.backend.global.domain.BaseTimeEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "PerformanceLocationImage")
public class PerformanceLocationImage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String imageUrl;

    @Column(nullable = false)
    private Long sortOrder;


    public static PerformanceLocationImage create(String imageUrl, Long sortOrder) {
        return PerformanceLocationImage.builder()
                .imageUrl(imageUrl)
                .sortOrder(sortOrder)
                .build();
    }

}
