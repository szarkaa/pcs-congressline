package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import hu.congressline.pcs.domain.AccPeople;

public interface AccPeopleRepository extends JpaRepository<AccPeople, Long> {

    List<AccPeople> findAllByRegistrationRegistrationTypeId(Long id);

    void deleteAllByRegistrationRegistrationTypeRegistrationId(Long id);
}
