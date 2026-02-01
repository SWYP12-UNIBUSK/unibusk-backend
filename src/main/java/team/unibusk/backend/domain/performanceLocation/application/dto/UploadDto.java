package team.unibusk.backend.domain.performanceLocation.application.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class UploadDto {
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

    private String imageUrlinFolder;
    private String imageUrlinS3;

    public void setImageUrl(String url){
        this.imageUrl = url;
    }
}