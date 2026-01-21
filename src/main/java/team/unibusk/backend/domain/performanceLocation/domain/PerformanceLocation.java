package team.unibusk.backend.domain.performanceLocation.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.unibusk.backend.global.domain.BaseTimeEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PerformanceLocation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 15)
    private String name;        // 장소 이름

    @Column(nullable = false, length = 20)
    private String phoneNumber;

    @Column(nullable = false, length = 100)
    private String address;     // 상세 주소

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal latitude;    // 위도 (예: 37.1234567)

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal longitude;  // 경도


    // 1:N 단방향 매핑: 장소 이미지 리스트
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "performance_location_id")
    private List<PerformanceLocationImage> images = new ArrayList<>();

    @Builder
    public PerformanceLocation(String name, String phoneNumber, String address,
                               BigDecimal latitude, BigDecimal longitude,
                               List<PerformanceLocationImage> images) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        if (images != null) {
            this.images.addAll(images);
        }
    }
}