package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import hu.congressline.pcs.domain.Congress;
import hu.congressline.pcs.domain.PaymentTransaction;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {

    List<PaymentTransaction> findAllByCongress(Congress congress);

    List<PaymentTransaction> findAllByPaymentTrxDateBetween(ZonedDateTime fromDate, ZonedDateTime toDate);

    List<PaymentTransaction> findAllByPaymentTrxDateBetweenAndTransactionId(ZonedDateTime fromDate, ZonedDateTime toDate, String transactionId);

    List<PaymentTransaction> findAllByPaymentTrxDateBetweenAndPaymentOrderNumber(ZonedDateTime fromDate, ZonedDateTime toDate, String orderNumber);

    List<PaymentTransaction> findAllByPaymentTrxDateBetweenAndTransactionIdAndPaymentOrderNumber(ZonedDateTime fromDate, ZonedDateTime toDate,
                                                                                                 String transactionId, String orderNumber);

    Optional<PaymentTransaction> findOneByTransactionId(String transactionId);

    List<PaymentTransaction> findByPaymentTrxStatus(String statusCode);
}
