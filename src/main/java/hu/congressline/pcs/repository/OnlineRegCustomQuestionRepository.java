package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import hu.congressline.pcs.domain.OnlineRegCustomQuestion;

public interface OnlineRegCustomQuestionRepository extends JpaRepository<OnlineRegCustomQuestion, Long> {

    List<OnlineRegCustomQuestion> findAllByCongressIdOrderByQuestionOrder(Long id);

    List<OnlineRegCustomQuestion> findAllByCongressUuidAndCurrencyCurrencyOrderByQuestionOrder(String uuid, String currency);

    List<OnlineRegCustomQuestion> findAllByCongressIdAndCurrencyCurrencyOrderByQuestionOrder(Long id, String currency);

    void deleteAllByCongressId(Long id);

}
