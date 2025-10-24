package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import hu.congressline.pcs.domain.Currency;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {

    Optional<Currency> findCurrencyByCurrency(String currency);

    Optional<Currency> findOneByCurrencyIgnoreCaseAndIdNot(String currency, Long id);
}
