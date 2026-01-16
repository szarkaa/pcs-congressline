package hu.congressline.pcs.service;

import org.springframework.context.MessageSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import hu.congressline.pcs.domain.Registration;
import hu.congressline.pcs.domain.User;
import hu.congressline.pcs.web.rest.vm.ConfirmationTitleType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tech.jhipster.config.JHipsterProperties;

import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailService {

    private static final String USER = "user";
    private static final String BASE_URL = "baseUrl";

    private final JHipsterProperties properties;
    private final JavaMailSender javaMailSender;
    private final MessageSource messageSource;
    private final SpringTemplateEngine templateEngine;

    @Async
    public void sendEmail(String from, String fromName, String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        sendEmailSync(from, fromName, to, null, subject, content, isMultipart, isHtml);
    }

    @Async
    public void sendEmailFromTemplate(User user, String templateName, String titleKey) {
        sendEmailFromTemplateSync(user, templateName, titleKey);
    }

    @SuppressWarnings({"MissingJavadocMethod", "ParameterNumber", "MultipleStringLiterals"})
    @Async
    public void sendConfirmationPdfEmail(String from, String to, String ccAddress, String fileName, ConfirmationTitleType titleType, Locale locale,
                                         Registration registration, byte[] pdfBytes) {
        log.debug("Send confirmation e-mail[ to '{}']", to);
        Map<String, Object> contextVariables = new HashMap<>();
        contextVariables.put("locale", locale);
        String confirmationTitle = messageSource.getMessage("confirmation.pdf.email.subject."
            + (ConfirmationTitleType.PRO_FORMA_INVOICE.equals(titleType) ? "proFormaInvoice" : "confirmation"), new Object[]{}, locale);
        contextVariables.put("confirmationTitle", confirmationTitle);
        contextVariables.put("title", registration.getTitle());
        contextVariables.put("name1", "hu".equals(locale.getLanguage()) ? registration.getLastName() : registration.getFirstName());
        contextVariables.put("name2", "hu".equals(locale.getLanguage()) ? registration.getFirstName() : registration.getLastName());
        String contentType = messageSource.getMessage("confirmation.pdf.email.content."
            + (ConfirmationTitleType.PRO_FORMA_INVOICE.equals(titleType) ? "proFormaInvoice" : "confirmation"), new Object[]{}, locale);
        contextVariables.put("contentType", contentType);
        String subject = messageSource.getMessage("confirmation.pdf.email.subject." + (ConfirmationTitleType.PRO_FORMA_INVOICE.equals(titleType)
            ? "proFormaInvoice" : "confirmation"), new Object[]{}, locale);
        MailAttachment mailAttachment = MailAttachment.builder().fileName(fileName).fileExtension("pdf").content(pdfBytes).mimeType("application/pdf").build();
        sendEmailFromTemplateSync(from, null, to, ccAddress, subject, "mail/confirmationEmail", locale, contextVariables, mailAttachment);
    }

    @SuppressWarnings("MultipleStringLiterals")
    @Async
    public void sendActivationEmail(User user) {
        log.debug("Sending activation email to '{}'", user.getEmail());
        sendEmailFromTemplateSync(user, "mail/activationEmail", "email.activation.title");
    }

    @SuppressWarnings("MultipleStringLiterals")
    @Async
    public void sendCreationEmail(User user) {
        log.debug("Sending creation email to '{}'", user.getEmail());
        sendEmailFromTemplateSync(user, "mail/creationEmail", "email.activation.title");
    }

    @Async
    public void sendPasswordResetMail(User user) {
        log.debug("Sending password reset email to '{}'", user.getEmail());
        sendEmailFromTemplateSync(user, "mail/passwordResetEmail", "email.reset.title");
    }

    private void sendEmailFromTemplateSync(User user, String templateName, String titleKey) {
        if (user.getEmail() == null) {
            log.debug("Email doesn't exist for user '{}'", user.getLogin());
            return;
        }
        Locale locale = Locale.forLanguageTag(user.getLangKey());
        Context context = new Context(locale);
        context.setVariable(USER, user);
        context.setVariable(BASE_URL, properties.getMail().getBaseUrl());
        String content = templateEngine.process(templateName, context);
        String subject = messageSource.getMessage(titleKey, null, locale);
        sendEmailSync(null, null, user.getEmail(), null, subject, content, false, true);
    }

    @SuppressWarnings("ParameterNumber")
    private void sendEmailFromTemplateSync(String from, String fromName, String to, String ccAddress, String subject, @NonNull String templateName,
                                           @NonNull Locale locale, @NonNull Map<String, Object> contextVariables, MailAttachment... attachments) {
        Context context = new Context(locale);
        context.setVariable(BASE_URL, properties.getMail().getBaseUrl());
        contextVariables.keySet().forEach(key -> context.setVariable(key, contextVariables.get(key)));
        String content = templateEngine.process(templateName, context);
        sendEmailSync(from, fromName, to, ccAddress, subject, content, true, true, attachments);
    }

    @SuppressWarnings("ParameterNumber")
    private void sendEmailSync(String from, String fromName, String to, String ccAddress, String subject, String content, boolean isMultipart,
                               boolean isHtml, MailAttachment... attachments) {
        log.debug("Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}", isMultipart, isHtml, to, subject, content);

        // Prepare message using a Spring helper
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            message.setFrom(nonNull(from) ? from : properties.getMail().getFrom(), nonNull(fromName) ? fromName : "Congressline PCS System");
            message.setTo(to);
            if (nonNull(ccAddress)) {
                message.setCc(ccAddress);
            }
            message.setSubject(subject);
            message.setText(content, isHtml);
            for (var attachment : attachments) {
                message.addAttachment(attachment.getFileName() + "." + attachment.getFileExtension(),
                    new ByteArrayDataSource(attachment.getContent(), attachment.getMimeType()));
            }
            javaMailSender.send(mimeMessage);
            log.debug("Sent email to user '{}'", to);
        } catch (MailException | MessagingException | IOException e) {
            log.warn("Email could not be sent to user '{}'", to, e);
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    private static class MailAttachment {
        private final String fileName;
        private final String fileExtension;
        private final String mimeType;
        private final byte[] content;
    }
}
