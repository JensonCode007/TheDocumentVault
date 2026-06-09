package org.example.document_pro_v1.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.example.document_pro_v1.dto.SearchResponse;
import org.example.document_pro_v1.entity.QueryLog;
import org.example.document_pro_v1.entity.Tenant;
import org.example.document_pro_v1.entity.User;
import org.example.document_pro_v1.jwtSecurity.JwtTokenProvider;
import org.example.document_pro_v1.repository.QueryLogRepository;
import org.example.document_pro_v1.repository.TenantRepository;
import org.example.document_pro_v1.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {
    private final JwtTokenProvider jwtTokenProvider;
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    public SearchResponse querySearch(String query, String authHeader) {
        String jwtToken = authHeader;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtToken = authHeader.substring(7);
        }

        String tenantSlug = jwtTokenProvider.getTenantSlugFromToken(jwtToken);
        Tenant tenant = tenantRepository.findBySlug(tenantSlug)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
        User user = userRepository.findByEmail(jwtTokenProvider.getUserEmailFromToken(jwtToken))
                .orElseThrow(() -> new RuntimeException("User not found"));

        try{
            String jsonBody = objectMapper.writeValueAsString(Map.of("query", query, "tenantSlug", tenantSlug));
            RequestBody requestBody = RequestBody.create(
                    jsonBody,
                    okhttp3.MediaType.parse("application/json")

            );
            Request request = new Request.Builder()
                    .url("http://localhost:8000/search")
                    .post(requestBody)
                    .build();
            long startTime = System.currentTimeMillis();
            try (Response response = okHttpClient.newCall(request).execute()) {
                long endTime = System.currentTimeMillis();
                long latencyMS = endTime - startTime;
                if (!response.isSuccessful()) {
                    log.error("AI Server failed with code {}: {}", response.code(), response.body().string());
                    throw new RuntimeException("Failed to get response from AI server.");
                }

                String responseBody = response.body().string();

                JsonNode rootNode = objectMapper.readTree(responseBody);

                String response_results = rootNode.path("results").toString();

                QueryLog queryLog = new QueryLog();
                queryLog.setQueryText(query);
                queryLog.setResponseResults(response_results);
                queryLog.setUser(user);
                queryLog.setTenant(tenant);
                queryLog.setResponseTimeMS(String.valueOf(latencyMS));
                auditLogService.logService(queryLog);


                log.info("Successfully Returned the text✅");
                return  objectMapper.readValue(responseBody, SearchResponse.class);
            }


        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }




    }
}
