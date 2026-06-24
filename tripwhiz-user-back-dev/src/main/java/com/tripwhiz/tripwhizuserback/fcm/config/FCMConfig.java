package com.tripwhiz.tripwhizuserback.fcm.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FCMConfig {

    private final ResourceLoader resourceLoader;

    @Value("${com.tripwhiz.firebase.config-path}")
    private String firebaseConfigPath;

    public FCMConfig(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Bean
    public FirebaseMessaging firebaseMessaging() throws IOException {
        Resource firebaseConfig = resolveFirebaseConfig(firebaseConfigPath);

        try (InputStream inputStream = firebaseConfig.getInputStream()) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(inputStream))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        }

        return FirebaseMessaging.getInstance();
    }

    private Resource resolveFirebaseConfig(String path) {
        if (path == null || path.isBlank()) {
            throw new IllegalStateException("TRIPWHIZ_FIREBASE_CONFIG_PATH environment variable must be set.");
        }

        if (path.startsWith("classpath:") || path.startsWith("file:")) {
            return resourceLoader.getResource(path);
        }

        return resourceLoader.getResource("file:" + path);
    }
}
