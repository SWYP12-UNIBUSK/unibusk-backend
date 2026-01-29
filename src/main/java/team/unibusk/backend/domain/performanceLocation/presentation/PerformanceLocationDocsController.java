package team.unibusk.backend.domain.performanceLocation.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import team.unibusk.backend.domain.performanceLocation.application.dto.response.PerformanceLocationMapListResponse;
import team.unibusk.backend.global.exception.ExceptionResponse;

@Tag(name = "Performance Location", description = "버스킹 장소 관련 API")
public interface PerformanceLocationDocsController {

    @Operation(
            summary = "지도 내 버스킹 장소 조회",
            description = "지정한 지도 영역(north, south, east, west) 내에 존재하는 모든 버스킹 장소를 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PerformanceLocationMapListResponse.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청",
                            content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 에러",
                            content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
            }
    )
    ResponseEntity<PerformanceLocationMapListResponse> getLocationsInMap(
            @Parameter(description = "북쪽 경도", required = true) @RequestParam("north") Double north,
            @Parameter(description = "남쪽 경도", required = true) @RequestParam("south") Double south,
            @Parameter(description = "동쪽 위도", required = true) @RequestParam("east") Double east,
            @Parameter(description = "서쪽 위도", required = true) @RequestParam("west") Double west
    );
}
