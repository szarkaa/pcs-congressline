package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import hu.congressline.pcs.domain.ChargedServiceInvoiceHistory;
import hu.congressline.pcs.domain.Invoice;

public interface ChargedServiceInvoiceHistoryRepository extends JpaRepository<ChargedServiceInvoiceHistory, Long> {

    List<ChargedServiceInvoiceHistory> findAllByInvoiceInOrderById(List<Invoice> invoices);

    List<ChargedServiceInvoiceHistory> findAllByInvoice(Invoice invoice);

    List<ChargedServiceInvoiceHistory> findAllByChargedServiceIdOrderByIdDesc(Long id);
}
