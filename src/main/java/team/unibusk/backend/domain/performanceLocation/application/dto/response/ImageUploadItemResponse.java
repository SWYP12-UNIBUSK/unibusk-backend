package team.unibusk.backend.domain.performanceLocation.application.dto.response;

import team.unibusk.backend.domain.performanceLocation.domain.ImageUploadItemStatus;

public record ImageUploadItemResponse(
        Long itemId,
        String originalFileName,
        String objectKey,
        ImageUploadItemStatus status
) {
}