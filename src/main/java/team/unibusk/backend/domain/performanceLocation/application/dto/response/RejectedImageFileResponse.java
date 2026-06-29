package team.unibusk.backend.domain.performanceLocation.application.dto.response;

public record RejectedImageFileResponse(
        String originalFileName,
        String reason
) {
}