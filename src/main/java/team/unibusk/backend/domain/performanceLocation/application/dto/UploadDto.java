package team.unibusk.backend.domain.performanceLocation.application.dto;

import lombok.Builder;

@Builder
public record UploadDto(
        int rowNum,
        String name,
        String address,
        String operatorName,
        String operatorPhoneNumber,
        String availableHours,
        String operatorUrl,
        String imageUrl,
        String guide1,
        String guide2,
        String guide3,
        String imageUrlinFolder,
        String imageUrlinS3
) {

    public UploadDto withImageUrl(String imageUrl) {
        return UploadDto.builder()
                .rowNum(this.rowNum)
                .name(this.name)
                .address(this.address)
                .operatorName(this.operatorName)
                .operatorPhoneNumber(this.operatorPhoneNumber)
                .availableHours(this.availableHours)
                .operatorUrl(this.operatorUrl)
                .imageUrl(imageUrl) // 변경할 값
                .guide1(this.guide1)
                .guide2(this.guide2)
                .guide3(this.guide3)
                .imageUrlinFolder(this.imageUrlinFolder)
                .imageUrlinS3(this.imageUrlinS3)
                .build();
    }
}