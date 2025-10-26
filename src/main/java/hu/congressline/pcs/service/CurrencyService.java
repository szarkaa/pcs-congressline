package hu.congressline.pcs.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

import hu.congressline.pcs.repository.RateRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CurrencyService implements MonetaryService {

    private final RateRepository rateRepository;

    @SuppressWarnings("MissingJavadocMethod")
    public BigDecimal convertCurrencyToHuf(BigDecimal value, String currency, LocalDate validDate) {
        BigDecimal exchangeRate = getRateForDate(currency, validDate);
        if (exchangeRate.compareTo(BigDecimal.ZERO) > 0) {
            return roundUp(value.multiply(exchangeRate));
        } else {
            return BigDecimal.ZERO;
        }
    }

    @SuppressWarnings("MissingJavadocMethod")
    public BigDecimal convertCurrencyToHuf(@NonNull BigDecimal value, @NonNull BigDecimal exchangeRate) {
        if (exchangeRate.compareTo(BigDecimal.ZERO) > 0) {
            return roundUp(value.multiply(exchangeRate));
        } else {
            return BigDecimal.ZERO;
        }
    }

    @SuppressWarnings("MissingJavadocMethod")
    public BigDecimal getRateForDate(@NonNull String currency, @NonNull LocalDate validDate) {
        return rateRepository.findOneByCurrencyCurrencyAndValid(currency, validDate).map(rate -> roundUp(rate.getRate())).orElse(null);
    }
}
