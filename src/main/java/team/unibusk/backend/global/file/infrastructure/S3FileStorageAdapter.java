package team.unibusk.backend.global.file.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import team.unibusk.backend.global.file.port.FileStoragePort;
import team.unibusk.backend.global.file.presentation.exception.FileDeleteFailedException;
import team.unibusk.backend.global.file.presentation.exception.FileUploadFailedException;
import team.unibusk.backend.global.file.presentation.exception.InvalidFileTypeException;
import team.unibusk.backend.global.file.presentation.exception.InvalidFileUrlException;

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

            try (var in = file.getInputStream()) {
                s3Client.putObject(
                        putObjectRequest,
                        RequestBody.fromInputStream(in, file.getSize())
                );
            }

        } catch (IOException | software.amazon.awssdk.core.exception.SdkException e) {
            throw new FileUploadFailedException();
        }

        return getPublicUrl(key);
    }

    private String createKey(String originalFilename, String folder) {
        String extension = getExtension(originalFilename);
        return folder + "/" + UUID.randomUUID() + "." + extension;
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".") || filename.endsWith(".")) {
            throw new InvalidFileTypeException();
        }
        return filename.substring(filename.lastIndexOf('.') + 1);
    }

    private String getPublicUrl(String key) {
        String[] parts = key.split("/");
        String encodedKey = java.util.Arrays.stream(parts)
                .map(part -> URLEncoder.encode(part, StandardCharsets.UTF_8).replace("+", "%20"))
                .collect(java.util.stream.Collectors.joining("/"));
        return String.format(publicUrlFormat, bucket) + "/" + encodedKey;
    }

    @Override
    public void delete(String fileUrl) {
        String key = extractKeyFromUrl(fileUrl);

        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
        } catch (software.amazon.awssdk.core.exception.SdkException e) {
            throw new FileDeleteFailedException();
        }
    }

    private String extractKeyFromUrl(String fileUrl) {
        try {
            // URL에서 버킷 주소 이후의 경로(Key)만 추출
            // 예: https://bucket.s3.amazonaws.com/folder/uuid.jpg -> folder/uuid.jpg
            String baseUrl = String.format(publicUrlFormat, bucket);
            if (!fileUrl.startsWith(baseUrl)) {
                throw new InvalidFileUrlException();
            }
            return fileUrl.substring(baseUrl.length() + 1);
        } catch (Exception e) {
            throw new InvalidFileUrlException();
        }
    }
}