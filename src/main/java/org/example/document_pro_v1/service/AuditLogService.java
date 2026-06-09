package org.example.document_pro_v1.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.document_pro_v1.entity.QueryLog;
import org.example.document_pro_v1.repository.QueryLogRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogService {
    private final QueryLogRepository queryLogRepository;

    @Async
    public void logService(QueryLog queryLog) {
        try{
            queryLogRepository.save(queryLog);
            log.info("QueryLog saved with id {}", queryLog.getId());
        }
        catch(Exception e){
            log.error("QueryLog save failed with exception {}", e.getMessage());
        }
    }
}
