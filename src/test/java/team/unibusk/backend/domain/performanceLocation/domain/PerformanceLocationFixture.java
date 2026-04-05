package team.unibusk.backend.domain.performanceLocation.domain;

import org.springframework.test.util.ReflectionTestUtils;

public class PerformanceLocationFixture {

    public static PerformanceLocation createLocation(Long id, String name) {
        PerformanceLocation location = PerformanceLocation.builder()
                .name(name)
                .address("서울시 마포구 어울마당로")
                .operatorName("마포구청")
                .operatorPhoneNumber("02-123-4567")
                .latitude(37.5546)
                .longitude(126.9206)
                .build();

        ReflectionTestUtils.setField(location, "id", id);

        return location;
    }

    public static PerformanceLocation createDetailLocation(
            Long id,
            String name,
            String address,
            Double latitude,
            Double longitude
    ) {
        PerformanceLocation location = PerformanceLocation.builder()
                .name(name)
                .address(address)
                .operatorName("관리주체")
                .operatorPhoneNumber("010-0000-0000")
                .availableHours("10:00 ~ 22:00")
                .operatorUrl("https://unibusk.site")
                .latitude(latitude)
                .longitude(longitude)
                .build();

        ReflectionTestUtils.setField(location, "id", id);

        return location;
    }

}
