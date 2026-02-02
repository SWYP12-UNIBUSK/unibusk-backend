package team.unibusk.backend.domain.performanceLocation.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.unibusk.backend.global.domain.BaseTimeEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "uk_performance_location_name", columnNames = "name")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PerformanceLocation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String operatorName;

    @Column(nullable = false)
    private String operatorPhoneNumber;

    @Column(nullable = true)
    private String availableHours;

    @Column(nullable = true)
    private String operatorUrl; //신청 사이트

    @Column(nullable = false)
    private Double latitude; // 위도

    @Column(nullable = false)
    private Double longitude; // 경도

    // 애그리거트 루트를 통해서만 자식의 생명주기가 관리됨
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "performance_location_id") // 자식 테이블에 FK 생성
    private List<PerformanceLocationImage> images = new ArrayList<>();

    @Builder
    private PerformanceLocation(
            String name, String address, String operatorName, String operatorPhoneNumber,
            String availableHours, String operatorUrl,
            Double latitude, Double longitude,
            List<PerformanceLocationImage> images
    ) {
        this.name = name;
        this.address = address;
        this.operatorName = operatorName;
        this.operatorPhoneNumber = operatorPhoneNumber;
        this.availableHours = availableHours;
        this.operatorUrl = operatorUrl;
        this.latitude = latitude;
        this.longitude = longitude;

        if (images != null) this.images.addAll(images);
    }

}
