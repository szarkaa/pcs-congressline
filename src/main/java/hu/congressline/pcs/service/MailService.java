package hu.congressline.pcs.service;

import org.springframework.context.MessageSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import hu.congressline.pcs.config.PcsProperties;
import hu.congressline.pcs.domain.Company;
import hu.congressline.pcs.domain.Invoice;
import hu.congressline.pcs.domain.OnlineRegistration;
import hu.congressline.pcs.domain.PayingGroup;
import hu.congressline.pcs.domain.PaymentRefundTransaction;
import hu.congressline.pcs.domain.PaymentTransaction;
import hu.congressline.pcs.domain.Registration;
import hu.congressline.pcs.domain.User;
import hu.congressline.pcs.service.dto.SendAllConfirmationPdfToEmailDTO;
import hu.congressline.pcs.service.dto.kh.PaymentStatus;
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

import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailService {

    private static final String USER = "user";
    private static final String BASE_URL = "baseUrl";

    private final PcsProperties properties;
    private final JavaMailSender javaMailSender;
    private final MessageSource messageSource;
    private final SpringTemplateEngine templateEngine;

    @SuppressWarnings("ParameterNumber")
    @Async
    public void sendEmail(String from, String fromName, String to, String cc, String subject, String content, boolean isMultipart, boolean isHtml) {
        sendEmailSync(from, fromName, to, cc, null, subject, content, isMultipart, isHtml);
    }

    @Async
    public void sendEmailFromTemplate(User user, String templateName, String titleKey) {
        sendEmailFromTemplateSync(user, templateName, titleKey);
    }

    @SuppressWarnings({"MissingJavadocMethod", "ParameterNumber", "MultipleStringLiterals"})
    @Async
    public void sendConfirmationPdfEmail(String from, String to, String cc, String fileName, ConfirmationTitleType titleType, Locale locale,
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
        sendEmailFromTemplateSync(from, null, to, cc, null, subject, "mail/confirmationEmail", locale, contextVariables, mailAttachment);
    }

    @SuppressWarnings({"MissingJavadocMethod"})
    @Async
    public void sendAllConfirmationPdfToEmail(String from, String to, Locale locale, List<SendAllConfirmationPdfToEmailDTO> pdfList) {
        log.debug("Send all confirmations to e-mail[ to '{}']", to);
        Map<String, Object> contextVariables = new HashMap<>();
        contextVariables.put("locale", locale);
        String subject = messageSource.getMessage("confirmation.all.pdf.email.subject", new Object[]{}, locale);
        contextVariables.put("confirmationTitle", subject);
        sendEmailFromTemplateSync(from, null, to, null, null, subject, "mail/confirmationAllEmail", locale, contextVariables,
            pdfList.stream().map(dto -> {
                return MailAttachment.builder()
                    .fileName("confirmation-reg-id-" + dto.getRegId())
                    .fileExtension("pdf")
                    .content(dto.getPdfBytes()).mimeType("application/pdf").build();
            }).toList().toArray(new MailAttachment[0]));
    }

    @SuppressWarnings({"MissingJavadocMethod"})
    @Async
    public void sendInvoicePdfEmail(String from, String to, String fileName, Locale locale, Registration registration, byte[] pdfBytes) {
        log.debug("Send invoice e-mail[ to '{}']", to);
        Map<String, Object> contextVariables = new HashMap<>();
        contextVariables.put("locale", locale);
        contextVariables.put("title", registration.getTitle());
        contextVariables.put("name1", "hu".equals(locale.getLanguage()) ? registration.getLastName() : registration.getFirstName());
        contextVariables.put("name2", "hu".equals(locale.getLanguage()) ? registration.getFirstName() : registration.getLastName());

        String subject = messageSource.getMessage("invoice.pdf.email.subject", new Object[]{}, locale);
        MailAttachment mailAttachment = MailAttachment.builder().fileName(fileName).fileExtension("pdf").content(pdfBytes).mimeType("application/pdf").build();
        sendEmailFromTemplateSync(from, null, to, from, null, subject, "mail/invoiceEmail", locale, contextVariables, mailAttachment);
    }

    @SuppressWarnings({"MissingJavadocMethod", "MultipleStringLiterals"})
    @Async
    public void sendGroupDiscountInvoicePdfEmail(String from, String to, PayingGroup payingGroup, Locale locale, byte[] pdfBytes) {
        log.debug("Send group discount invoice e-mail[ to '{}']", to);
        Map<String, Object> contextVariables = new HashMap<>();
        contextVariables.put("locale", locale);
        contextVariables.put("name", payingGroup.getName());
        String subject = messageSource.getMessage("group.discount.invoice.pdf.email.subject", new Object[]{}, locale);
        MailAttachment mailAttachment = MailAttachment.builder().fileName("invoice").fileExtension("pdf").content(pdfBytes).mimeType("application/pdf").build();
        sendEmailFromTemplateSync(from, null, to, from, null, subject, "mail/groupDiscountInvoiceEmail", locale, contextVariables, mailAttachment);
    }

    @SuppressWarnings({"MissingJavadocMethod", "MultipleStringLiterals"})
    @Async
    public void sendMiscInvoicePdfEmail(String from, String to, Invoice invoice, Locale locale, byte[] pdfBytes) {
        log.debug("Send misc invoice e-mail[ to '{}']", to);
        Map<String, Object> contextVariables = new HashMap<>();
        contextVariables.put("locale", locale);
        contextVariables.put("name", invoice.getName1());
        String subject = messageSource.getMessage("misc.invoice.pdf.email.subject", new Object[]{}, locale);
        MailAttachment mailAttachment = MailAttachment.builder().fileName("invoice").fileExtension("pdf").content(pdfBytes).mimeType("application/pdf").build();
        sendEmailFromTemplateSync(from, null, to, from, null, subject, "mail/miscInvoiceEmail", locale, contextVariables, mailAttachment);
    }

    @SuppressWarnings({"MissingJavadocMethod", "MultipleStringLiterals"})
    @Async
    public void sendOnlineRegNotificationEmail(String to, String cc, String congressName, Locale locale) {
        log.debug("Send online reg notification e-mail[ to '{}']", to);
        Map<String, Object> contextVariables = new HashMap<>();
        contextVariables.put("locale", locale);
        contextVariables.put("congressName", congressName);
        String subject = messageSource.getMessage("online.reg.notification.email.subject", new Object[]{congressName}, locale);
        sendEmailFromTemplateSync(null, null, to, cc, null, subject, "mail/onlineRegNotificationEmail", locale, contextVariables);
    }

    @SuppressWarnings({"MissingJavadocMethod", "MultipleStringLiterals"})
    @Async
    public void sendOnlinePaymentRefundNotificationEmail(String to, PaymentTransaction trx, PaymentRefundTransaction refundTrx, Company company, Locale locale) {
        log.debug("Send online reg refund notification e-mail[ to '{}']", to);
        Map<String, Object> contextVariables = new HashMap<>();
        String title = StringUtils.hasText(trx.getTitle()) ? trx.getTitle() : "";
        String lastName = StringUtils.hasText(trx.getLastName()) ? trx.getLastName() : "";
        String firstName = StringUtils.hasText(trx.getFirstName()) ? trx.getFirstName() : "";
        String name = "hu".equals(locale.getLanguage()) ? lastName + (StringUtils.hasText(lastName) ? " " : "") + firstName
            : firstName + (StringUtils.hasText(firstName) ? " " : "") + lastName;
        String paymentStatusText = messageSource.getMessage("online.refund.notification.email.successful", new Object[] {}, locale);
        String paymentStatusMessage = messageSource.getMessage("online.refund.notification.email.status", new Object[]{}, locale);
        String meetingCode = trx.getCongress().getMeetingCode();

        contextVariables.put("locale", locale);
        contextVariables.put("title", title);
        contextVariables.put("lastName", lastName);
        contextVariables.put("firstName", firstName);
        contextVariables.put("name", name);
        contextVariables.put("nameWithTitle", (StringUtils.hasText(title) ? title + " " : "") + name);
        contextVariables.put("paymentStatusMessage", paymentStatusMessage);
        contextVariables.put("paymentStatusText", paymentStatusText.toUpperCase());
        contextVariables.put("paymentTrxStatus", refundTrx.getPaymentTrxStatus() + " " + paymentStatusText);
        contextVariables.put("paymentTrxResponse", (StringUtils.hasText(refundTrx.getPaymentTrxAuthCode()) ? refundTrx.getPaymentTrxAuthCode() : "") + " " + paymentStatusMessage);
        contextVariables.put("paymentTrxId", refundTrx.getTransactionId());
        contextVariables.put("paymentTrxDate", refundTrx.getPaymentTrxDate().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        contextVariables.put("amount", refundTrx.getAmount());
        contextVariables.put("currency", refundTrx.getCurrency().toUpperCase());
        contextVariables.put("meetingCode", meetingCode);
        contextVariables.put("companyName", company.getName());
        contextVariables.put("companyWebsite", StringUtils.hasText(trx.getCongress().getWebsite()) ? trx.getCongress().getWebsite() : "");
        contextVariables.put("companyEmail", StringUtils.hasText(trx.getCongress().getContactEmail()) ? trx.getCongress().getContactEmail() : "");

        String subject = messageSource.getMessage("online.refund.notification.email.subject", new Object[]{paymentStatusText, trx.getCongress().getMeetingCode()}, locale);
        sendEmailFromTemplateSync(null, null, to, null, trx.getCongress().getContactEmail() != null ? trx.getCongress().getContactEmail() : null,
            subject, "mail/onlineRefundNotificationEmail", locale, contextVariables);
    }

    @SuppressWarnings({"MissingJavadocMethod", "MultipleStringLiterals"})
    @Async
    public void sendOnlinePaymentNotificationEmail(String to, OnlineRegistration onlineReg, BigDecimal total, String currency, Company company, Locale locale) {
        log.debug("Send online reg payment notification e-mail[ to '{}']", to);
        String title = StringUtils.hasText(onlineReg.getTitle()) ? onlineReg.getTitle() : "";
        String lastName = StringUtils.hasText(onlineReg.getLastName()) ? onlineReg.getLastName() : "";
        String firstName = StringUtils.hasText(onlineReg.getFirstName()) ? onlineReg.getFirstName() : "";
        String name = "hu".equals(locale.getLanguage()) ? lastName + (StringUtils.hasText(lastName) ? " " : "") + firstName
            : firstName + (StringUtils.hasText(firstName) ? " " : "") + lastName;
        String nameWithTitle = (StringUtils.hasText(title) ? title + " " : "") + name;
        String paymentStatusText = messageSource.getMessage("online.payment.notification.email."
            + (PaymentStatus.PAYMENT_WAITING_FOR_SETTLEMENT.toString().equals(onlineReg.getPaymentTrxStatus()) ? "" : "un") + "successful", new Object[] {}, locale);
        String mailStatusCode = onlineReg.getPaymentTrxStatus() + (PaymentStatus.PAYMENT_DENIED.toString().equals(onlineReg.getPaymentTrxStatus())
            ? ("130".equals(onlineReg.getPaymentTrxResultCode()) ? "_SESSION_EXPIRED" : "_ERROR") : "");
        String paymentStatusMessage = messageSource.getMessage("online.payment.notification.email.status." + mailStatusCode, new Object[]{}, locale);

        String meetingCode = onlineReg.getCongress().getMeetingCode();
        String subject = messageSource.getMessage("online.payment.notification.email.subject", new Object[]{paymentStatusText, meetingCode}, locale);
        Map<String, Object> contextVariables = new HashMap<>();
        contextVariables.put("locale", locale);
        contextVariables.put("subject", subject);
        contextVariables.put("meetingCode", meetingCode);
        contextVariables.put("name", name);
        contextVariables.put("email", onlineReg.getEmail());
        contextVariables.put("paymentStatusText", paymentStatusText.toUpperCase());
        contextVariables.put("paymentTrxStatus", onlineReg.getPaymentTrxStatus() + " " + paymentStatusText);
        contextVariables.put("paymentTrxResponse", (StringUtils.hasText(onlineReg.getPaymentTrxAuthCode()) ? onlineReg.getPaymentTrxAuthCode() : "") + " " + paymentStatusMessage);
        contextVariables.put("paymentTrxId", onlineReg.getPaymentTrxId());
        contextVariables.put("paymentTrxDate", onlineReg.getPaymentTrxDate().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        contextVariables.put("total", total);
        contextVariables.put("currency", currency.toUpperCase());
        contextVariables.put("companyName", company.getName());
        contextVariables.put("companyWebsite", StringUtils.hasText(onlineReg.getCongress().getWebsite()) ? onlineReg.getCongress().getWebsite() : "");
        contextVariables.put("mailStatusCode", mailStatusCode);
        contextVariables.put("nameWithTitle", nameWithTitle);
        sendEmailFromTemplateSync(null, null, to, null, null, subject, "mail/onlinePaymentNotificationEmail", locale, contextVariables);
        log.debug("Send online payment notification e-mail to '{}'", to);
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
        sendEmailSync(null, null, user.getEmail(), null, null, subject, content, false, true);
    }

    @SuppressWarnings("ParameterNumber")
    private void sendEmailFromTemplateSync(String from, String fromName, String to, String cc, String bcc, String subject, @NonNull String templateName,
                                           @NonNull Locale locale, @NonNull Map<String, Object> contextVariables, MailAttachment... attachments) {
        Context context = new Context(locale);
        context.setVariable(BASE_URL, properties.getMail().getBaseUrl());
        contextVariables.keySet().forEach(key -> context.setVariable(key, contextVariables.get(key)));
        String content = templateEngine.process(templateName, context);
        sendEmailSync(from, fromName, to, cc, bcc, subject, content, true, true, attachments);
    }

    @SuppressWarnings("ParameterNumber")
    private void sendEmailSync(String from, String fromName, String to, String cc, String bcc, String subject, String content, boolean isMultipart,
                               boolean isHtml, MailAttachment... attachments) {
        log.debug("Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}", isMultipart, isHtml, to, subject, content);

        // Prepare message using a Spring helper
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            message.setFrom(nonNull(from) ? from : properties.getMail().getFrom(), nonNull(fromName) ? fromName : "Congressline PCS System");
            message.setTo(to);
            if (nonNull(cc)) {
                message.setCc(cc);
            }
            if (nonNull(bcc)) {
                message.setBcc(bcc);
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
