package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import hu.congressline.pcs.domain.Invoice;
import hu.congressline.pcs.domain.MiscInvoiceItem;

public interface MiscInvoiceItemRepository extends JpaRepository<MiscInvoiceItem, Long> {

    List<MiscInvoiceItem> findAllByInvoice(Invoice invoice);

    List<MiscInvoiceItem> findAllByInvoiceIn(List<Invoice> invoices);
}
