package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import hu.congressline.pcs.domain.Invoice;
import hu.congressline.pcs.domain.InvoiceRegistration;
import hu.congressline.pcs.domain.Registration;
import hu.congressline.pcs.domain.enumeration.InvoiceType;

public interface InvoiceRegistrationRepository extends JpaRepository<InvoiceRegistration, Long> {

    List<InvoiceRegistration> findByRegistrationIdOrderByIdDesc(Long id);

    List<InvoiceRegistration> findByRegistration(Registration registration);

    List<InvoiceRegistration> findByRegistrationId(Long id);

    List<InvoiceRegistration> findByInvoice(Invoice invoice);

    Optional<InvoiceRegistration> findByInvoiceId(Long id);

    List<InvoiceRegistration> findByRegistrationCongressId(Long id);

    List<InvoiceRegistration> findByInvoiceInvoiceTypeInAndInvoiceCreatedDateBetween(List<InvoiceType> invoiceTypes, LocalDate fromDate, LocalDate toDate);

    List<InvoiceRegistration> findByInvoiceInvoiceTypeInAndRegistrationCongressProgramNumberAndInvoiceCreatedDateBetweenAndInvoiceInvoiceNumberOrInvoiceStornoInvoiceNumber(
            List<InvoiceType> invoiceTypes, String programNumber, LocalDate fromDate, LocalDate toDate, String invoiceNumber, String stornoInvoiceNumber);

    List<InvoiceRegistration> findByInvoiceInvoiceTypeInAndRegistrationCongressProgramNumberAndInvoiceCreatedDateBetween(
            List<InvoiceType> invoiceTypes, String programNumber, LocalDate fromDate, LocalDate toDate);

    List<InvoiceRegistration> findByInvoiceInvoiceTypeInAndInvoiceCreatedDateBetweenAndInvoiceInvoiceNumberOrInvoiceStornoInvoiceNumber(
            List<InvoiceType> invoiceTypes, LocalDate fromDate, LocalDate toDate, String invoiceNumber, String stornoInvoiceNumber);
}
