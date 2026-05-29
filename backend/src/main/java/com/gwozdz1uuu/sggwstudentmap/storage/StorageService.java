package com.gwozdz1uuu.sggwstudentmap.storage;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class StorageService {

    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private static final long MAX_SIZE = 5 * 1024 * 1024; // 5 MB

    private final Storage storage;
    private final String bucketName;

    public StorageService(Storage storage, String storageBucketName) {
        this.storage = storage;
        this.bucketName = storageBucketName;
    }

    /**
     * Uploads a file to GCS and returns the public download URL.
     *
     * @param file     the uploaded multipart file
     * @param folder   top-level folder (e.g. "feed", "reviews", "places")
     * @param entityId parent entity id used as subfolder
     * @return public URL of the uploaded object
     */
    public String upload(MultipartFile file, String folder, Integer entityId) {
        validate(file);

        String ext = extensionFor(file.getContentType());
        String objectPath = "%s/%d/%s.%s".formatted(folder, entityId, UUID.randomUUID(), ext);

        BlobId blobId = BlobId.of(bucketName, objectPath);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();

        try {
            storage.create(blobInfo, file.getBytes());
        } catch (IOException e) {
            log.error("Failed to upload file to GCS: {}", objectPath, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "File upload failed");
        }

        String encodedPath = URLEncoder.encode(objectPath, StandardCharsets.UTF_8);
        return "https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media"
                .formatted(bucketName, encodedPath);
    }

    /**
     * Deletes an object from GCS by its full URL (as stored in the database).
     * Silently ignores if the object doesn't exist.
     */
    public void delete(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) return;

        String objectPath = extractObjectPath(fileUrl);
        if (objectPath == null) return;

        try {
            storage.delete(BlobId.of(bucketName, objectPath));
        } catch (Exception e) {
            log.warn("Failed to delete GCS object: {}", objectPath, e);
        }
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Unsupported file type. Allowed: JPEG, PNG, WebP");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "File too large. Maximum size: 5 MB");
        }
    }

    private static String extensionFor(String contentType) {
        return switch (contentType) {
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            default -> "jpg";
        };
    }

    /**
     * Extracts the GCS object path from a Firebase Storage URL.
     */
    private String extractObjectPath(String url) {
        String marker = "/o/";
        int start = url.indexOf(marker);
        if (start < 0) return null;
        String afterMarker = url.substring(start + marker.length());
        int queryStart = afterMarker.indexOf('?');
        String encoded = queryStart >= 0 ? afterMarker.substring(0, queryStart) : afterMarker;
        return java.net.URLDecoder.decode(encoded, StandardCharsets.UTF_8);
    }
}
