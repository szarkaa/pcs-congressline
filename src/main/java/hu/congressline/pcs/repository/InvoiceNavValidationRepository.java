package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import hu.congressline.pcs.domain.Invoice;
import hu.congressline.pcs.domain.InvoiceNavValidation;

public interface InvoiceNavValidationRepository extends JpaRepository<InvoiceNavValidation, Long> {

    List<InvoiceNavValidation> findByInvoice(Invoice invoice);

    List<InvoiceNavValidation> findByInvoiceId(Long id);

    void deleteByInvoice(Invoice invoice);
}
