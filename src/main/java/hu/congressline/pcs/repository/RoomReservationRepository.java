package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import hu.congressline.pcs.domain.Congress;
import hu.congressline.pcs.domain.Hotel;
import hu.congressline.pcs.domain.RoomReservation;

public interface RoomReservationRepository extends JpaRepository<RoomReservation, Long> {

    @Query("select e.roomReservation from RoomReservationRegistration e where e.registration.id = :id")
    List<RoomReservation> findAllByRegistrationId(@Param("id") Long registrationId);

    @Query("select e from RoomReservation e where e.room.congressHotel.congress = :congress and e.room.congressHotel.hotel = :hotel")
    List<RoomReservation> findAllByCongressAndHotel(@Param("congress") Congress congress, @Param("hotel") Hotel hotel);

    @Query("select distinct e.roomReservation from RoomReservationRegistration e WHERE e.roomReservation.shared = true and "
            + "e.roomReservation.room.congressHotel.congress.id = :congressId and e.roomReservation.room.bed > (select count(r) from "
            + "RoomReservationRegistration r where r.roomReservation = e.roomReservation) and not exists "
            + "(select rrr.roomReservation from RoomReservationRegistration rrr where rrr.roomReservation = e.roomReservation and rrr.registration.id = :registrationId)")
    List<RoomReservation> findAllSharedOnesByRegistrationId(@Param("congressId") Long congressId, @Param("registrationId") Long registrationId);

}
