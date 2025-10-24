package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import hu.congressline.pcs.domain.OnlineRegistration;
import hu.congressline.pcs.domain.OnlineRegistrationOptionalService;

public interface OnlineRegistrationOptionalServiceRepository extends JpaRepository<OnlineRegistrationOptionalService, Long> {

    List<OnlineRegistrationOptionalService> findAllByRegistration(OnlineRegistration onlineReg);
}
