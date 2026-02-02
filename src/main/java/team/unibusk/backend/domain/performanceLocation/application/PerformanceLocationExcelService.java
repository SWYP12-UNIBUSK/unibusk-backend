package team.unibusk.backend.domain.performanceLocation.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import team.unibusk.backend.domain.applicationguide.domain.ApplicationGuide;
import team.unibusk.backend.domain.applicationguide.domain.ApplicationGuideRepository;
import team.unibusk.backend.domain.performanceLocation.application.dto.UploadDto;
import team.unibusk.backend.domain.performanceLocation.application.dto.response.PerformanceLocationExcelResponse;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocationImage;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocationRepository;
import team.unibusk.backend.global.file.application.FileUploadService;
import team.unibusk.backend.global.kakaoMap.application.KakaoMapService;
import team.unibusk.backend.global.kakaoMap.application.dto.Coordinate;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PerformanceLocationExcelService {

    private final PerformanceLocationRepository performanceLocationRepository;
    private final ApplicationGuideRepository applicationGuideRepository;
    private final KakaoMapService kakaoMapService;
    private final FileUploadService fileUploadService;

    private static final String PERFORMANCELOCATION_FOLDER = "performanceLocations";
    private static final DataFormatter FORMATTER = new DataFormatter();

    @Transactional
    public PerformanceLocationExcelResponse uploadPerformanceLocationExcelData(MultipartFile excelFile, List<MultipartFile> images) throws IOException {

        // 검증 및 데이터 준비
        validateExcelFile(excelFile);
        List<UploadDto> dtos = getExcelDtoFromExcel(excelFile);
        Map<String, MultipartFile> imageMap = createImageMap(images);

        List<String> failedLogs = new ArrayList<>();
        int successCount = 0;

        // 각 행별 순차 처리
        for (UploadDto dto : dtos) {
            int actualRowNum = dto.rowNum();

            try {
                // [검증 1] 필수 필드 체크
                validateRequiredFields(dto);

                // [검증 2] 이름 중복 체크
                checkAlreadyExists(dto.name());

                // [데이터 변환] 주소 -> 좌표(위경도) 변환
                Coordinate coordinate = kakaoMapService.getCoordinateByAddress(dto.address())
                        .orElseThrow(() -> new RuntimeException("주소에 해당하는 좌표를 찾을 수 없습니다."));

                // [이미지 처리] 엑셀 파일명에 맞는 이미지 찾기 및 S3 업로드
                String s3Url = null;
                if (StringUtils.hasText(dto.imageUrl())) {
                    MultipartFile matchedImage = getMatchedImageFile(dto.imageUrl(), imageMap);
                    s3Url = fileUploadService.upload(matchedImage, PERFORMANCELOCATION_FOLDER);
                }

                // [최종 저장] DB에 엔티티 저장
                saveEntity(dto, coordinate, s3Url);
                successCount++;

            } catch (DataIntegrityViolationException e) {
                failedLogs.add(String.format("[Row %d] [장소: %s] 실패: DB 제약 조건 위반(중복 가능성)", actualRowNum, dto.name()));
            } catch (Exception e) {
                String reason = e.getMessage() != null ? e.getMessage() : "알 수 없는 에러";
                failedLogs.add(String.format("[Row %d] [장소: %s] 실패: %s", actualRowNum, dto.name(), reason));
            }
        }

        printFinalReport(successCount, failedLogs);

        return PerformanceLocationExcelResponse.builder()
                .successCount(successCount)
                .failCount(failedLogs.size())
                .failedLogList(failedLogs)
                .build();
    }

    // --- 유틸리티 메서드 영역 ---

    private void validateExcelFile(MultipartFile file) {
        // 1. 파일 존재 여부 및 비어있는지 확인
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드된 엑셀 파일이 없거나 비어 있습니다.");
        }

        // 2. 파일 확장자 확인
        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.toLowerCase().endsWith(".xlsx")) {
            throw new IllegalArgumentException("지원하지 않는 파일 형식입니다. .xlsx 확장자 파일만 업로드 가능합니다.");
        }
    }

    private List<UploadDto> getExcelDtoFromExcel(MultipartFile file) throws IOException {
        List<UploadDto> dtos = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) continue;

                dtos.add(UploadDto.builder()
                        .rowNum(row.getRowNum() + 1)
                        .name(getCellValue(row, 0))
                        .address(getCellValue(row, 1))
                        .operatorName(getCellValue(row, 2))
                        .operatorPhoneNumber(getCellValue(row, 3))
                        .availableHours(getCellValue(row, 4))
                        .operatorUrl(getCellValue(row, 5))
                        .imageUrl(getCellValue(row, 6))
                        .guide1(getCellValue(row, 7))
                        .guide2(getCellValue(row, 8))
                        .guide3(getCellValue(row, 9))
                        .build());
            }
        }
        return dtos;
    }

    private String getCellValue(Row row, int index) {
        Cell cell = row.getCell(index);
        return (cell == null) ? "" : FORMATTER.formatCellValue(cell).trim();
    }

    private Map<String, MultipartFile> createImageMap(List<MultipartFile> imageFolder) {
        if (imageFolder == null) return Collections.emptyMap();
        return imageFolder.stream()
                .filter(file -> !file.isEmpty() && StringUtils.hasText(file.getOriginalFilename()))
                .collect(Collectors.toMap(MultipartFile::getOriginalFilename, f -> f, (e, r) -> e));
    }

    private MultipartFile getMatchedImageFile(String imageUrl, Map<String, MultipartFile> imageMap) {
        for (Map.Entry<String, MultipartFile> entry : imageMap.entrySet()) {
            String fileName = entry.getKey();
            if (fileName.startsWith(imageUrl)) {
                return entry.getValue();
            }
        }
        throw new RuntimeException("폴더 내에 '" + imageUrl + "' 파일이 없습니다.");
    }

    private void validateRequiredFields(UploadDto dto) {
        if (!StringUtils.hasText(dto.name())) throw new IllegalArgumentException("장소명이 비어있습니다.");
        if (!StringUtils.hasText(dto.address())) throw new IllegalArgumentException("주소가 비어있습니다.");
        if (!StringUtils.hasText(dto.operatorName())) throw new IllegalArgumentException("운영기관명이 비어있습니다.");
        if (!StringUtils.hasText(dto.operatorPhoneNumber())) throw new IllegalArgumentException("연락처가 비어있습니다.");
    }

    private void checkAlreadyExists(String name) {
        if (performanceLocationRepository.findByName(name).isPresent()) {
            throw new RuntimeException("이미 존재하는 장소 이름입니다.");
        }
    }

    private void saveEntity(UploadDto dto, Coordinate coordinate, String s3Url) {
        List<PerformanceLocationImage> images = new ArrayList<>();
        if (StringUtils.hasText(s3Url)) {
            images.add(PerformanceLocationImage.builder().imageUrl(s3Url).build());
        }

        PerformanceLocation location = PerformanceLocation.builder()
                .name(dto.name())
                .address(dto.address())
                .operatorName(dto.operatorName())
                .operatorPhoneNumber(dto.operatorPhoneNumber())
                .availableHours(dto.availableHours())
                .operatorUrl(dto.operatorUrl())
                .latitude(coordinate.latitude())
                .longitude(coordinate.longitude())
                .images(images)
                .build();

        PerformanceLocation savedLocation = performanceLocationRepository.save(location);

        List<ApplicationGuide> guides = new ArrayList<>();
        String[] contents = {dto.guide1(), dto.guide2(), dto.guide3()};

        for (String content : contents) {
            if (StringUtils.hasText(content)) {
                // 정의하신 create 정적 팩토리 메서드 활용
                guides.add(ApplicationGuide.create(content, savedLocation));
            }
        }

        // 리스트가 비어있지 않을 때만 일괄 저장
        if (!guides.isEmpty()) {
            applicationGuideRepository.saveAll(guides);
        }
    }

    private void printFinalReport(int successCount, List<String> failedLogs) {
        log.info("업로드 완료 - 성공: {}, 실패: {}", successCount, failedLogs.size());
        if (!failedLogs.isEmpty()) failedLogs.forEach(log::warn);
    }

    private boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK && StringUtils.hasText(FORMATTER.formatCellValue(cell))) {
                return false;
            }
        }
        return true;
    }
}