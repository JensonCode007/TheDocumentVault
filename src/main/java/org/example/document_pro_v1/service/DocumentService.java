package org.example.document_pro_v1.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import org.example.document_pro_v1.Enums.DocumentStatus;
import org.example.document_pro_v1.dto.DocumentResponse;
import org.example.document_pro_v1.entity.Document;
import org.example.document_pro_v1.entity.Tenant;
import org.example.document_pro_v1.entity.User;
import org.example.document_pro_v1.jwtSecurity.UserPrincipal;

import org.example.document_pro_v1.repository.DocumentRepository;
import org.example.document_pro_v1.repository.TenantRepository;
import org.example.document_pro_v1.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;


@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentService {


    private final OkHttpClient okHttpClient;
    private final DocumentRepository documentRepository;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;

    public String calculateCheckSum(MultipartFile file) throws IOException {

        return DigestUtils.md5DigestAsHex(file.getInputStream());
    }

    public String documentIngestion(UserPrincipal userPrincipal, MultipartFile file) throws IOException {




        String tenantSlug = userPrincipal.getTenantSlug();
        User user = userRepository.findByEmail(userPrincipal.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Tenant tenant = tenantRepository.findBySlug(tenantSlug)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));




        try{
            String filehash = calculateCheckSum(file);
            if (documentRepository.existsByTenantAndFileHash(tenant, filehash)) {
                log.warn("Duplicate file upload attempt blocked for hash: {}", filehash);
                return "Error: This document has already been uploaded and indexed.";
            }

            Document document = new Document();
            document.setFileName(file.getOriginalFilename());
            document.setStatus(DocumentStatus.PROCESSING);
            document.setTenant(tenant);
            document.setUploadedBy(user);
            document.setFileHash(filehash);
            documentRepository.save(document);
            RequestBody requestBody = RequestBody.create(
                    file.getBytes(),
                    okhttp3.MediaType.parse("application/pdf")
            );

            MultipartBody body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", file.getOriginalFilename(), requestBody)
                    .addFormDataPart("tenantSlug", tenantSlug)
                    .addFormDataPart("documentId", filehash)
                    .build();

            Request request = new Request.Builder()
                    .url("http://localhost:8000/ingest-pdf")
                    .post(body)
                    .build();

            try (Response response = okHttpClient.newCall(request).execute()) {
                log.info("Succesfully uploaded the file ✅");

                if(!response.isSuccessful()){
                    log.info(response.message());
                    document.setStatus(DocumentStatus.FAILED);
                    documentRepository.save(document);
                }
                String rawJson = response.body().string();
                JsonNode rootNode = objectMapper.readTree(rawJson);

                int chunk_indexed = rootNode.path("chunk_indexed").asInt(0);



                document.setChunksIndexed(chunk_indexed);
                document.setStatus(DocumentStatus.INDEXED);
                documentRepository.save(document);

                return "FastAPI Response: " + rawJson;
            }

        } catch (Exception e) {
            log.error("Ingestion failed", e);
            return "Failed: " + e.getMessage();
        }


    }

    public DocumentResponse getDocumentById(Long id) {

        Document document = documentRepository.findById(id)
                .orElseThrow(()-> {
                    log.info("Document Not Found");
                    return new RuntimeException("Document Not Found");
                });

        return new DocumentResponse(
                document.getId(),
                document.getFileName(),
                document.getUploadedBy().getEmail(),
                document.getUploadedAt(),
                document.getTenant().getSlug()
        );
    }

    public Page<DocumentResponse> getAllDocuments(UserPrincipal userPrincipal,int page, int size, String sortBy, String sortDir) {
        Tenant tenant = tenantRepository.findBySlug(userPrincipal.getTenantSlug())
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Document> documents = documentRepository.findByTenant(tenant, pageable);

        return documents.map(doc-> new DocumentResponse(
                doc.getId(),
                doc.getFileName(),
                doc.getUploadedBy().getEmail(),
                doc.getUploadedAt(),
                doc.getTenant().getSlug()
                )

        );

    }


    public String deleteDocumentById(UserPrincipal userPrincipal, Long id) {
        Document document = documentRepository.findById(id).orElseThrow(()-> {
            log.info("There is no document with id: {}", id);
            return new RuntimeException("Document Not Found");
        });
        String tenantSlug = userPrincipal.getTenantSlug();

        if(!document.getTenant().getSlug().equals(tenantSlug)){
            throw new AccessDeniedException("Access Denied");
        }
        HttpUrl url = HttpUrl.parse("http://localhost:8000/delete/"+document.getFileHash())
                .newBuilder()
                .addQueryParameter("tenantSlug", tenantSlug)
                .build();

        Request request = new Request.Builder().url(url).delete().build();

        try(Response response = okHttpClient.newCall(request).execute()) {
            if(!response.isSuccessful()){
                log.warn("Failed to delete vectors from AI server. Status: {}", response.code());
            }
            else {
                log.info("Successfully deleted vectors from ChromaDB");
            }

        }
        catch (Exception e){
            log.error("Network error while contacting AI server for deletion", e);
            throw new RuntimeException("Could not contact AI server to delete document vectors.");
        }
            documentRepository.delete(document);
            return "Document Successfully deleted";

        }



}



