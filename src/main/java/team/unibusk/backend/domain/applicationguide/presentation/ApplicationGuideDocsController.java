package team.unibusk.backend.domain.applicationguide.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import team.unibusk.backend.domain.applicationguide.application.dto.response.ApplicationGuideListResponse;
import team.unibusk.backend.global.exception.ExceptionResponse;

@Tag(name = "Application Guide", description = "버스킹 장소 신청 가이드 관련 API")
public interface ApplicationGuideDocsController {

    @Operation(
            summary = "버스킹 장소 신청 가이드 조회",
            description = "performanceLocationId를 통해 특정 버스킹 장소의 신청 가이드 목록을 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApplicationGuideListResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "존재하지 않는 버스킹 장소",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<ApplicationGuideListResponse> getApplicationGuides(
            @Parameter(description = "버스킹 장소 ID", required = true, example = "1")
            @PathVariable Long performanceLocationId
    );

}
