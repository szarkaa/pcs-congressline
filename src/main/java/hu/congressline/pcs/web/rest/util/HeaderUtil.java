package hu.congressline.pcs.web.rest.util;

import hu.congressline.pcs.service.util.MessageUtil;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;

/**
 * Utility class for HTTP headers creation.
 */
public final class HeaderUtil {

    private static final Logger log = LoggerFactory.getLogger(HeaderUtil.class);

    private HeaderUtil(){
    }

    public static HttpHeaders createAlert(String message, String param) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-pcsApp-alert", message);
        headers.add("X-pcsApp-params", param);
        return headers;
    }

    public static HttpHeaders createEntityCreationAlert(String entityName, String param) {
        return createAlert("pcsApp." + entityName + ".created", param);
    }

    public static HttpHeaders createEntityUpdateAlert(String entityName, String param) {
        return createAlert("pcsApp." + entityName + ".updated", param);
    }

    public static HttpHeaders createEntityDeletionAlert(String entityName, String param) {
        return createAlert("pcsApp." + entityName + ".deleted", param);
    }

    public static HttpHeaders createFailureAlert(String entityName, String errorKey, String defaultMessage) {
        return createFailureAlert(entityName, errorKey, defaultMessage, true);
    }
    public static HttpHeaders createFailureAlert(String entityName, String errorKey, String defaultMessage, boolean translate) {
        log.error("Entity creation failed, {}", defaultMessage);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-pcsApp-error", "error." + errorKey);
        headers.add("X-pcsApp-params", entityName);
        if (translate) {
            headers.add("X-pcsApp-translate", "true");
        }
        return headers;
    }

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
            headers.add("X-pcsApp-constraint-violation", "error.constraintsviolationexists");
            headers.add("X-pcsApp-params", entityName);
        } else {
            headers.add("X-pcsApp-constraint-violation", "error.constraintsviolationdetailed");
            headers.add("X-pcsApp-params", className);
        }

        return headers;
    }

}
