package team.unibusk.backend.global.file.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import team.unibusk.backend.global.file.port.FileStoragePort;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3FileStorageAdapter implements FileStoragePort {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.public-url:https://%s.s3.amazonaws.com}")
    private String publicUrlFormat;

    @Override
    public String upload(MultipartFile file, String folder) {
        String key = createKey(file.getOriginalFilename(), folder);

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromBytes(file.getBytes())
            );

        } catch (IOException e) {
            throw new RuntimeException("S3 파일 업로드 실패", e);
        }

        return getPublicUrl(key);
    }

    private String createKey(String originalFilename, String folder) {
        String extension = getExtension(originalFilename);
        return folder + "/" + UUID.randomUUID() + "." + extension;
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new IllegalArgumentException("파일 확장자가 없습니다.");
        }
        return filename.substring(filename.lastIndexOf('.') + 1);
    }

    private String getPublicUrl(String key) {
        String encodedKey = URLEncoder.encode(key, StandardCharsets.UTF_8)
                .replace("+", "%20");
        return String.format(publicUrlFormat, bucket) + "/" + encodedKey;
    }
}
