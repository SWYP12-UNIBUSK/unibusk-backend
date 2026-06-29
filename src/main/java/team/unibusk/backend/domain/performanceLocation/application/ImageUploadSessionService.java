package team.unibusk.backend.domain.performanceLocation.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.unibusk.backend.domain.performanceLocation.application.dto.request.ImageUploadFileRequest;
import team.unibusk.backend.domain.performanceLocation.application.dto.request.ImageUploadSessionCreateRequest;
import team.unibusk.backend.domain.performanceLocation.application.dto.response.ImageUploadItemResponse;
import team.unibusk.backend.domain.performanceLocation.application.dto.response.ImageUploadSessionCreateResponse;
import team.unibusk.backend.domain.performanceLocation.application.dto.response.RejectedImageFileResponse;
import team.unibusk.backend.domain.performanceLocation.domain.ImageUploadItem;
import team.unibusk.backend.domain.performanceLocation.domain.ImageUploadSession;
import team.unibusk.backend.domain.performanceLocation.infrastructure.ImageUploadItemJpaRepository;
import team.unibusk.backend.domain.performanceLocation.infrastructure.ImageUploadSessionJpaRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional
public class ImageUploadSessionService {

    private static final int MAX_FILES_PER_REQUEST = 1000;
    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024L;
    private static final long SESSION_EXPIRE_HOURS = 24;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    private final ImageUploadSessionJpaRepository sessionRepository;
    private final ImageUploadItemJpaRepository itemRepository;

    /*
        ImageUploadSessionCreateResponse의 흐름
        // 1. request.files() null/empty 검증
        // 2. 1000개 초과 검증
        // 3. 파일별 검증해서 accepted/rejected 분리
        // 4. accepted 기준 total count/size 계산
        // 5. session 저장
        // 6. session id 기반 rawObjectKey 생성
        // 7. item saveAll
        // 8. response 반환
     */
    public ImageUploadSessionCreateResponse createSession(ImageUploadSessionCreateRequest request) {
        validateRequest(request);

        List<ImageUploadFileRequest> acceptedFiles = new ArrayList<>();
        List<RejectedImageFileResponse> rejectedFiles = new ArrayList<>();
        Set<String> seenFileNames = new HashSet<>();

        for (ImageUploadFileRequest file : request.files()) {
            String rejectedReason = validateFile(file, seenFileNames);

            if (rejectedReason != null) {
                rejectedFiles.add(new RejectedImageFileResponse(
                        file == null ? null : file.originalFileName(),
                        rejectedReason
                ));
                continue;
            }

            acceptedFiles.add(file);
        }

        if (acceptedFiles.isEmpty()) {
            throw new IllegalArgumentException("등록 가능한 이미지가 없습니다.");
        }

        long totalSize = acceptedFiles.stream()
                .mapToLong(ImageUploadFileRequest::fileSize)
                .sum();

        ImageUploadSession session = ImageUploadSession.builder()
                .totalItemCount(acceptedFiles.size())
                .totalSize(totalSize)
                .expiresAt(LocalDateTime.now().plusHours(SESSION_EXPIRE_HOURS))
                .build();

        ImageUploadSession savedSession = sessionRepository.save(session);

        List<ImageUploadItem> items = acceptedFiles.stream()
                .map(file -> ImageUploadItem.builder()
                        .session(savedSession)
                        .originalFileName(file.originalFileName().trim())
                        .contentType(file.contentType())
                        .fileSize(file.fileSize())
                        .rawObjectKey(createRawObjectKey(savedSession.getId(), file.originalFileName()))
                        .build())
                .toList();

        List<ImageUploadItem> savedItems = itemRepository.saveAll(items);

        List<ImageUploadItemResponse> itemResponses = savedItems.stream()
                .map(item -> new ImageUploadItemResponse(
                        item.getId(),
                        item.getOriginalFileName(),
                        item.getRawObjectKey(),
                        item.getStatus()
                ))
                .toList();

        return new ImageUploadSessionCreateResponse(
                savedSession.getId(),
                savedSession.getStatus(),
                savedItems.size(),
                rejectedFiles.size(),
                itemResponses,
                rejectedFiles
        );
    }

    private void validateRequest(ImageUploadSessionCreateRequest request) {
        if (request == null || request.files() == null || request.files().isEmpty()) {
            throw new IllegalArgumentException("이미지 파일 목록은 필수입니다.");
        }

        if (request.files().size() > MAX_FILES_PER_REQUEST) {
            throw new IllegalArgumentException("요청당 이미지 파일은 최대 1,000개까지 등록할 수 있습니다.");
        }
    }

    private String validateFile(ImageUploadFileRequest file, Set<String> seenFileNames) {
        if (file == null) {
            return "파일 정보가 없습니다.";
        }

        String originalFileName = file.originalFileName();

        if (originalFileName == null || originalFileName.isBlank()) {
            return "파일명은 필수입니다.";
        }

        String trimmedFileName = originalFileName.trim();

        if (trimmedFileName.length() > 255) {
            return "파일명은 최대 255자까지 허용됩니다.";
        }

        String normalizedFileName = trimmedFileName.toLowerCase();

        if (!seenFileNames.add(normalizedFileName)) {
            return "같은 요청 안에서 중복된 파일명입니다.";
        }

        String extension = extractExtension(trimmedFileName);

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            return "허용되지 않는 확장자입니다.";
        }

        if (file.contentType() == null || !ALLOWED_CONTENT_TYPES.contains(file.contentType())) {
            return "허용되지 않는 Content-Type입니다.";
        }

        if (file.fileSize() <= 0) {
            return "파일 크기는 0보다 커야 합니다.";
        }

        if (file.fileSize() > MAX_FILE_SIZE) {
            return "파일 크기는 최대 20MB까지 허용됩니다.";
        }

        return null;
    }

    private String createRawObjectKey(Long sessionId, String originalFileName) {
        String extension = extractExtension(originalFileName);

        return "temp/performance-location-imports/"
                + sessionId
                + "/raw/"
                + UUID.randomUUID()
                + "."
                + extension;
    }

    private String extractExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');

        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            return "";
        }

        return fileName.substring(dotIndex + 1).toLowerCase();
    }
}