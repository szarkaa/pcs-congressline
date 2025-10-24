package hu.congressline.pcs.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.time.LocalDate;

import hu.congressline.pcs.domain.Company;
import hu.congressline.pcs.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class CompanyService {

    private static final String INVOICE_NUMBER_PATTERN = "00000";

    private final CompanyRepository companyRepository;

    @SuppressWarnings("MissingJavadocMethod")
    public Company save(Company company) {
        log.debug("Request to save Company : {}", company);
        return companyRepository.save(company);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Company getCompanyProfile() {
        Company company;
        if (companyRepository.count() > 0) {
            company = companyRepository.findAll().get(0);
        } else {
            company = new Company();
            company.setInvoiceNumber(0);
        }
        return company;
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional
    public synchronized Integer generateInvoiceNumber() {
        Company companyProfile = getCompanyProfile();
        if (companyProfile.getAnnualYearPrefix() == null) {
            companyProfile.setAnnualYearPrefix(LocalDate.now().getYear());
        } else if (!companyProfile.getAnnualYearPrefix().equals(LocalDate.now().getYear())) {
            companyProfile.setAnnualYearPrefix(LocalDate.now().getYear());
            companyProfile.setInvoiceNumber(0);
        }

        Integer invoiceNumber = companyProfile.getInvoiceNumber();
        companyProfile.setInvoiceNumber(invoiceNumber + 1);
        Company result = save(companyProfile);
        return result.getInvoiceNumber();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional
    public synchronized Integer generateProformaInvoiceNumber() {
        Company companyProfile = getCompanyProfile();
        if (companyProfile.getAnnualYearPrefix() == null) {
            companyProfile.setAnnualYearPrefix(LocalDate.now().getYear());
        } else if (!companyProfile.getAnnualYearPrefix().equals(LocalDate.now().getYear())) {
            companyProfile.setAnnualYearPrefix(LocalDate.now().getYear());
            companyProfile.setProFormaInvoiceNumber(0);
        }

        Integer invoiceNumber = companyProfile.getProFormaInvoiceNumber();
        companyProfile.setProFormaInvoiceNumber(invoiceNumber + 1);
        Company result = save(companyProfile);
        return result.getProFormaInvoiceNumber();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional
    public String getNextFullInvoiceNumber() {
        final Integer invoiceNumber = generateInvoiceNumber();
        final Company companyProfile = getCompanyProfile();
        return companyProfile.getAnnualYearPrefix() + "/" + companyProfile.getInvoiceNumberPrefix() + "/" + new DecimalFormat(INVOICE_NUMBER_PATTERN).format(invoiceNumber);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional
    public String getNextProformaInvoiceNumber() {
        final Integer invoiceNumber = generateProformaInvoiceNumber();
        final Company companyProfile = getCompanyProfile();
        return "EB/" + companyProfile.getAnnualYearPrefix() + "/" + new DecimalFormat(INVOICE_NUMBER_PATTERN).format(invoiceNumber);
    }
}
