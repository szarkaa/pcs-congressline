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
import hu.congressline.pcs.domain.RegistrationRegistrationType;
import hu.congressline.pcs.domain.enumeration.ChargeableItemType;
import hu.congressline.pcs.repository.ChargedServiceRepository;
import hu.congressline.pcs.repository.GroupDiscountInvoiceHistoryRepository;
import hu.congressline.pcs.repository.InvoicePayingGroupRepository;
import hu.congressline.pcs.repository.RegistrationRegistrationTypeRepository;
import hu.congressline.pcs.service.dto.RegFeeDetailsDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class RegFeeDetailsReportService extends XlsReportService {

    private final RegistrationRegistrationTypeRepository rrtRepository;
    private final InvoicePayingGroupRepository ipRepository;
    private final GroupDiscountInvoiceHistoryRepository gdihRepository;
    private final ChargedServiceRepository csRepository;
    private final DiscountService discountService;
    private final CongressService congressService;

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<RegFeeDetailsDTO> findAll(Long congressId) {
        log.debug("Request to get all RegFeeDetailsDTO");
        Congress congress = congressService.getById(congressId);
        Set<RegFeeDetailsDTO> dtos = new HashSet<>();
        List<RegistrationRegistrationType> rrtList = rrtRepository.findAllByRegistrationCongressId(congress.getId());

        rrtList.forEach(rrt -> {
            final RegFeeDetailsDTO dto = dtos.stream().filter(o -> rrt.getRegistrationType().getId().equals(o.getId()))
                    .findFirst().orElse(new RegFeeDetailsDTO());
            dto.setId(rrt.getRegistrationType().getId());
            dto.setCode(rrt.getRegistrationType().getCode());
            dto.setName(rrt.getRegistrationType().getName());
            dto.setCurrency(rrt.getChargeableItemCurrency());

            if (rrt.getRegFee().equals(rrt.getRegistrationType().getFirstRegFee())) {
                dto.setFirstCount(dto.getFirstCount() + rrt.getAccPeople());
                dto.setFirstFee(dto.getFirstFee().add(rrt.getRegFee().multiply(new BigDecimal(rrt.getAccPeople()))));
            } else if (rrt.getRegFee().equals(rrt.getRegistrationType().getSecondRegFee())) {
                dto.setSecondCount(dto.getSecondCount() + rrt.getAccPeople());
                dto.setSecondFee(dto.getSecondFee().add(rrt.getRegFee().multiply(new BigDecimal(rrt.getAccPeople()))));
            } else if (rrt.getRegFee().equals(rrt.getRegistrationType().getThirdRegFee())) {
                dto.setThirdCount(dto.getThirdCount() + rrt.getAccPeople());
                dto.setThirdFee(dto.getThirdFee().add(rrt.getRegFee().multiply(new BigDecimal(rrt.getAccPeople()))));
            }

            dtos.add(dto);
        });

        //Paid by person
        List<ChargedService> chargedServices = csRepository.findAllByRegistrationCongress(congress);
        chargedServices.stream()
            .filter(o -> o.getPaymentType().equals(ChargeableItemType.REGISTRATION))
            .forEach(cs -> {
                final RegFeeDetailsDTO dto = dtos.stream()
                    .filter(o -> ((RegistrationRegistrationType) cs.getChargeableItem()).getRegistrationType().getId().equals(o.getId()))
                    .findFirst().orElse(new RegFeeDetailsDTO());
                dto.setPaidByPerson(dto.getPaidByPerson().add(cs.getAmount()));
                dtos.add(dto);
            });

        // Paid by group
        final List<Invoice> invoices = ipRepository.findByPayingGroupCongressId(congress.getId())
                .stream().map(InvoicePayingGroup::getInvoice).filter(invoice -> !invoice.getStornired()).collect(Collectors.toList());
        final List<GroupDiscountInvoiceHistory> discountInvoiceHistories = gdihRepository.findAllByInvoiceIn(invoices);
        final Set<ChargeableItem> chargeableItems = discountInvoiceHistories.stream()
                .map(GroupDiscountInvoiceHistory::getChargeableItem)
                .filter(chargeableItem -> chargeableItem.getChargeableItemType().equals(ChargeableItemType.REGISTRATION))
                .filter(chargeableItem -> chargeableItem.getDateOfGroupPayment() != null).collect(Collectors.toSet());

        chargeableItems.forEach(item -> {
            final RegFeeDetailsDTO dto = dtos.stream()
                    .filter(o -> ((RegistrationRegistrationType) item).getRegistrationType().getId().equals(o.getId()))
                    .findFirst().orElse(new RegFeeDetailsDTO());
            final BigDecimal paidByGroup = discountService.getAmountOfDiscount((RegistrationRegistrationType) item);
            dto.setPaidByGroup(dto.getPaidByGroup().add(paidByGroup));
            dtos.add(dto);
        });

        final List<RegFeeDetailsDTO> returnList = new ArrayList<>(dtos);
        returnList.sort(Comparator.comparing(RegFeeDetailsDTO::getName));
        return returnList;
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public byte[] downloadReportXls(Congress congress) throws IOException {
        final List<RegFeeDetailsDTO> dtos = findAll(congress.getId());
        final XSSFWorkbook workbook = new XSSFWorkbook();
        Map<String, Integer> columns = new LinkedHashMap<>();
        columns.put("Code", 100);
        columns.put("Name", 200);
        columns.put("Early c.", 100);
        columns.put("Medium c.", 100);
        columns.put("Late c.", 100);
        columns.put("Early p.", 100);
        columns.put("Medium p.", 100);
        columns.put("Late p.", 100);
        columns.put("Total", 100);
        columns.put("Paid by person", 100);
        columns.put("Paid by group", 100);
        columns.put("Currency", 100);

        final XSSFSheet sheet = createXlsxTab(workbook, "Registration fee details", "Registration orders", congress.getName(), getColumnWidthsAsArray(columns));
        addSubHeader(sheet, columns);

        XSSFCellStyle wrappingCellStyle = workbook.createCellStyle();
        wrappingCellStyle.setWrapText(true);

        int rowIndex = 4;
        for (RegFeeDetailsDTO dto : dtos) {
            final XSSFRow row = sheet.createRow(rowIndex);
            addCell(row, wrappingCellStyle, 0, dto.getCode());
            addCell(row, wrappingCellStyle, 1, dto.getName());
            addCell(row, wrappingCellStyle, 2, dto.getFirstCount());
            addCell(row, wrappingCellStyle, 3, dto.getSecondCount());
            addCell(row, wrappingCellStyle, 4, dto.getThirdCount());
            addCell(row, wrappingCellStyle, 5, dto.getFirstFee());
            addCell(row, wrappingCellStyle, 6, dto.getSecondFee());
            addCell(row, wrappingCellStyle, 7, dto.getThirdFee());
            addCell(row, wrappingCellStyle, 8, dto.getTotal());
            addCell(row, wrappingCellStyle, 9, dto.getPaidByPerson());
            addCell(row, wrappingCellStyle, 10, dto.getPaidByGroup());
            addCell(row, wrappingCellStyle, 11, dto.getCurrency());
            rowIndex++;
        }

        addListedItemsCountRow(sheet, getTotalRowStyle(workbook), rowIndex, dtos.size());

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            workbook.write(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("An error occurred while creating the Registration fee details report XLSX file", e);
            throw e;
        }
    }
}
