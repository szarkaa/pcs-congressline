package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import hu.congressline.pcs.domain.Invoice;
import hu.congressline.pcs.domain.InvoiceCharge;

public interface InvoiceChargeRepository extends JpaRepository<InvoiceCharge, Long> {

    List<InvoiceCharge> findAllByInvoice(Invoice invoice);

}
