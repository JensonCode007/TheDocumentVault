package org.example.document_pro_v1.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.document_pro_v1.jwtSecurity.UserPrincipal;
import org.example.document_pro_v1.service.DocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
@RestController
@Slf4j
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/document-ingest")
    public ResponseEntity<String> documentIngestion(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestParam(name = "file")  MultipartFile file) {
        log.info("Document ingestion started");
        if(file.isEmpty()){
            log.info("file is empty");
            return ResponseEntity.badRequest().build();
        }

        return new ResponseEntity<>(documentService.documentIngestion(userPrincipal,file), HttpStatus.ACCEPTED);
    }

}
