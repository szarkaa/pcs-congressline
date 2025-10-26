package hu.congressline.pcs.service;

import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import hu.congressline.pcs.config.audit.AuditEventConverter;
import hu.congressline.pcs.repository.PersistenceAuditEventRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class AuditEventService {

    private final PersistenceAuditEventRepository persistenceAuditEventRepository;
    private final AuditEventConverter auditEventConverter;

    public Page<AuditEvent> findAll(Pageable pageable) {
        return persistenceAuditEventRepository.findAll(pageable)
            .map(auditEventConverter::convertToAuditEvent);
    }

    public Page<AuditEvent> findByDates(LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable) {
        return persistenceAuditEventRepository.findAllByAuditEventDateBetween(fromDate, toDate, pageable)
            .map(auditEventConverter::convertToAuditEvent);
    }

    public Optional<AuditEvent> find(Long id) {
        return persistenceAuditEventRepository.findById(id).map(auditEventConverter::convertToAuditEvent);
    }
}
