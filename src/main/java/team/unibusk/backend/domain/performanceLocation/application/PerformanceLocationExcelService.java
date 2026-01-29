package team.unibusk.backend.domain.performanceLocation.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import team.unibusk.backend.domain.performanceLocation.application.dto.ExcelDto;
import team.unibusk.backend.domain.performanceLocation.domain.ApplicationGuide;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocationImage;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocationRepository;
import team.unibusk.backend.global.kakaoMap.application.KakaoMapService;
import team.unibusk.backend.global.kakaoMap.application.dto.Coordinate;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PerformanceLocationExcelService {

    private final PerformanceLocationRepository performanceLocationRepository;
    private final KakaoMapService kakaoMapService;

    public int uploadPerformanceLocationExcelData(MultipartFile file) throws IOException {

        //엑셀 파일 검증
        validateExcelFile(file);

        //데이터 추출
        List<ExcelDto> dtos = getExcelDtoFromExcel(file);

        List<String> failedLogs = new ArrayList<>(); //실패 기록용 로그
        int successCount = 0;                          //성공

        for (int i = 0; i < dtos.size(); i++) {
            ExcelDto dto = dtos.get(i);
            int currentRowNum = i + 2;

            try{
                //필수 컬럼내용이 비었는지 확인
                validateRequiredFields(dto.getName(), dto.getAddress(), dto.getOperatorName(), dto.getOperatorPhoneNumber());
                //name, address 가 이미 존재하는지 확인
                validateName(dto.getName());

                //카카오 api로 위도 경도 계산
                Coordinate coordinate = kakaoMapService.getCoordinateByAddress(dto.getAddress())
                        .orElseThrow(() -> new RuntimeException("좌표를 가져오지 못했습니다."));

                // DB에 저장
                saveExcelDto(dto, coordinate);
                successCount++;



            }catch (DataIntegrityViolationException e) {
                // ✅ 경합 조건 발생 시 DB 제약 조건에 의해 여기서 걸러짐
                log.error("Row {} 중복 데이터 충돌: {}", currentRowNum, e.getMessage());
                failedLogs.add(String.format("[Row %d], [장소: %s], [실패 원인: 데이터베이스 제약 조건 위반(중복 데이터)]", currentRowNum, dto.getName()));
            }catch (Exception e) {
                //실패 시 로그 기록 및 리스트 추가
                String reason = e.getMessage() != null ? e.getMessage() : "알 수 없는 에러";
                log.error("Row {} 저장 실패: {}", currentRowNum, reason);
                failedLogs.add(String.format("[Row %d], [장소 이름 : %s], [장소: %s],  [실패 원인: %s]", currentRowNum, dto.getName(), dto.getAddress(), reason));
            }
        }
        // 결과 리포트 출력
        printFinalReport(successCount, failedLogs);

        return failedLogs.size();
    }

    //엑셀 파일 검증
    private void validateExcelFile(MultipartFile file) throws IOException{
        //  파일 존재 여부 확인
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드된 파일이 비어 있습니다.");
        }

        //  확장자 확인
        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.toLowerCase().endsWith(".xlsx")) {
            throw new IllegalArgumentException("지원하지 않는 파일 형식입니다. .xlsx 확장자만 가능합니다.");
        }
    }
    //데이터 엑셀 파일로부터 추출
    private List<ExcelDto> getExcelDtoFromExcel(MultipartFile file) throws IOException{
        List<ExcelDto> dtos = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            // 데이터가 시작되는 2번째 행(index 1)부터 끝까지 반복
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);

                // 행이 아예 비어있으면 비어있으면 스킵
                if (row == null || isRowEmpty(row)) continue;

                ExcelDto dto = ExcelDto.builder()
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
                        .build();

                dtos.add(dto);
            }
        }
        return dtos;

    }
    //엑셀에서 cell 값 가져오기
    private String getCellValue(Row row, int index) {
        Cell cell = row.getCell(index);
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            default -> "";
        };
    }
    //DB에 name 이미 존재하는지 확인
    private void validateName(String name){
        if (performanceLocationRepository.existsByName(name)) {
            throw new RuntimeException("이미 존재하는 장소 이름입니다. (입력값(name): " + name + ")");
        }

    }
    //저장할 엑셀 행에 필수적인 값 누락 없는지 검증
    private void validateRequiredFields(String name, String address, String operatorName, String operatorPhoneNumber) {
        if (name.isEmpty()) {
            throw new IllegalArgumentException("장소명(name) 컬럼이 비어있습니다.");
        }
        if (address.isEmpty()) {
            throw new IllegalArgumentException("주소(address) 컬럼이 비어있습니다.");
        }
        if (operatorName.isEmpty()) {
            throw new IllegalArgumentException("운영기관명(operatorName) 컬럼이 비어있습니다.");
        }
        if (operatorPhoneNumber.isEmpty()) {
            throw new IllegalArgumentException("운영기관 연락처(operatorPhoneNumber) 컬럼이 비어있습니다.");
        }
    }
    //DB에 저장
    private void saveExcelDto(ExcelDto dto, Coordinate coordinate) {
        //이미지 리스트 생성
        List<PerformanceLocationImage> images = new ArrayList<>();
        if (!dto.getImageUrl().isEmpty()) {
            images.add(PerformanceLocationImage.builder().imageUrl(dto.getImageUrl()).build());
        }

        List<ApplicationGuide> guides = new ArrayList<>();
        String[] contents = {dto.getGuide1(), dto.getGuide2(), dto.getGuide3()};
        for (String content : contents) {
            if (content != null && !content.isEmpty()) {
                guides.add(ApplicationGuide.builder().content(content).build());
            }
        }

        // 엔티티 생성 및 저장
        PerformanceLocation location = PerformanceLocation.builder()
                .name(dto.getName())
                .address(dto.getAddress())
                .operatorName(dto.getOperatorName())
                .operatorPhoneNumber(dto.getOperatorPhoneNumber())
                .availableHours(dto.getAvailableHours())
                .operatorUrl(dto.getOperatorUrl())
                .latitude(coordinate.latitude())
                .longitude(coordinate.longitude())
                .images(images)
                .applicationGuides(guides)
                .build();

        performanceLocationRepository.save(location);
    }
    //결과 출력
    private void printFinalReport(int successCount, List<String> failedLogs) {
        log.info("==========================================");
        log.info("엑셀 업로드 결과 리포트");
        log.info("성공 개수: {}건", successCount);
        log.info("실패 개수: {}건", failedLogs.size());

        if (!failedLogs.isEmpty()) {
            log.warn("---- 실패 원인 리스트 ----");
            failedLogs.forEach(log::warn);
        }
        log.info("==========================================");
    }
    //행의 모든 셀을 검사하여 실제로 내용이 하나라도 있는지 확인
    private boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK && !getCellValue(row, c).isEmpty()) {
                return false; // 하나라도 내용이 있으면 빈 행이 아님
            }
        }
        return true; // 모든 셀이 비어있으면 빈 행으로 간주
    }


}