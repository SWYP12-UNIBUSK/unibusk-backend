package team.unibusk.backend.domain.performanceLocation.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

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
    private String phoneNumber;

    @Column(nullable = false, length = 255)
    private String location;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal longitude;

    public static PerformanceLocation from(String name, String phoneNumber, String location, BigDecimal latitude, BigDecimal longitude) {
        return PerformanceLocation.builder()
                .name(name)
                .phoneNumber(phoneNumber)
                .location(location)
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }
}