package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import hu.congressline.pcs.domain.Congress;
import hu.congressline.pcs.domain.OnlineRegistrationCustomAnswer;

public interface OnlineRegistrationCustomAnswerRepository extends JpaRepository<OnlineRegistrationCustomAnswer, Long> {

    List<OnlineRegistrationCustomAnswer> findAllByOnlineRegistrationIdOrderByQuestionQuestionOrderAsc(Long id);

    List<OnlineRegistrationCustomAnswer> findAllByRegistrationId(Long id);

    void deleteAllByOnlineRegistrationId(Long id);

    void deleteAllByRegistrationId(Long id);

    List<OnlineRegistrationCustomAnswer> findAllByRegistrationCongress(Congress congress);

}
