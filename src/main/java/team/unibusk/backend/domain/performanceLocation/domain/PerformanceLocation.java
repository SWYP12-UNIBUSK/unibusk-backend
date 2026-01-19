package team.unibusk.backend.domain.performanceLocation.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import team.unibusk.backend.domain.performanceLocationImage.domain.PerformanceLocationImage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name="PerformanceLocation")
public class PerformanceLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 20)
    private String phone;

    @Column(nullable = false, length = 255)
    private String location;

    // decimal(10,7)은 BigDecimal로 매핑하는 것이 정확합니다.
    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal longitude;

    @BatchSize(size = 100)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "performance_location_id") // DB의 이미지 테이블에 FK 생성
    private List<PerformanceLocationImage> images = new ArrayList<>();

    public static PerformanceLocation from(String name, String phone, String location, BigDecimal latitude, BigDecimal longitude) {
        return PerformanceLocation.builder()
                .name(name)
                .phone(phone)
                .location(location)
                .latitude(latitude)
                .longitude(longitude)
                .images(new ArrayList<>())
                .build();
    }

    public void addImage(PerformanceLocationImage image) {
        this.images.add(image);
    }
}
