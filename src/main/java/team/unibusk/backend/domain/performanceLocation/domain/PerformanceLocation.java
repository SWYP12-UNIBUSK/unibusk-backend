package team.unibusk.backend.domain.performanceLocation.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Builder
    private PerformanceLocation(String name, String phone, String location, BigDecimal latitude, BigDecimal longitude) {
        this.name = name;
        this.phone = phone;
        this.location = location;
        this.latitude = latitude != null ? latitude : BigDecimal.ZERO;
        this.longitude = longitude != null ? longitude : BigDecimal.ZERO;
    }
}
