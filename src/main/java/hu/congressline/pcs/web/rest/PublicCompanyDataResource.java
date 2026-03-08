package hu.congressline.pcs.web.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hu.congressline.pcs.service.PublicCompanyDataService;
import hu.congressline.pcs.service.dto.publiccompanydata.DetailResponse;
import hu.congressline.pcs.service.dto.publiccompanydata.SearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class PublicCompanyDataResource {

    private final PublicCompanyDataService service;

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/public-company-data/search")
    public ResponseEntity<?> search(@RequestParam String keyword) {
        SearchResponse response = service.searchByName(keyword);
        if (response.status()) {
            return ResponseEntity.ok(response.response().results());
        } else {
            return ResponseEntity.status(response.statusCode()).body(response.error());
        }
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/public-company-data/detail")
    public ResponseEntity<?> detail(@RequestParam String id) {
        final DetailResponse response = service.getCompanyDetail(id);
        if (response.status()) {
            return ResponseEntity.ok(response.response().results());
        } else {
            return ResponseEntity.status(response.statusCode()).body(response.error());
        }
    }
}
