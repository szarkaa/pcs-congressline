package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import hu.congressline.pcs.domain.Invoice;
import hu.congressline.pcs.domain.InvoiceItem;

public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {

    List<InvoiceItem> findAllByInvoice(Invoice invoice);

    List<InvoiceItem> findAllByInvoiceIdIn(List<Long> invoiceIds);

    List<InvoiceItem> findAllByInvoiceIn(List<Invoice> invoices);
}
