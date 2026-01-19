package team.unibusk.backend.global.file.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import team.unibusk.backend.global.file.port.FileStoragePort;
import team.unibusk.backend.global.file.presentation.exception.FileUploadFailedException;
import team.unibusk.backend.global.file.presentation.exception.InvalidFileTypeException;

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
}
