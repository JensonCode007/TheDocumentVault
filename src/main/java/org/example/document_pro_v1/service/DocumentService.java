package org.example.document_pro_v1.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import org.example.document_pro_v1.Enums.DocumentStatus;
import org.example.document_pro_v1.entity.Document;
import org.example.document_pro_v1.entity.User;
import org.example.document_pro_v1.jwtSecurity.UserPrincipal;

import org.example.document_pro_v1.repository.DocumentRepository;
import org.example.document_pro_v1.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;


@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentService {


    private final OkHttpClient okHttpClient;
    private final DocumentRepository documentRepository;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

    public String documentIngestion(UserPrincipal userPrincipal, @RequestParam(name = "file") MultipartFile file) {

        String tenantSlug = userPrincipal.getTenantSlug();
        User user = userRepository.findByEmail(userPrincipal.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));




        try{
            RequestBody requestBody = RequestBody.create(
                    file.getBytes(),
                    okhttp3.MediaType.parse("application/pdf")
            );

            MultipartBody body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", file.getOriginalFilename(), requestBody)
                    .addFormDataPart("tenantSlug", tenantSlug)
                    .build();

            Request request = new Request.Builder()
                    .url("http://localhost:8000/ingest-pdf")
                    .post(body)
                    .build();

            try (Response response = okHttpClient.newCall(request).execute()) {
                log.info("Succesfully uploaded the file ✅");

                String rawJson = response.body().string();
                JsonNode rootNode = objectMapper.readTree(rawJson);

                int chunk_indexed = rootNode.path("chunk_indexed").asInt(0);

                Document document = new Document();
                document.setFileName(file.getOriginalFilename());
                document.setChunksIndexed(chunk_indexed);
                document.setStatus(DocumentStatus.INDEXED);
                document.setTenant(user.getTenant());
                document.setUploadedBy(user);
                documentRepository.save(document);

                return "FastAPI Response: " + response.body().string();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Failed: " + e.getMessage();
        }


    }
}
