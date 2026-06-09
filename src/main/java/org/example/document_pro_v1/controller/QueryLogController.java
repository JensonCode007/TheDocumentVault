package org.example.document_pro_v1.controller;


import lombok.RequiredArgsConstructor;
import org.example.document_pro_v1.dto.QueryLogResponse;
import org.example.document_pro_v1.entity.Tenant;
import org.example.document_pro_v1.jwtSecurity.UserPrincipal;
import org.example.document_pro_v1.service.QueryLogService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/query-log")
public class QueryLogController {

    private final QueryLogService queryLogService;

    @GetMapping("/user")
    public ResponseEntity<Page<QueryLogResponse>> getQueryLogOfUserByUserName(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "queriedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir)
    {
        if(page < 0) page = 0;
        if(size >50) size = 50;

        Page<QueryLogResponse> response = queryLogService.getQueryLogByUser(userPrincipal, page, size, sortBy, sortDir);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/tenant")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Page<QueryLogResponse>> getQueryLogOfUserByTenantName(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir)
    {
        if(page < 0) page = 0;
        if(size >50) size = 50;

        Page<QueryLogResponse> response = queryLogService.getQueryLogByTenant(userPrincipal, page, size, sortBy, sortDir);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }


}
