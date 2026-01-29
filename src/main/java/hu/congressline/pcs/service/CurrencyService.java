package hu.congressline.pcs.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import hu.congressline.pcs.domain.Currency;
import hu.congressline.pcs.repository.CurrencyRepository;
import hu.congressline.pcs.repository.RateRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CurrencyService implements MonetaryService {

    private final RateRepository rateRepository;
    private final CurrencyRepository currencyRepository;

    @SuppressWarnings("MissingJavadocMethod")
    public BigDecimal convertCurrencyToHuf(BigDecimal value, String currency, LocalDate validDate) {
        BigDecimal exchangeRate = getRateForDate(currency, validDate);
        if (exchangeRate.compareTo(BigDecimal.ZERO) > 0) {
            return roundUp(value.multiply(exchangeRate), 2);
        } else {
            return BigDecimal.ZERO;
        }
    }

    @SuppressWarnings("MissingJavadocMethod")
    public BigDecimal convertCurrencyToHuf(@NonNull BigDecimal value, @NonNull BigDecimal exchangeRate) {
        if (exchangeRate.compareTo(BigDecimal.ZERO) > 0) {
            return roundUp(value.multiply(exchangeRate), 2);
        } else {
            return BigDecimal.ZERO;
        }
    }

    @SuppressWarnings("MissingJavadocMethod")
    public BigDecimal getRateForDate(@NonNull String currency, @NonNull LocalDate validDate) {
        return rateRepository.findOneByCurrencyCurrencyAndValid(currency, validDate).map(rate -> roundUp(rate.getRate(), 2)).orElse(null);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public Set<Currency> getAllByIds(@NonNull Set<Long> currenciesIds) {
        return currencyRepository.findAllByIdIn(currenciesIds);
    }
}
