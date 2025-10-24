package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

import hu.congressline.pcs.domain.ChargedService;
import hu.congressline.pcs.domain.Congress;

public interface ChargedServiceRepository extends JpaRepository<ChargedService, Long> {

    void deleteAllByRegistrationId(Long id);

    List<ChargedService> findAllByRegistrationId(Long id);

    List<ChargedService> findAllByRegistrationCongress(Congress congress);

    List<ChargedService> findAllByRegistrationCongressAndRegistrationIdIn(Congress congress, List<Long> regIdList);

    List<ChargedService> findAllByDateOfPaymentBetween(LocalDate fromDate, LocalDate toDate);

    List<ChargedService> findAllByDateOfPaymentBetweenAndRegistrationCongressProgramNumber(LocalDate fromDate, LocalDate toDate, String programNumber);

    List<ChargedService> findAllByDateOfPaymentBetweenAndTransactionId(LocalDate fromDate, LocalDate toDate, String transactionId);

    List<ChargedService> findAllByDateOfPaymentBetweenAndRegistrationCongressProgramNumberAndTransactionId(LocalDate fromDate, LocalDate toDate,
                                                                                                           String programNumber, String transactionId);
}
