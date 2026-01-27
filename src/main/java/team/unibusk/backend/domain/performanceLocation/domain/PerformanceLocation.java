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

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 20)
    private String phoneNumber;

    @Column(nullable = false, length = 255)
    private String location;

    @Column(nullable = false)
    private Double latitude; // 위도

    @Column(nullable = false)
    private Double longitude; // 경도

    // 애그리거트 루트를 통해서만 자식의 생명주기가 관리됨
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "performance_location_id") // 자식 테이블에 FK 생성
    private List<PerformanceLocationImage> images = new ArrayList<>();

    @Builder
    private PerformanceLocation(String name, String phoneNumber, String location,
                                Double latitude, Double longitude,
                                List<PerformanceLocationImage> images) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;

        if(images != null) this.images.addAll(images);
    }

}
