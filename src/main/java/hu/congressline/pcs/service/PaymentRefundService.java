package hu.congressline.pcs.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import hu.congressline.pcs.domain.PaymentRefundTransaction;
import hu.congressline.pcs.domain.PaymentTransaction;
import hu.congressline.pcs.repository.PaymentRefundTransactionRepository;
import hu.congressline.pcs.service.dto.kh.PaymentRefundResult;
import hu.congressline.pcs.service.dto.kh.PaymentReverseResult;
import hu.congressline.pcs.service.dto.kh.PaymentStatus;
import hu.congressline.pcs.service.dto.kh.PaymentStatusResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static hu.congressline.pcs.service.dto.kh.PaymentStatus.PAYMENT_REFUND_PROCESSING;
import static hu.congressline.pcs.service.dto.kh.PaymentStatus.PAYMENT_RETURNED;
import static hu.congressline.pcs.service.dto.kh.PaymentStatus.PAYMENT_WAITING_FOR_SETTLEMENT;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class PaymentRefundService {
    private static final String BANK_AUTH_NUMBER = "bankAuthNumber";
    private final CompanyService companyService;
    //private final MailService mailService;
    private final PaymentTransactionService paymentTransactionService;
    private final PaymentRefundTransactionRepository repository;
    private final OnlinePaymentService paymentService;

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Optional<PaymentRefundTransaction> findById(Long id) {
        log.debug("Request to find PaymentRefundTransaction : {}", id);
        return repository.findById(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public PaymentRefundTransaction getById(Long id) {
        log.debug("Request to get PaymentRefundTransaction : {}", id);
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("PaymentRefundTransaction not found by id: " + id));
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void refundPayment(String transactionId, BigDecimal amount, String currency) {
        final PaymentTransaction paymentTransaction = paymentTransactionService.getByTransactionId(transactionId);
        if (PAYMENT_WAITING_FOR_SETTLEMENT.toString().equals(paymentTransaction.getPaymentTrxStatus())) {
            final PaymentReverseResult reverseResult = paymentService.sendPaymentReverseRequest(transactionId, currency);
            PaymentRefundTransaction refundTransaction = new PaymentRefundTransaction();
            refundTransaction.setTransactionId(transactionId);
            refundTransaction.setPaymentTrxDate(ZonedDateTime.now());
            refundTransaction.setPaymentTrxStatus(reverseResult.getPaymentStatus() != null ? PaymentStatus.getByCode(reverseResult.getPaymentStatus()).toString() : null);
            refundTransaction.setPaymentTrxResultCode(reverseResult.getResultCode() != null ? reverseResult.getResultCode().toString() : null);
            refundTransaction.setPaymentTrxResultMessage(reverseResult.getResultMessage());
            refundTransaction.setBankAuthNumber(BANK_AUTH_NUMBER);
            refundTransaction.setAmount(amount);
            refundTransaction.setCurrency(currency);
            repository.save(refundTransaction);
        } else {
            final PaymentRefundResult refundResult = paymentService.sendPaymentRefundRequest(transactionId, amount, currency);
            PaymentRefundTransaction refundTransaction = new PaymentRefundTransaction();
            refundTransaction.setTransactionId(transactionId);
            refundTransaction.setPaymentTrxDate(ZonedDateTime.now());
            refundTransaction.setPaymentTrxStatus(refundResult.getPaymentStatus() != null ? PaymentStatus.getByCode(refundResult.getPaymentStatus()).toString() : null);
            refundTransaction.setPaymentTrxResultCode(refundResult.getResultCode() != null ? refundResult.getResultCode().toString() : null);
            refundTransaction.setPaymentTrxResultMessage(refundResult.getResultMessage());
            refundTransaction.setPaymentTrxAuthCode(refundResult.getAuthCode());
            refundTransaction.setBankAuthNumber(BANK_AUTH_NUMBER);
            refundTransaction.setAmount(amount);
            refundTransaction.setCurrency(currency);
            repository.save(refundTransaction);
        }
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void checkPendingRefundResults() {
        List<PaymentRefundTransaction> refundList = repository.findByPaymentTrxResultCode(PAYMENT_REFUND_PROCESSING.toString());
        refundList.forEach(refundTransaction -> {
            String currency = refundTransaction.getCurrency();
            final PaymentStatusResult statusResult = paymentService.sendPaymentStatusRequest(refundTransaction.getTransactionId(), currency);
            if (PAYMENT_RETURNED.toString().equals(PaymentStatus.getByCode(statusResult.getPaymentStatus()).toString())) {
                refundTransaction.setPaymentTrxResultCode(PaymentStatus.getByCode(statusResult.getPaymentStatus()).toString());
                refundTransaction.setPaymentTrxResultMessage(statusResult.getResultMessage());
                refundTransaction.setPaymentTrxAuthCode(statusResult.getAuthCode());
                refundTransaction.setBankAuthNumber(BANK_AUTH_NUMBER);
                PaymentRefundTransaction result = repository.save(refundTransaction);

                final PaymentTransaction paymentTransaction = paymentTransactionService.getByTransactionId(result.getTransactionId());
                //mailService.sendOnlinePaymentRefundNotificationEmail(paymentTransaction.getEmail(), paymentTransaction, refundTransaction,
                //companyService.getCompanyProfile(), new Locale(Currency.HUF.toString().equalsIgnoreCase(currency) ? "hu" : "en"));
            }
        });
    }

}
