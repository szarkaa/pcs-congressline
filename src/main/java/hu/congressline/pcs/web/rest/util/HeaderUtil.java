package hu.congressline.pcs.web.rest.util;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;

import hu.congressline.pcs.service.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class HeaderUtil {

    private static final String PCS_APP = "pcsApp.";
    private static final String X_ALERT = "X-pcsApp-alert";
    private static final String X_PARAM = "X-pcsApp-params";
    private static final String X_CONSTRAINT_VIOLATION = "X-pcsApp-constraint-violation";

    private HeaderUtil() {
    }

    @SuppressWarnings("MissingJavadocMethod")
    public static HttpHeaders createAlert(String message, String param) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(X_ALERT, message);
        headers.add(X_PARAM, param);
        return headers;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public static HttpHeaders createEntityCreationAlert(String entityName, String param) {
        return createAlert(PCS_APP + entityName + ".created", param);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public static HttpHeaders createEntityUpdateAlert(String entityName, String param) {
        return createAlert(PCS_APP + entityName + ".updated", param);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public static HttpHeaders createEntityDeletionAlert(String entityName, String param) {
        return createAlert(PCS_APP + entityName + ".deleted", param);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public static HttpHeaders createFailureAlert(String entityName, String errorKey, String defaultMessage) {
        return createFailureAlert(entityName, errorKey, defaultMessage, true);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public static HttpHeaders createFailureAlert(String entityName, String errorKey, String defaultMessage, boolean translate) {
        log.error("Entity creation failed, {}", defaultMessage);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-pcsApp-error", "error." + errorKey);
        headers.add(X_PARAM, entityName);
        if (translate) {
            headers.add("X-pcsApp-translate", "true");
        }
        return headers;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public static HttpHeaders createDeleteConstraintViolationAlert(String entityName, DataIntegrityViolationException e) {
        String className = null;
        String referenceName = null;
        if (e.getCause() instanceof ConstraintViolationException) {
            if (e.getCause().getCause() instanceof DataIntegrityViolationException) {
                String errorMessage = e.getCause().getCause().getMessage();
                className = MessageUtil.parseDeleteConstraintViolationClassMessage(errorMessage);
                referenceName = MessageUtil.parseDeleteConstraintViolationReferenceMessage(errorMessage);
            }
        }

        HttpHeaders headers = new HttpHeaders();
        if (className == null || referenceName == null) {
            headers.add(X_CONSTRAINT_VIOLATION, "error.constraintsviolationexists");
            headers.add(X_PARAM, entityName);
        } else {
            headers.add(X_CONSTRAINT_VIOLATION, "error.constraintsviolationdetailed");
            headers.add(X_PARAM, className);
        }

        return headers;
    }

}
