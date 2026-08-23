package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import hu.congressline.pcs.domain.OnlineRegistration;
import hu.congressline.pcs.domain.OnlineRegistrationOptionalService;

public interface OnlineRegistrationOptionalServiceRepository extends JpaRepository<OnlineRegistrationOptionalService, Long> {

    List<OnlineRegistrationOptionalService> findAllByRegistration(OnlineRegistration onlineReg);

    @Query("select coalesce(sum(e.participant), 0) from OnlineRegistrationOptionalService e where e.optionalService.id = :optionalServiceId")
    Integer countOrderedOptionalServiceParticipants(@Param("optionalServiceId") Long optionalServiceId);
}
