package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import hu.congressline.pcs.domain.Rate;

public interface RateRepository extends JpaRepository<Rate, Long> {

    @Query("SELECT e FROM Rate e WHERE e.currency.currency = :currency AND e.valid = current_date")
    List<Rate> getRates(@Param("currency") String currency);

    Optional<Rate> findOneByCurrencyCurrencyAndValid(String currency, LocalDate valid);
}
