package team.unibusk.backend.domain.performanceLocation.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import team.unibusk.backend.global.domain.BaseTimeEntity;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "PerformanceLocationImage")
public class PlImage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_location_id", nullable = false)
    private Pl pl;

    @Column(nullable = false, length = 255)
    private String imageUrl;

    @Column(nullable = false)
    private Long sortOrder;

    @Builder
    public PlImage(Pl pl, String imgUrl, Long sortOrder){
        this.pl = pl;
        this.imageUrl = imgUrl;
        this.sortOrder = sortOrder;
    }

}
