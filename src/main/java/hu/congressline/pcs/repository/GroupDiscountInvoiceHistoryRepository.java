package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import hu.congressline.pcs.domain.GroupDiscountInvoiceHistory;
import hu.congressline.pcs.domain.Invoice;

public interface GroupDiscountInvoiceHistoryRepository extends JpaRepository<GroupDiscountInvoiceHistory, Long> {

    List<GroupDiscountInvoiceHistory> findAllByInvoice(Invoice invoice);

    List<GroupDiscountInvoiceHistory> findAllByInvoiceIn(List<Invoice> invoices);

    List<GroupDiscountInvoiceHistory> findAllByChargeableItemIdOrderByIdDesc(Long id);
}
