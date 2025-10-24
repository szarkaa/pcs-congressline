package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import hu.congressline.pcs.domain.Invoice;
import hu.congressline.pcs.domain.enumeration.InvoiceNavStatus;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findAllByNavStatusIn(List<InvoiceNavStatus> statusList);
}
