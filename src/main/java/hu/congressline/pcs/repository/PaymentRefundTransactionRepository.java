package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

import hu.congressline.pcs.domain.PaymentRefundTransaction;

public interface PaymentRefundTransactionRepository extends JpaRepository<PaymentRefundTransaction, Long> {

    List<PaymentRefundTransaction> findByPaymentTrxResultCode(String resultCode);

    List<PaymentRefundTransaction> findByTransactionId(String transactionId);

    List<PaymentRefundTransaction> findByTransactionIdIn(Set<String> transactionIds);
}
