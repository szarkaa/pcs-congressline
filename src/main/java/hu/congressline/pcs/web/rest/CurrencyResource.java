package hu.congressline.pcs.web.rest;

import org.springframework.dao.DataIntegrityViolationException;
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
import java.util.List;
import java.util.Optional;

import hu.congressline.pcs.domain.Currency;
import hu.congressline.pcs.repository.CurrencyRepository;
import hu.congressline.pcs.web.rest.util.HeaderUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class CurrencyResource {
    private static final String ENTITY_NAME = "currency";
    private static final String CURRENCY_EXISTS = "currencyexists";

    private final CurrencyRepository currencyRepository;

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/currencies")
    public ResponseEntity<Currency> create(@Valid @RequestBody Currency currency) throws URISyntaxException {
        log.debug("REST request to save Currency : {}", currency);
        currency.setCurrency(currency.getCurrency().toUpperCase());

        if (currency.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil
                .createFailureAlert(ENTITY_NAME, "idexists", "A new currency cannot already have an ID"))
                .body(null);
        } else if (currencyRepository.findCurrencyByCurrency(currency.getCurrency()).isPresent()) {
            return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, CURRENCY_EXISTS, "Currency already exists"))
                    .body(null);
        }

        Currency result = currencyRepository.save(currency);
        return ResponseEntity.created(new URI("/api/currencies/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
                .body(result);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PutMapping("/currencies")
    public ResponseEntity<Currency> update(@Valid @RequestBody Currency currency) throws URISyntaxException {
        log.debug("REST request to update Currency : {}", currency);
        currency.setCurrency(currency.getCurrency().toUpperCase());

        if (currency.getId() == null) {
            return create(currency);
        }

        final Optional<Currency> existingCurrency = currencyRepository.findOneByCurrencyIgnoreCaseAndIdNot(currency.getCurrency(), currency.getId());
        if (existingCurrency.isPresent() && (existingCurrency.get().getCurrency().equals(currency.getCurrency()))) {
            return ResponseEntity.badRequest().headers(HeaderUtil
                .createFailureAlert(ENTITY_NAME, CURRENCY_EXISTS, "Currency  already exist!"))
                .body(null);
        } else {
            Currency result = currencyRepository.save(currency);
            return ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, currency.getId().toString()))
                    .body(result);
        }
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/currencies")
    public List<Currency> getAll() {
        log.debug("REST request to get all Currencies");
        return currencyRepository.findAll();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/currencies/{id}")
    public ResponseEntity<Currency> getById(@PathVariable Long id) {
        log.debug("REST request to get Currency : {}", id);
        return currencyRepository.findById(id)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @DeleteMapping("/currencies/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete Currency : {}", id);
        try {
            currencyRepository.deleteById(id);
            return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
        } catch (DataIntegrityViolationException e) {
            log.debug("Constraint violation exception during delete operation.", e);
            return ResponseEntity.badRequest().headers(HeaderUtil.createDeleteConstraintViolationAlert(ENTITY_NAME, e)).body(null);
        }
    }

}
