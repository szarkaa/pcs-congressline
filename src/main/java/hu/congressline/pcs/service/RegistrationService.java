package hu.congressline.pcs.service;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import hu.congressline.pcs.domain.BankAccount;
import hu.congressline.pcs.domain.ChargeableItem;
import hu.congressline.pcs.domain.Congress;
import hu.congressline.pcs.domain.Country;
import hu.congressline.pcs.domain.OrderedOptionalService;
import hu.congressline.pcs.domain.Registration;
import hu.congressline.pcs.domain.RegistrationRegistrationType;
import hu.congressline.pcs.domain.RoomReservationRegistration;
import hu.congressline.pcs.domain.Workplace;
import hu.congressline.pcs.domain.enumeration.RegistrationTypeType;
import hu.congressline.pcs.repository.AccPeopleRepository;
import hu.congressline.pcs.repository.ChargedServiceRepository;
import hu.congressline.pcs.repository.CountryRepository;
import hu.congressline.pcs.repository.OnlineRegistrationCustomAnswerRepository;
import hu.congressline.pcs.repository.OrderedOptionalServiceRepository;
import hu.congressline.pcs.repository.RegistrationRegistrationTypeRepository;
import hu.congressline.pcs.repository.RegistrationRepository;
import hu.congressline.pcs.repository.RoomReservationRegistrationRepository;
import hu.congressline.pcs.repository.WorkplaceRepository;
import hu.congressline.pcs.service.util.RegistrationUploadHeader;
import hu.congressline.pcs.web.rest.vm.PcsBatchUploadVm;
import hu.congressline.pcs.web.rest.vm.RegistrationSummaryVM;
import hu.congressline.pcs.web.rest.vm.RegistrationVM;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final RegistrationRegistrationTypeRepository rrtRepository;
    private final RoomReservationRegistrationRepository rrrRepository;
    private final OrderedOptionalServiceRepository oosRepository;
    private final AccPeopleRepository accPeopleRepository;
    private final OrderedOptionalServiceService oosService;
    private final ChargedServiceRepository chargedServiceRepository;
    private final RoomReservationService roomReservationService;
    private final CongressService congressService;
    private final CountryRepository countryRepository;
    private final WorkplaceRepository workplaceRepository;
    private final OnlineRegistrationCustomAnswerRepository orcaRepository;

    @SuppressWarnings("MissingJavadocMethod")
    public Registration save(Registration registration) {
        log.debug("Request to save Registration : {}", registration);
        injectRegId(registration);
        if (registration.getDateOfApp() == null) {
            registration.setDateOfApp(LocalDate.now());
        }
        return registrationRepository.save(registration);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<RegistrationVM> findAllRegistrationVMByCongressId(Long congressId) {
        log.debug("Request to get all Registration ids");
        return registrationRepository.findAllByCongressId(congressId).stream().map(RegistrationVM::new).collect(Collectors.toList());
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<Registration> findAllByCongressId(Long id) {
        log.debug("Request to get all Registrations by congress id: {}", id);
        return registrationRepository.findAllByCongressId(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional
    public Optional<Registration> findTheFirstOneByCongressId(Long id) {
        return registrationRepository.findFirstRegistrationByCongressId(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Optional<Registration> findById(Long id) {
        log.debug("Request to find Registration : {}", id);
        return registrationRepository.findById(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Registration getById(Long id) {
        log.debug("Request to get Registration : {}", id);
        return registrationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Registration not found with id: " + id));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Long findNextIdAfterDeletedId(Long id, Long congressId) {
        Long nextId = null;
        List<Long> list = registrationRepository.findNextIdAfterDeletedId(id, congressId);
        if (list.isEmpty()) {
            list = registrationRepository.findPreviousIdAfterDeletedId(id, congressId);
            if (!list.isEmpty()) {
                nextId = list.get(0);
            }
        } else {
            nextId = list.get(0);
        }
        return nextId;
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public RegistrationSummaryVM getRegistrationSummaryByCongressId(Long id) {
        RegistrationSummaryVM dto = new RegistrationSummaryVM();
        dto.setRegistered(registrationRepository.countByCongressId(id));
        dto.setOnSpot(registrationRepository.countByCongressIdAndOnSpot(id, Boolean.TRUE));
        dto.setAccPeople(registrationRepository.countAccPeopleByCongressId(id, RegistrationTypeType.ACCOMPANYING_FEE));
        return dto;
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public String getRegistrationCurrency(Registration registration) {
        final List<RegistrationRegistrationType> rrtList = rrtRepository.findAllByRegistrationId(registration.getId());
        final List<RoomReservationRegistration> rrrList = rrrRepository.findAllByRegistrationId(registration.getId());
        final List<OrderedOptionalService> oosList = oosRepository.findAllByRegistrationId(registration.getId());

        return getCurrency(rrtList, rrrList, oosList);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void delete(Long id) {
        log.debug("Request to delete Registration : {}", id);
        orcaRepository.deleteAllByRegistrationId(id);
        accPeopleRepository.deleteAllByRegistrationRegistrationTypeRegistrationId(id);
        rrtRepository.deleteAllByRegistrationId(id);
        roomReservationService.deleteAllByRegistrationId(id);
        oosService.deleteAllByRegistrationId(id);
        chargedServiceRepository.deleteAllByRegistrationId(id);
        registrationRepository.deleteById(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public List<String> processUploadedRegistrations(PcsBatchUploadVm pcsFile) {
        log.debug("Process uploaded registrations");
        List<String> messageList = new ArrayList<>();
        Congress congress = congressService.getById(pcsFile.getCongressId());
        try {
            final XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(pcsFile.getFile()));
            final XSSFSheet sheet = workbook.getSheetAt(0);

            final Map<RegistrationUploadHeader, Integer> headerNameIndexMap = collectBatchUploadHeaderNames(sheet.getRow(0), messageList);
            if (!messageList.isEmpty()) {
                String message = "First row must be header row with the following possible headers: title, family_name, first_name, position, "
                        + "other_data, workplace, department, country, zip_code, city, street, phone, email, fax, invoice_name,"
                        + "invoice_country, invoice_zip_code, invoice_city, invoice_address, invoice_tax_number,"
                        + "workplace_name, workplace_vat_reg_number, workplace_department, workplace_zip_code, workplace_city,"
                        + "workplace_street, workplace_phone, workplace_fax, workplace_email, workplace_country";
                messageList.add(message);
                return messageList;
            }

            for (int rowIdx = 1; rowIdx <= sheet.getLastRowNum(); rowIdx++) {
                final XSSFRow row = sheet.getRow(rowIdx);
                validateBatchUploadRow(row, rowIdx, headerNameIndexMap, messageList);
            }

            if (messageList.isEmpty()) {
                List<Registration> list = new ArrayList<>();
                for (int rowIdx = 1; rowIdx <= sheet.getLastRowNum(); rowIdx++) {
                    final XSSFRow row = sheet.getRow(rowIdx);
                    list.add(createRegistrationFromBatchUploadRow(congress, row, headerNameIndexMap));
                }

                list.forEach(registration -> {
                    final Registration result = save(registration);
                    messageList.add(result.getLastName() + ", " + result.getFirstName() + " successfully saved with reg id: " + result.getRegId());
                });
            }

        } catch (IOException e) {
            log.error("Failed to create xlsx workbook from uploaded bytestream", e);
            messageList.add("Failed to create xlsx registrations from the uploaded file.");
        }
        return messageList;
    }

    private String getCurrency(List<? extends ChargeableItem>... items) {
        String currency = null;
        for (List<? extends ChargeableItem> itemList : items) {
            for (ChargeableItem item : itemList) {
                currency = item.getChargeableItemCurrency();
                break;
            }

            if (currency != null) {
                break;
            }
        }
        return currency;
    }

    private Registration createRegistrationFromBatchUploadRow(Congress congress, XSSFRow row, Map<RegistrationUploadHeader, Integer> headerNameIndexMap) {
        Registration registration = new Registration();
        registration.setCongress(congress);
        DataFormatter formatter = new DataFormatter();
        Optional.ofNullable(headerNameIndexMap.get(RegistrationUploadHeader.REG_UPLOAD_TITLE))
                .flatMap(idx -> Optional.ofNullable(row.getCell(idx))).ifPresent(cell -> registration.setTitle(formatter.formatCellValue(cell)));
        Optional.ofNullable(headerNameIndexMap.get(RegistrationUploadHeader.REG_UPLOAD_FAMILY_NAME))
                .flatMap(idx -> Optional.ofNullable(row.getCell(idx))).ifPresent(cell -> registration.setLastName(formatter.formatCellValue(cell)));
        Optional.ofNullable(headerNameIndexMap.get(RegistrationUploadHeader.REG_UPLOAD_FIRST_NAME))
                .flatMap(idx -> Optional.ofNullable(row.getCell(idx))).ifPresent(cell -> registration.setFirstName(formatter.formatCellValue(cell)));
        Optional.ofNullable(headerNameIndexMap.get(RegistrationUploadHeader.REG_UPLOAD_POSITION))
                .flatMap(idx -> Optional.ofNullable(row.getCell(idx))).ifPresent(cell -> registration.setPosition(formatter.formatCellValue(cell)));
        Optional.ofNullable(headerNameIndexMap.get(RegistrationUploadHeader.REG_UPLOAD_OTHER_DATA))
                .flatMap(idx -> Optional.ofNullable(row.getCell(idx))).ifPresent(cell -> registration.setOtherData(formatter.formatCellValue(cell)));
        Optional.ofNullable(headerNameIndexMap.get(RegistrationUploadHeader.REG_UPLOAD_DEPARTMENT))
                .flatMap(idx -> Optional.ofNullable(row.getCell(idx))).ifPresent(cell -> registration.setDepartment(formatter.formatCellValue(cell)));
        Optional.ofNullable(headerNameIndexMap.get(RegistrationUploadHeader.REG_UPLOAD_COUNTRY))
                .flatMap(idx -> Optional.ofNullable(row.getCell(idx))).ifPresent(cell -> registration.setCountry(getCountryFromCellValue(cell)));
        Optional.ofNullable(headerNameIndexMap.get(RegistrationUploadHeader.REG_UPLOAD_ZIP_CODE))
                .flatMap(idx -> Optional.ofNullable(row.getCell(idx))).ifPresent(cell -> registration.setZipCode(formatter.formatCellValue(cell)));
        Optional.ofNullable(headerNameIndexMap.get(RegistrationUploadHeader.REG_UPLOAD_CITY))
                .flatMap(idx -> Optional.ofNullable(row.getCell(idx))).ifPresent(cell -> registration.setCity(formatter.formatCellValue(cell)));
        Optional.ofNullable(headerNameIndexMap.get(RegistrationUploadHeader.REG_UPLOAD_STREET))
                .flatMap(idx -> Optional.ofNullable(row.getCell(idx))).ifPresent(cell -> registration.setStreet(formatter.formatCellValue(cell)));
        Optional.ofNullable(headerNameIndexMap.get(RegistrationUploadHeader.REG_UPLOAD_PHONE))
                .flatMap(idx -> Optional.ofNullable(row.getCell(idx))).ifPresent(cell -> registration.setPhone(formatter.formatCellValue(cell)));
        Optional.ofNullable(headerNameIndexMap.get(RegistrationUploadHeader.REG_UPLOAD_EMAIL))
                .flatMap(idx -> Optional.ofNullable(row.getCell(idx))).ifPresent(cell -> registration.setEmail(formatter.formatCellValue(cell)));
        Optional.ofNullable(headerNameIndexMap.get(RegistrationUploadHeader.REG_UPLOAD_FAX))
                .flatMap(idx -> Optional.ofNullable(row.getCell(idx))).ifPresent(cell -> registration.setFax(formatter.formatCellValue(cell)));
        Optional.ofNullable(headerNameIndexMap.get(RegistrationUploadHeader.REG_UPLOAD_INVOICE_NAME))
                .flatMap(idx -> Optional.ofNullable(row.getCell(idx))).ifPresent(cell -> registration.setInvoiceName(formatter.formatCellValue(cell)));
        Optional.ofNullable(headerNameIndexMap.get(RegistrationUploadHeader.REG_UPLOAD_INVOICE_COUNTRY))
                .flatMap(idx -> Optional.ofNullable(row.getCell(idx))).ifPresent(cell -> registration.setInvoiceCountry(getCountryFromCellValue(cell)));
        Optional.ofNullable(headerNameIndexMap.get(RegistrationUploadHeader.REG_UPLOAD_INVOICE_ZIP_CODE))
                .flatMap(idx -> Optional.ofNullable(row.getCell(idx))).ifPresent(cell -> registration.setInvoiceZipCode(formatter.formatCellValue(cell)));
        Optional.ofNullable(headerNameIndexMap.get(RegistrationUploadHeader.REG_UPLOAD_INVOICE_CITY))
                .flatMap(idx -> Optional.ofNullable(row.getCell(idx))).ifPresent(cell -> registration.setInvoiceCity(formatter.formatCellValue(cell)));
        Optional.ofNullable(headerNameIndexMap.get(RegistrationUploadHeader.REG_UPLOAD_INVOICE_ADDRESS))
                .flatMap(idx -> Optional.ofNullable(row.getCell(idx))).ifPresent(cell -> registration.setInvoiceAddress(formatter.formatCellValue(cell)));
        Optional.ofNullable(headerNameIndexMap.get(RegistrationUploadHeader.REG_UPLOAD_INVOICE_TAX_NUMBER))
                .flatMap(idx -> Optional.ofNullable(row.getCell(idx))).ifPresent(cell -> registration.setInvoiceTaxNumber(formatter.formatCellValue(cell)));

        registration.setWorkplace(getWorkplaceFromCellValue(congress, row, headerNameIndexMap));
        return registration;
    }

    private Workplace getWorkplaceFromCellValue(Congress congress, XSSFRow row, Map<RegistrationUploadHeader, Integer> headerNameIndexMap) {
        if (headerNameIndexMap.get(RegistrationUploadHeader.REG_UPLOAD_WORKPLACE_NAME) == null) {
            return null;
        }

        DataFormatter formatter = new DataFormatter();
        Workplace workplace = new Workplace();
        workplace.setCongress(congress);
        Optional.ofNullable(headerNameIndexMap.get(RegistrationUploadHeader.REG_UPLOAD_WORKPLACE_NAME))
                .flatMap(idx -> Optional.ofNullable(row.getCell(idx))).ifPresent(cell -> workplace.setName(formatter.formatCellValue(cell)));
        Optional.ofNullable(headerNameIndexMap.get(RegistrationUploadHeader.REG_UPLOAD_WORKPLACE_VAT_REG_NUMBER))
                .flatMap(idx -> Optional.ofNullable(row.getCell(idx))).ifPresent(cell -> workplace.setVatRegNumber(formatter.formatCellValue(cell)));
        Optional.ofNullable(headerNameIndexMap.get(RegistrationUploadHeader.REG_UPLOAD_WORKPLACE_COUNTRY))
                .flatMap(idx -> Optional.ofNullable(row.getCell(idx))).ifPresent(cell -> workplace.setCountry(getCountryFromCellValue(cell)));
        Optional.ofNullable(headerNameIndexMap.get(RegistrationUploadHeader.REG_UPLOAD_WORKPLACE_DEPARTMENT))
                .flatMap(idx -> Optional.ofNullable(row.getCell(idx))).ifPresent(cell -> workplace.setDepartment(formatter.formatCellValue(cell)));
        Optional.ofNullable(headerNameIndexMap.get(RegistrationUploadHeader.REG_UPLOAD_WORKPLACE_ZIP_CODE))
                .flatMap(idx -> Optional.ofNullable(row.getCell(idx))).ifPresent(cell -> workplace.setZipCode(formatter.formatCellValue(cell)));
        Optional.ofNullable(headerNameIndexMap.get(RegistrationUploadHeader.REG_UPLOAD_WORKPLACE_CITY))
                .flatMap(idx -> Optional.ofNullable(row.getCell(idx))).ifPresent(cell -> workplace.setCity(formatter.formatCellValue(cell)));
        Optional.ofNullable(headerNameIndexMap.get(RegistrationUploadHeader.REG_UPLOAD_WORKPLACE_STREET))
                .flatMap(idx -> Optional.ofNullable(row.getCell(idx))).ifPresent(cell -> workplace.setStreet(formatter.formatCellValue(cell)));
        Optional.ofNullable(headerNameIndexMap.get(RegistrationUploadHeader.REG_UPLOAD_WORKPLACE_PHONE))
                .flatMap(idx -> Optional.ofNullable(row.getCell(idx))).ifPresent(cell -> workplace.setPhone(formatter.formatCellValue(cell)));
        Optional.ofNullable(headerNameIndexMap.get(RegistrationUploadHeader.REG_UPLOAD_WORKPLACE_FAX))
                .flatMap(idx -> Optional.ofNullable(row.getCell(idx))).ifPresent(cell -> workplace.setFax(formatter.formatCellValue(cell)));
        return workplaceRepository.save(workplace);
    }

    private Country getCountryFromCellValue(XSSFCell cell) {
        DataFormatter formatter = new DataFormatter();
        if (cell == null || formatter.formatCellValue(cell).isEmpty()) {
            return null;
        }
        return countryRepository.findOneByName(formatter.formatCellValue(cell)).orElse(null);
    }

    private void validateBatchUploadRow(XSSFRow row, int rowIdx, Map<RegistrationUploadHeader, Integer> headerNameIndexMap, List<String> messageList) {
        int xlsRowIdx = rowIdx + 1;
        int cellIdx = Optional.ofNullable(headerNameIndexMap.get(RegistrationUploadHeader.REG_UPLOAD_FAMILY_NAME)).orElse(Integer.MAX_VALUE);
        if (row == null) {
            messageList.add(xlsRowIdx + ". row can not be empty. Fill it properly or delete it.");
        }

        XSSFCell cell = row.getCell(cellIdx);
        if (cell == null || cell.getStringCellValue().isEmpty()) {
            messageList.add(xlsRowIdx + ". row: Family name can not be empty.");
        }

        cellIdx = Optional.ofNullable(headerNameIndexMap.get(RegistrationUploadHeader.REG_UPLOAD_FIRST_NAME)).orElse(Integer.MAX_VALUE);
        cell = row.getCell(cellIdx);
        if (cell == null || cell.getStringCellValue().isEmpty()) {
            messageList.add(xlsRowIdx + ". row: First name can not be empty.");
        }

        cellIdx = Optional.ofNullable(headerNameIndexMap.get(RegistrationUploadHeader.REG_UPLOAD_COUNTRY)).orElse(Integer.MAX_VALUE);
        cell = row.getCell(cellIdx);
        if (cell != null && !cell.getStringCellValue().isEmpty()) {
            if (countryRepository.findOneByName(cell.getStringCellValue()).isEmpty()) {
                messageList.add(xlsRowIdx + ". row: Country name does not exist in the pcs system.");
            }
        }

        cellIdx = Optional.ofNullable(headerNameIndexMap.get(RegistrationUploadHeader.REG_UPLOAD_INVOICE_COUNTRY)).orElse(Integer.MAX_VALUE);
        cell = row.getCell(cellIdx);
        if (cell != null && !cell.getStringCellValue().isEmpty()) {
            if (countryRepository.findOneByName(cell.getStringCellValue()).isEmpty()) {
                messageList.add(xlsRowIdx + ". row: Invoice country name does not exist in the pcs system.");
            }
        }

        cellIdx = Optional.ofNullable(headerNameIndexMap.get(RegistrationUploadHeader.REG_UPLOAD_WORKPLACE_COUNTRY)).orElse(Integer.MAX_VALUE);
        cell = row.getCell(cellIdx);
        if (cell != null && !cell.getStringCellValue().isEmpty()) {
            if (countryRepository.findOneByName(cell.getStringCellValue()).isEmpty()) {
                messageList.add(xlsRowIdx + ". row: Workplace country name does not exist in the pcs system.");
            }
        }
    }

    private Map<RegistrationUploadHeader, Integer> collectBatchUploadHeaderNames(XSSFRow row, List<String> messageList) {
        Map<RegistrationUploadHeader, Integer> headerNameIndexMap = new HashMap<>();
        for (int colIdx = 0; colIdx < row.getLastCellNum(); colIdx++) {
            final String headerName = row.getCell(colIdx).getStringCellValue();
            final Optional<RegistrationUploadHeader> headerOpt = RegistrationUploadHeader.getHeaderByName(headerName);
            if (headerOpt.isPresent()) {
                headerNameIndexMap.put(headerOpt.get(), colIdx);
            } else {
                messageList.add(headerName + " is not a valid header name!");
            }
        }

        if (headerNameIndexMap.get(RegistrationUploadHeader.REG_UPLOAD_WORKPLACE_NAME) == null && (
                headerNameIndexMap.get(RegistrationUploadHeader.REG_UPLOAD_WORKPLACE_VAT_REG_NUMBER) != null
                    || headerNameIndexMap.get(RegistrationUploadHeader.REG_UPLOAD_WORKPLACE_DEPARTMENT) != null
                    || headerNameIndexMap.get(RegistrationUploadHeader.REG_UPLOAD_WORKPLACE_COUNTRY) != null
                    || headerNameIndexMap.get(RegistrationUploadHeader.REG_UPLOAD_WORKPLACE_ZIP_CODE) != null
                    || headerNameIndexMap.get(RegistrationUploadHeader.REG_UPLOAD_WORKPLACE_CITY) != null
                    || headerNameIndexMap.get(RegistrationUploadHeader.REG_UPLOAD_WORKPLACE_STREET) != null
                    || headerNameIndexMap.get(RegistrationUploadHeader.REG_UPLOAD_WORKPLACE_PHONE) != null
                    || headerNameIndexMap.get(RegistrationUploadHeader.REG_UPLOAD_WORKPLACE_FAX) != null
                    || headerNameIndexMap.get(RegistrationUploadHeader.REG_UPLOAD_WORKPLACE_EMAIL) != null
            )) {
            messageList.add("workplace_name must be in the column headers if any other workplace related column headers is in the header row!");
        }
        return headerNameIndexMap;
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Set<BankAccount> findBankAccountsByCongressAndCurrency(Long congressId, String currencyName) {
        Congress congress = congressService.getEagerById(congressId);
        final Set<BankAccount> bankAccounts = congress.getBankAccounts();
        return bankAccounts.stream().filter(account -> account.getCurrency().getCurrency().equals(currencyName)).collect(Collectors.toSet());
    }

    private void injectRegId(Registration registration) {
        if (registration.getRegId() == null) {
            Integer regId = registrationRepository.findLastRegistrationId(registration.getCongress().getId());
            registration.setRegId(regId == null ? Integer.valueOf(1) : Integer.valueOf(regId + 1));
        }
    }
}
