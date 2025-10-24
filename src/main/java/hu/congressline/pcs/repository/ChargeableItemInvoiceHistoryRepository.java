package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import hu.congressline.pcs.domain.ChargeableItemInvoiceHistory;
import hu.congressline.pcs.domain.Invoice;

public interface ChargeableItemInvoiceHistoryRepository extends JpaRepository<ChargeableItemInvoiceHistory, Long> {

    List<ChargeableItemInvoiceHistory> findAllByInvoice(Invoice invoice);

    List<ChargeableItemInvoiceHistory> findAllByInvoiceInOrderById(List<Invoice> invoices);

    List<ChargeableItemInvoiceHistory> findAllByChargeableItemIdOrderByIdDesc(Long id);
}
