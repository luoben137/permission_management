package com.permission.service;

import com.permission.entity.AuditLog;
import com.permission.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Transactional
    public void log(Long userId, String username, String action, String resourceType,
                   Long resourceId, String details, String ip) {
        AuditLog log = new AuditLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setAction(action);
        log.setResourceType(resourceType);
        log.setResourceId(resourceId);
        log.setDetails(details);
        log.setIp(ip);
        auditLogRepository.save(log);
    }

    public Page<AuditLog> findByFilters(Long userId, String action,
                                        LocalDateTime startDate, LocalDateTime endDate,
                                        Pageable pageable) {
        return auditLogRepository.findByFilters(userId, action, startDate, endDate, pageable);
    }
}

