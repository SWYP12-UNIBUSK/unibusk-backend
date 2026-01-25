package team.unibusk.backend.domain.performanceLocation.presentation.request;

import lombok.Builder;
import team.unibusk.backend.domain.performanceLocation.application.dto.requset.PerformanceLocationListServiceRequest;

@Builder
public record PerformanceLocationListRequest (

        Integer page,
        Integer size,
        String sort,
        Boolean isDesc // true : 내림차순, false : 오름차순
){
    public PerformanceLocationListRequest {
        if (page == null) page = 0;
        if (size == null) size = 8;

        // sort 값에 허용된 필드만 들어올 수 있게 정의, 만약 잘못된 sort 를 선택하면 name으로 강제 변환
        java.util.Set<String> allowedFields = java.util.Set.of("id", "name", "address", "createdAt");
        if(sort == null || !allowedFields.contains(sort)) { sort="name"; }
        if (isDesc == null) isDesc = true; // 기본값은 내림차순
    }

    public PerformanceLocationListServiceRequest toServiceRequest(){
        return PerformanceLocationListServiceRequest.builder()
                .page(this.page)
                .size(this.size)
                .sort(this.sort)
                .isDesc(this.isDesc)
                .build();
    }
}
