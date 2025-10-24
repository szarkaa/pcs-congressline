package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import hu.congressline.pcs.domain.RoomReservationRegistration;

public interface RoomReservationRegistrationRepository extends JpaRepository<RoomReservationRegistration, Long> {

    List<RoomReservationRegistration> findAllByRegistrationId(Long registrationId);

    List<RoomReservationRegistration> findAllByIdIn(List<Long> ids);

    void deleteAllByRegistrationId(Long registrationId);

}
