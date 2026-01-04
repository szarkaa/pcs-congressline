package hu.congressline.pcs.repository;

import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import hu.congressline.pcs.config.audit.AuditEventConverter;
import hu.congressline.pcs.domain.PersistentAuditEvent;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class CustomAuditEventRepository implements AuditEventRepository {

    private static final String AUTHORIZATION_FAILURE = "AUTHORIZATION_FAILURE";

    private static final String ANONYMOUS_USER = "anonymoususer";

    private final PersistenceAuditEventRepository persistenceAuditEventRepository;
    private final AuditEventConverter auditEventConverter;

    @Override
    public List<AuditEvent> find(String principal, Instant after, String type) {
        Iterable<PersistentAuditEvent> persistentAuditEvents =
            persistenceAuditEventRepository.findByPrincipalAndAuditEventDateAfterAndAuditEventType(principal, LocalDateTime.from(after), type);
        return auditEventConverter.convertToAuditEvent(persistentAuditEvents);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void add(AuditEvent event) {
        if (!AUTHORIZATION_FAILURE.equals(event.getType()) && !ANONYMOUS_USER.equals(event.getPrincipal())) {
            PersistentAuditEvent persistentAuditEvent = new PersistentAuditEvent();
            persistentAuditEvent.setPrincipal(event.getPrincipal());
            persistentAuditEvent.setAuditEventType(event.getType());
            persistentAuditEvent.setAuditEventDate(LocalDateTime.ofInstant(event.getTimestamp(), ZoneId.systemDefault()));
            persistentAuditEvent.setData(auditEventConverter.convertDataToStrings(event.getData()));
            //persistenceAuditEventRepository.save(persistentAuditEvent);
        }
    }
}
