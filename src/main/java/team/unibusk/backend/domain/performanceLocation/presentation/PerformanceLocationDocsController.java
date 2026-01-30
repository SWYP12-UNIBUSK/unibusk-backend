package team.unibusk.backend.domain.performanceLocation.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import team.unibusk.backend.domain.performanceLocation.application.dto.response.PerformanceLocationListResponse;
import team.unibusk.backend.domain.performanceLocation.application.dto.response.PerformanceLocationMapListResponse;
import team.unibusk.backend.domain.performanceLocation.application.dto.response.PerformanceLocationSearchListResponse;
import team.unibusk.backend.global.exception.ExceptionResponse;

@Tag(name = "Performance Location", description = "버스킹 장소 관련 API")
public interface PerformanceLocationDocsController {

    @Operation(
            summary = "버스킹 장소 검색 (페이징)",
            description = "공연 장소의 이름(name) 또는 주소(address)에 키워드가 포함된 장소를 페이징 처리하여 조회합니다.\n\n" +
                    "**에러 케이스:**\n" +
                    "* `EMPTY_KEYWORD`: 키워드가 비어있거나 공백일 경우 발생",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "검색 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PerformanceLocationListResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 (키워드 미입력 등)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionResponse.class),
                                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            value = "{\"code\": \"EMPTY_KEYWORD\", \"message\": \"검색어를 입력해주세요.\"}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러",
                            content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
            }
    )
    @Parameters({
            @Parameter(
                    name = "keyword",
                    description = "검색 키워드 (이름 또는 주소)",
                    required = true,
                    example = "서울"
            ),
            @Parameter(
                    name = "page",
                    description = "페이지 번호 (0부터 시작)",
                    schema = @Schema(type = "integer", defaultValue = "0"),
                    example = "0"
            ),
            @Parameter(
                    name = "size",
                    description = "한 페이지당 데이터 개수",
                    schema = @Schema(type = "integer", defaultValue = "4"),
                    example = "4"
            )
    })
    ResponseEntity<PerformanceLocationListResponse> search(
            @RequestParam(value = "keyword") String keyword,
            @PageableDefault(page = 0, size = 4) @ParameterObject Pageable pageable
    );

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
            @Parameter(description = "북쪽 위도", required = true) @RequestParam("north") Double north,
            @Parameter(description = "남쪽 위도", required = true) @RequestParam("south") Double south,
            @Parameter(description = "동쪽 경도", required = true) @RequestParam("east") Double east,
            @Parameter(description = "서쪽 경도", required = true) @RequestParam("west") Double west
    );

    @Operation(
            summary = "버스킹 장소 검색 (리스트)",
            description = "공연 장소의 이름(name) 또는 주소(address)에 keyword가 포함된 모든 장소를 리스트로 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PerformanceLocationSearchListResponse.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청",
                            content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 에러",
                            content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
            }
    )
    ResponseEntity<PerformanceLocationSearchListResponse> searchList(
            @Parameter(description = "검색 키워드", required = true) @RequestParam("keyword") String keyword
    );

}


