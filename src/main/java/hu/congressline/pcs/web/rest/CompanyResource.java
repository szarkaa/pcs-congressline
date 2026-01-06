package hu.congressline.pcs.web.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;

import hu.congressline.pcs.domain.Company;
import hu.congressline.pcs.service.CompanyService;
import hu.congressline.pcs.web.rest.util.HeaderUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class CompanyResource {

    private final CompanyService companyService;

    @SuppressWarnings("MissingJavadocMethod")
    @PutMapping("/company")
    public ResponseEntity<Company> updateCompany(@Valid @RequestBody Company company) throws URISyntaxException {
        log.debug("REST request to update Company : {}", company);
        final Company oldCompany = companyService.getCompanyProfile();
        company.setInvoiceNumber(oldCompany.getInvoiceNumber());
        company.setProFormaInvoiceNumber(oldCompany.getProFormaInvoiceNumber());
        Company result = companyService.save(company);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("company", company.getId().toString()))
                .body(result);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/company")
    public ResponseEntity<Company> getTheCompanyProfile() {
        log.debug("REST request to get the Company profile");
        return new ResponseEntity<>(companyService.getCompanyProfile(), HttpStatus.OK);
    }

}
