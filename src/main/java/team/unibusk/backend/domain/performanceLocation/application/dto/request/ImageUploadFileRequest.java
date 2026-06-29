package team.unibusk.backend.domain.performanceLocation.application.dto.request;

public record ImageUploadFileRequest(
        String originalFileName,
        String contentType,
        long fileSize
) {
}