package team.unibusk.backend.domain.performanceLocation.application.dto.response;

import team.unibusk.backend.domain.performanceLocation.domain.ImageUploadSessionStatus;

import java.util.List;

public record ImageUploadSessionCreateResponse(
        Long uploadSessionId,
        ImageUploadSessionStatus status,
        int registeredCount,
        int rejectedCount,
        List<ImageUploadItemResponse> items,
        List<RejectedImageFileResponse> rejectedFiles
) {
}