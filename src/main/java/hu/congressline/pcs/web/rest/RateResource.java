package hu.congressline.pcs.web.rest;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import hu.congressline.pcs.domain.Rate;
import hu.congressline.pcs.repository.RateRepository;
import hu.congressline.pcs.web.rest.util.HeaderUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class RateResource {
    private static final String ENTITY_NAME = "rate";
    private final RateRepository rateRepository;

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/rates")
    public ResponseEntity<Rate> createRate(@Valid @RequestBody Rate rate) throws URISyntaxException {
        log.debug("REST request to save Rate : {}", rate);
        if (rate.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new rate cannot already have an ID")).body(null);
        } else if (rateRepository.findOneByCurrencyCurrencyAndValid(rate.getCurrency().getCurrency(), rate.getValid()).isPresent()) {
            return ResponseEntity.badRequest()
                .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "rateexists", "Rate for this currency already exists valid from this day!"))
                .body(null);
        }

        Rate result = rateRepository.save(rate);
        return ResponseEntity.created(new URI("/api/rates/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PutMapping("/rates")
    public ResponseEntity<Rate> updateRate(@Valid @RequestBody Rate rate) throws URISyntaxException {
        log.debug("REST request to update Rate : {}", rate);
        if (rate.getId() == null) {
            return createRate(rate);
        }
        Rate result = rateRepository.save(rate);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, rate.getId().toString()))
            .body(result);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/rates")
    public List<Rate> getAllRates() {
        log.debug("REST request to get all Rates");
        return rateRepository.findAll();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/rates/{id}")
    public ResponseEntity<Rate> getRate(@PathVariable Long id) {
        log.debug("REST request to get Rate : {}", id);
        return rateRepository.findById(id)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/rates/current/{currency}")
    public ResponseEntity<Rate> getCurrentRate(@PathVariable String currency) {
        log.debug("REST request to get Rate for currency: {}", currency);
        List<Rate> rates = rateRepository.getRates(currency);
        Rate rate = rates.stream().findFirst().orElse(null);
        if (rate != null && !rate.getValid().isBefore(LocalDate.now())) {
            return new ResponseEntity<>(rate, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/rates/{validDate}/{currency}")
    public ResponseEntity<Rate> getRateForDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate validDate, @PathVariable String currency) {
        log.debug("REST request to get Rate for currency for valid date: {}", currency);
        Optional<Rate> rate = rateRepository.findOneByCurrencyCurrencyAndValid(currency, validDate);

        if (rate.isPresent() && !rate.get().getValid().isBefore(LocalDate.now())) {
            return new ResponseEntity<>(rate.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @SuppressWarnings("MissingJavadocMethod")
    @DeleteMapping("/rates/{id}")

    public ResponseEntity<Void> deleteRate(@PathVariable Long id) {
        log.debug("REST request to delete Rate : {}", id);
        try {
            rateRepository.deleteById(id);
            return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
        } catch (DataIntegrityViolationException e) {
            log.debug("Constraint violation exception during delete operation.", e);
            return ResponseEntity.badRequest().headers(HeaderUtil.createDeleteConstraintViolationAlert(ENTITY_NAME, e)).body(null);
        }

    }

}
