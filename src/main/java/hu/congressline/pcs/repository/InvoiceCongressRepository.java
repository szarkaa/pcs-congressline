package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

import hu.congressline.pcs.domain.Invoice;
import hu.congressline.pcs.domain.InvoiceCongress;
import hu.congressline.pcs.domain.enumeration.InvoiceType;

public interface InvoiceCongressRepository extends JpaRepository<InvoiceCongress, Long> {

    List<InvoiceCongress> findByCongressId(Long id);

    List<InvoiceCongress> findByInvoice(Invoice invoice);

    InvoiceCongress findByInvoiceId(Long id);

    List<InvoiceCongress> findByInvoiceInvoiceTypeInAndInvoiceCreatedDateBetween(List<InvoiceType> invoiceTypes, LocalDate fromDate, LocalDate toDate);

    List<InvoiceCongress> findByInvoiceInvoiceTypeInAndCongressProgramNumberAndInvoiceCreatedDateBetweenAndInvoiceInvoiceNumberOrInvoiceStornoInvoiceNumber(
            List<InvoiceType> invoiceTypes, String programNumber, LocalDate fromDate, LocalDate toDate, String invoiceNumber, String stornoInvoiceNumber);

    List<InvoiceCongress> findByInvoiceInvoiceTypeInAndCongressProgramNumberAndInvoiceCreatedDateBetween(List<InvoiceType> invoiceTypes, String programNumber,
                                                                                                         LocalDate fromDate, LocalDate toDate);

    List<InvoiceCongress> findByInvoiceInvoiceTypeInAndInvoiceCreatedDateBetweenAndInvoiceInvoiceNumberOrInvoiceStornoInvoiceNumber(List<InvoiceType> invoiceTypes,
                                                                                                                                    LocalDate fromDate, LocalDate toDate,
                                                                                                                                    String invoiceNumber,
                                                                                                                                    String stornoInvoiceNumber);

}
