package team.unibusk.backend.domain.performanceLocation.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExcelDto {
    private int rowNum;
    private String name;
    private String address;
    private String operatorName;
    private String operatorPhoneNumber;
    private String availableHours;
    private String operatorUrl;
    private String imageUrl;
    private String guide1;
    private String guide2;
    private String guide3;

    // ✅ 필수값 검증 메서드
    public void validate() {
        if (name.isEmpty()) throw new IllegalArgumentException("장소명(name) 컬럼이 비어있습니다.");
        if (address.isEmpty()) throw new IllegalArgumentException("주소(address) 컬럼이 비어있습니다.");
        if (operatorName.isEmpty()) throw new IllegalArgumentException("운영기관명(operatorName) 컬럼이 비어있습니다.");
        if (operatorPhoneNumber.isEmpty()) throw new IllegalArgumentException("운영기관 연락처(operatorPhoneNumber) 컬럼이 비어있습니다.");
    }
}