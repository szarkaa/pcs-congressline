package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import hu.congressline.pcs.domain.OrderedOptionalService;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderedOptionalServiceRepository extends JpaRepository<OrderedOptionalService, Long> {

    List<OrderedOptionalService> findAllByRegistrationId(Long id);

    List<OrderedOptionalService> findAllByIdIn(List<Long> ids);

    void deleteAllByRegistrationId(Long id);

    @Query("select coalesce(sum(e.participant), 0) from OrderedOptionalService e where e.optionalService.id = :optionalServiceId")
    Integer getOptionalServiceTotalReservationNumber(@Param("optionalServiceId") Long optionalServiceId);

}
