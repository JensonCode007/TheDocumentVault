package org.example.document_pro_v1.config;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class OkHttpClientConfig {
    @Bean
    public OkHttpClient okHttpClient() {
        log.info("OkHttpClient is configured ✅");
        return new OkHttpClient.Builder()
                .connectTimeout(300, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(1200, java.util.concurrent.TimeUnit.SECONDS)  // 2 min for LLM
                .writeTimeout(300, java.util.concurrent.TimeUnit.SECONDS)
                .build();

    }
}
