package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

import hu.congressline.pcs.domain.RoomReservationEntry;

public interface RoomReservationEntryRepository extends JpaRepository<RoomReservationEntry, Long> {

    List<RoomReservationEntry> findAllByRoomId(@Param("id") Long roomId);

    void deleteAllByRoomId(@Param("id") Long roomId);

}
