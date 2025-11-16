package hu.congressline.pcs.service;

import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import hu.congressline.pcs.domain.ChargeableItem;
import hu.congressline.pcs.domain.ChargedService;
import hu.congressline.pcs.domain.Congress;
import hu.congressline.pcs.domain.GroupDiscountInvoiceHistory;
import hu.congressline.pcs.domain.Invoice;
import hu.congressline.pcs.domain.InvoicePayingGroup;
import hu.congressline.pcs.domain.Registration;
import hu.congressline.pcs.domain.RegistrationRegistrationType;
import hu.congressline.pcs.domain.enumeration.ChargeableItemType;
import hu.congressline.pcs.repository.ChargedServiceRepository;
import hu.congressline.pcs.repository.GroupDiscountInvoiceHistoryRepository;
import hu.congressline.pcs.repository.InvoicePayingGroupRepository;
import hu.congressline.pcs.repository.RegistrationRegistrationTypeRepository;
import hu.congressline.pcs.service.dto.RegFeeDetailsByParticipantDTO;
import hu.congressline.pcs.web.rest.vm.RegFeeDetailsByParticipantsVM;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class RegFeeDetailsByParticipantReportService extends XlsReportService {

    private final RegistrationRegistrationTypeRepository rrtRepository;
    private final InvoicePayingGroupRepository ipRepository;
    private final DiscountService discountService;
    private final GroupDiscountInvoiceHistoryRepository gdihRepository;
    private final ChargedServiceRepository csRepository;
    private final CongressService congressService;

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<RegFeeDetailsByParticipantDTO> findAll(RegFeeDetailsByParticipantsVM reportFilter) {
        log.debug("Request to get all RegFeeDetailsByParticipantDTO");
        Congress congress = congressService.getById(reportFilter.getCongressId());
        Set<RegFeeDetailsByParticipantDTO> dtos = new HashSet<>();
        List<RegistrationRegistrationType> rrtList;
        if (reportFilter.getRegistrationType() != null) {
            rrtList = rrtRepository.findAllByRegistrationCongressIdAndRegistrationType(congress.getId(), reportFilter.getRegistrationType());
        } else {
            rrtList = rrtRepository.findAllByRegistrationCongressId(congress.getId());
        }

        rrtList.forEach(rrt -> {
            final RegFeeDetailsByParticipantDTO dto = dtos.stream().filter(o -> rrt.getRegistration().getRegId().equals(o.getRegId()))
                    .findFirst().orElse(new RegFeeDetailsByParticipantDTO());

            dto.setRegId(rrt.getRegistration().getRegId());
            dto.setName(rrt.getRegistration().getLastName() + ", " + rrt.getRegistration().getFirstName());
            dto.setPayingGroupName(rrt.getPayingGroupItem() != null ? rrt.getPayingGroupItem().getPayingGroup().getName() : "");
            if (rrt.getRegFee().equals(rrt.getRegistrationType().getFirstRegFee())) {
                dto.setFirstFee(dto.getFirstFee().add(rrt.getRegFee().multiply(new BigDecimal(rrt.getAccPeople()))));
            } else if (rrt.getRegFee().equals(rrt.getRegistrationType().getSecondRegFee())) {
                dto.setSecondFee(dto.getSecondFee().add(rrt.getRegFee().multiply(new BigDecimal(rrt.getAccPeople()))));
            } else if (rrt.getRegFee().equals(rrt.getRegistrationType().getThirdRegFee())) {
                dto.setThirdFee(dto.getThirdFee().add(rrt.getRegFee().multiply(new BigDecimal(rrt.getAccPeople()))));
            }

            dtos.add(dto);
        });

        //Paid by person
        List<ChargedService> chargedServices;
        if (reportFilter.getRegistrationType() != null) {
            chargedServices = csRepository.findAllByRegistrationCongressAndRegistrationIdIn(congress,
                rrtList.stream().map(RegistrationRegistrationType::getRegistration).map(Registration::getId).collect(Collectors.toList()));
        } else {
            chargedServices = csRepository.findAllByRegistrationCongress(congress);
        }

        chargedServices.stream()
            .filter(o -> o.getChargeableItem() != null && o.getChargeableItem().getChargeableItemType().equals(ChargeableItemType.REGISTRATION))
            .filter(o -> ((RegistrationRegistrationType) o.getChargeableItem()).getRegistrationType().getId().equals(reportFilter.getRegistrationType().getId()))
            .forEach(cs -> {
                final RegFeeDetailsByParticipantDTO dto = dtos.stream()
                    .filter(o -> cs.getRegistration().getRegId().equals(o.getRegId()))
                    .findFirst().orElse(new RegFeeDetailsByParticipantDTO());
                dto.setPaidByPerson(dto.getPaidByPerson().add(cs.getAmount()));
                dtos.add(dto);
            });

        // Paid by group
        final List<Invoice> invoices = ipRepository.findByPayingGroupCongressId(congress.getId())
                .stream().map(InvoicePayingGroup::getInvoice).filter(invoice -> !invoice.getStornired()).collect(Collectors.toList());
        final List<GroupDiscountInvoiceHistory> discountInvoiceHistories = gdihRepository.findAllByInvoiceIn(invoices);
        final Set<ChargeableItem> chargeableItems = discountInvoiceHistories.stream().map(GroupDiscountInvoiceHistory::getChargeableItem)
                .filter(o -> o.getDateOfGroupPayment() != null)
                .filter(o -> o.getChargeableItemType().equals(ChargeableItemType.REGISTRATION))
                .filter(reportFilter.getRegistrationType() != null ? o -> ((RegistrationRegistrationType) o).getRegistrationType().getId()
                        .equals(reportFilter.getRegistrationType().getId()) : o -> true)
                .collect(Collectors.toSet());

        chargeableItems.forEach(item -> {
            final RegFeeDetailsByParticipantDTO dto = dtos.stream()
                    .filter(o -> item.getRegistration().getRegId().equals(o.getRegId()))
                    .findFirst().orElse(new RegFeeDetailsByParticipantDTO());
            final BigDecimal paidByGroup = discountService.getAmountOfDiscount((RegistrationRegistrationType) item);
            dto.setPaidByGroup(dto.getPaidByGroup().add(paidByGroup));
            dtos.add(dto);
        });
        final List<RegFeeDetailsByParticipantDTO> returnList = new ArrayList<>(dtos);
        returnList.sort(Comparator.comparing(RegFeeDetailsByParticipantDTO::getName));
        return returnList;
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public byte[] downloadReportXls(RegFeeDetailsByParticipantsVM reportFilter) throws IOException {
        Congress congress = congressService.getById(reportFilter.getCongressId());
        final List<RegFeeDetailsByParticipantDTO> dtos = findAll(reportFilter);
        final XSSFWorkbook workbook = new XSSFWorkbook();
        Map<String, Integer> columns = new LinkedHashMap<>();
        columns.put("Reg id", 100);
        columns.put("Name", 200);
        columns.put("Order (1st d.)", 200);
        columns.put("Order (2nd d.)", 200);
        columns.put("Order (3rd d.)", 100);
        columns.put("Paid by person", 100);
        columns.put("Paid by group", 100);

        final XSSFSheet sheet = createXlsxTab(workbook, "Registration fee details by type by participant", reportFilter.getRegistrationType().getName()
                + " registration fees for Individual", congress.getName(), getColumnWidthsAsArray(columns));
        addSubHeader(sheet, columns);

        XSSFCellStyle wrappingCellStyle = workbook.createCellStyle();
        wrappingCellStyle.setWrapText(true);

        int rowIndex = 4;
        BigDecimal firstFeeTotal = BigDecimal.ZERO;
        BigDecimal secondFeeTotal = BigDecimal.ZERO;
        BigDecimal thirdFeeTotal = BigDecimal.ZERO;
        BigDecimal paidByPersonTotal = BigDecimal.ZERO;
        BigDecimal paidByGroupTotal = BigDecimal.ZERO;
        for (RegFeeDetailsByParticipantDTO dto : dtos) {
            final XSSFRow row = sheet.createRow(rowIndex);
            addCell(row, wrappingCellStyle, 0, dto.getRegId());
            addCell(row, wrappingCellStyle, 1, dto.getName());
            addCell(row, wrappingCellStyle, 2, dto.getFirstFee());
            addCell(row, wrappingCellStyle, 3, dto.getSecondFee());
            addCell(row, wrappingCellStyle, 4, dto.getThirdFee());
            addCell(row, wrappingCellStyle, 5, dto.getPaidByPerson());
            addCell(row, wrappingCellStyle, 6, dto.getPaidByGroup());
            firstFeeTotal = firstFeeTotal.add(dto.getFirstFee());
            secondFeeTotal = secondFeeTotal.add(dto.getSecondFee());
            thirdFeeTotal = thirdFeeTotal.add(dto.getThirdFee());
            paidByPersonTotal = paidByPersonTotal.add(dto.getPaidByPerson());
            paidByGroupTotal = paidByGroupTotal.add(dto.getPaidByGroup());
            rowIndex++;
        }

        // Total row
        final XSSFRow row = sheet.createRow(rowIndex++);
        final XSSFCellStyle totalRowStyle = getTotalRowStyle(workbook);
        addCell(row, totalRowStyle, 0, "Total");
        addCell(row, totalRowStyle, 1, "");
        addCell(row, totalRowStyle, 2, firstFeeTotal);
        addCell(row, totalRowStyle, 3, secondFeeTotal);
        addCell(row, totalRowStyle, 4, thirdFeeTotal);
        addCell(row, totalRowStyle, 5, paidByPersonTotal);
        addCell(row, totalRowStyle, 6, paidByGroupTotal);

        addListedItemsCountRow(sheet, getTotalRowStyle(workbook), rowIndex, dtos.size());

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            workbook.write(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("An error occured while creating the Registration fee details by participant report XLSX file", e);
            throw e;
        }
    }
}
