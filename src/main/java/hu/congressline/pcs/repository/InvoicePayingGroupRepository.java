package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import hu.congressline.pcs.domain.InvoicePayingGroup;
import hu.congressline.pcs.domain.enumeration.InvoiceType;

public interface InvoicePayingGroupRepository extends JpaRepository<InvoicePayingGroup, Long> {

    List<InvoicePayingGroup> findByPayingGroupId(Long id);

    Optional<InvoicePayingGroup> findByInvoiceId(Long id);

    List<InvoicePayingGroup> findByPayingGroupCongressId(Long id);

    List<InvoicePayingGroup> findByInvoiceInvoiceTypeInAndInvoiceCreatedDateBetween(List<InvoiceType> invoiceTypes, LocalDate fromDate, LocalDate toDate);

    List<InvoicePayingGroup> findByInvoiceInvoiceTypeInAndPayingGroupCongressProgramNumberAndInvoiceCreatedDateBetweenAndInvoiceInvoiceNumberOrInvoiceStornoInvoiceNumber(
            List<InvoiceType> invoiceTypes, String programNumber, LocalDate fromDate, LocalDate toDate, String invoiceNumber, String stornoInvoiceNumber);

    List<InvoicePayingGroup> findByInvoiceInvoiceTypeInAndPayingGroupCongressProgramNumberAndInvoiceCreatedDateBetween(List<InvoiceType> invoiceTypes, String programNumber,
                                                                                                                       LocalDate fromDate, LocalDate toDate);

    List<InvoicePayingGroup> findByInvoiceInvoiceTypeInAndInvoiceCreatedDateBetweenAndInvoiceInvoiceNumberOrInvoiceStornoInvoiceNumber(
            List<InvoiceType> invoiceTypes, LocalDate fromDate, LocalDate toDate, String invoiceNumber, String stornoInvoiceNumber);
}
