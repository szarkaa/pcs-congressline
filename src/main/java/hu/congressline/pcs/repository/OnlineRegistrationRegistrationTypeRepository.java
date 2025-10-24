package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import hu.congressline.pcs.domain.OnlineRegistration;
import hu.congressline.pcs.domain.OnlineRegistrationRegistrationType;

public interface OnlineRegistrationRegistrationTypeRepository extends JpaRepository<OnlineRegistrationRegistrationType, Long> {

    List<OnlineRegistrationRegistrationType> findAllByRegistration(OnlineRegistration onlineRegistration);
}
