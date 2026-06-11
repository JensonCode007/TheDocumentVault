package org.example.document_pro_v1.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.document_pro_v1.dto.DocumentResponse;
import org.example.document_pro_v1.jwtSecurity.UserPrincipal;
import org.example.document_pro_v1.service.DocumentService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
@RestController
@Slf4j
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/document-ingest")
    public ResponseEntity<String> documentIngestion(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestParam(name = "file")  MultipartFile file) throws IOException {
        log.info("Document ingestion started");
        if(file.isEmpty()){
            log.info("file is empty");
            return ResponseEntity.badRequest().build();
        }

        return new ResponseEntity<>(documentService.documentIngestion(userPrincipal,file), HttpStatus.ACCEPTED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponse> getDocument(@PathVariable Long id) {
        log.info("Get Document with id {} request received", id);
        return new ResponseEntity<>(documentService.getDocumentById(id), HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<Page<DocumentResponse>> getAllDocuments(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir
    )
    {
        if(page < 0) page = 0;
        if(size >50) size = 50;
        log.info("Get All Documents request received");
        Page<DocumentResponse> responsePage = documentService.getAllDocuments(userPrincipal, page, size, sortBy, sortDir);
        return new ResponseEntity<>(responsePage, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDocumentById(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id
    )
    {
        log.info("Delete Document with id {} request received", id);
        return new ResponseEntity<>(documentService.deleteDocumentById(userPrincipal, id), HttpStatus.OK);

    }

}
