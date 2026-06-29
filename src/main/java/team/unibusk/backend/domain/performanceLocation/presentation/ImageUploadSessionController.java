package team.unibusk.backend.domain.performanceLocation.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import team.unibusk.backend.domain.performanceLocation.application.ImageUploadSessionService;
import team.unibusk.backend.domain.performanceLocation.application.dto.request.ImageUploadSessionCreateRequest;
import team.unibusk.backend.domain.performanceLocation.application.dto.response.ImageUploadSessionCreateResponse;

@RestController
@RequiredArgsConstructor
public class ImageUploadSessionController {

    private final ImageUploadSessionService imageUploadSessionService;

    @PostMapping("/performance-locations/image-upload-sessions")
    public ResponseEntity<ImageUploadSessionCreateResponse> createSession(
            @RequestBody ImageUploadSessionCreateRequest request
    ) {
        ImageUploadSessionCreateResponse response = imageUploadSessionService.createSession(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}