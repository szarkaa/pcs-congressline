package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import hu.congressline.pcs.domain.AccPeopleOnline;
import hu.congressline.pcs.domain.OnlineRegistrationRegistrationType;

public interface AccPeopleOnlineRepository extends JpaRepository<AccPeopleOnline, Long> {

    List<AccPeopleOnline> findAllByOnlineRegistrationRegistrationType(OnlineRegistrationRegistrationType onlineRegistrationRegistrationType);
}
