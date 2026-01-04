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
import java.util.Set;

import hu.congressline.pcs.domain.BankAccount;
import hu.congressline.pcs.repository.BankAccountRepository;
import hu.congressline.pcs.service.RegistrationService;
import hu.congressline.pcs.web.rest.util.HeaderUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class BankAccountResource {
    private static final String ENTITY_NAME = "bankAccount";

    private final BankAccountRepository bankAccountRepository;
    private final RegistrationService registrationService;

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/bank-accounts")
    public ResponseEntity<BankAccount> createBankAccount(@Valid @RequestBody BankAccount bankAccount) throws URISyntaxException {
        log.debug("REST request to save BankAccount : {}", bankAccount);
        if (bankAccount.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new bankAccount cannot already have an ID")).body(null);
        }
        BankAccount result = bankAccountRepository.save(bankAccount);
        return ResponseEntity.created(new URI("/api/bank-accounts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PutMapping("/bank-accounts")
    public ResponseEntity<BankAccount> updateBankAccount(@Valid @RequestBody BankAccount bankAccount) throws URISyntaxException {
        log.debug("REST request to update BankAccount : {}", bankAccount);
        if (bankAccount.getId() == null) {
            return createBankAccount(bankAccount);
        }
        BankAccount result = bankAccountRepository.save(bankAccount);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, bankAccount.getId().toString()))
            .body(result);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/bank-accounts")
    public List<BankAccount> getAllBankAccounts() {
        log.debug("REST request to get all BankAccounts");
        return bankAccountRepository.findAll();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/registrations/{congressId}/{currency}/bank-accounts")
    public Set<BankAccount> getAllBankAccountsByRegistrationId(@PathVariable Long congressId, @PathVariable String currency) {
        log.debug("REST request to get all BankAccounts by congress id: {}, and currency: {}", congressId, currency);
        return registrationService.findBankAccountsByCongressAndCurrency(congressId, currency);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/bank-accounts/{id}")
    public ResponseEntity<BankAccount> getBankAccount(@PathVariable Long id) {
        log.debug("REST request to get BankAccount : {}", id);
        return bankAccountRepository.findById(id)
            .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @DeleteMapping("/bank-accounts/{id}")
    public ResponseEntity<Void> deleteBankAccount(@PathVariable Long id) {
        log.debug("REST request to delete BankAccount : {}", id);
        try {
            bankAccountRepository.deleteById(id);
            return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
        } catch (DataIntegrityViolationException e) {
            log.debug("Constraint violation exception during delete operation.", e);
            return ResponseEntity.badRequest().headers(HeaderUtil.createDeleteConstraintViolationAlert(ENTITY_NAME, e)).body(null);
        }
    }

}
