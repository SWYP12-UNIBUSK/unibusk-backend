package team.unibusk.backend.domain.PerformanceLocation.domain;

import jakarta.persistence.*;
import lombok.*;
import team.unibusk.backend.global.domain.BaseTimeEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "performance_location") // DB 테이블명과 정확히 일치
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PerformanceLocation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String phone;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false, precision = 13, scale = 10)
    private BigDecimal latitude; // Double 타입 사용

    @Column(nullable = false, precision = 13, scale = 10)
    private BigDecimal longitude;

    // 테이블이 미리 존재하므로, 연관관계만 명시 (mappedBy 주의)
    @OneToMany(mappedBy = "performanceLocation", fetch = FetchType.LAZY)
    private List<PerformanceLocationImage> images = new ArrayList<>();

    @Builder
    public PerformanceLocation(String name, String phone, String location, BigDecimal latitude, BigDecimal longitude) {
        this.name = name;
        this.phone = phone;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}