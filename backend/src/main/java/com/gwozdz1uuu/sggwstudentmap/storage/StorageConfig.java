package com.gwozdz1uuu.sggwstudentmap.storage;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class StorageConfig {

    @Value("${app.storage.bucket}")
    private String bucketName;

    @Value("${app.storage.credentials-path:}")
    private String credentialsPath;

    @Bean
    public Storage googleCloudStorage() throws IOException {
        StorageOptions.Builder builder = StorageOptions.newBuilder();

        if (credentialsPath != null && !credentialsPath.isBlank()) {
            builder.setCredentials(GoogleCredentials.fromStream(new FileInputStream(credentialsPath)));
        }

        return builder.build().getService();
    }

    @Bean
    public String storageBucketName() {
        return bucketName;
    }
}
