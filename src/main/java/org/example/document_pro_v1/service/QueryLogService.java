package org.example.document_pro_v1.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.document_pro_v1.dto.QueryLogResponse;
import org.example.document_pro_v1.entity.QueryLog;
import org.example.document_pro_v1.entity.Tenant;
import org.example.document_pro_v1.entity.User;
import org.example.document_pro_v1.jwtSecurity.UserPrincipal;
import org.example.document_pro_v1.repository.QueryLogRepository;
import org.example.document_pro_v1.repository.TenantRepository;
import org.example.document_pro_v1.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;



@Service
@Slf4j
@RequiredArgsConstructor
public class QueryLogService {
    private final QueryLogRepository queryLogRepository;
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;

    public Page<QueryLogResponse> getQueryLogByUser(UserPrincipal userPrincipal
            , int page, int size, String sortBy, String sortDir
    ) {

        User user = userRepository.findByEmail(userPrincipal.getEmail())
                .orElseThrow(()-> new UsernameNotFoundException(userPrincipal.getEmail()));
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<QueryLog> queryLogs = queryLogRepository.findByUser(user, pageable);

        return queryLogs.map(log -> new QueryLogResponse(
                log.getId(),
                log.getQueryText(),
                log.getResponseResults(),
                log.getResponseTimeMS(),
                log.getQueriedAt(),
                log.getUser().getEmail()
        ));



    }

    public Page<QueryLogResponse> getQueryLogByTenant(UserPrincipal userPrincipal, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Tenant tenant = tenantRepository.findBySlug(userPrincipal.getTenantSlug())
                .orElseThrow(()-> new RuntimeException("Tenant Not Found"));

        Page<QueryLog> queryLogs = queryLogRepository.findByTenant(tenant, pageable);

        return queryLogs.map(log -> new QueryLogResponse(
                log.getId(),
                log.getQueryText(),
                log.getResponseResults(),
                log.getResponseTimeMS(),
                log.getQueriedAt(),
                log.getUser().getEmail()
        ));

    }
}
