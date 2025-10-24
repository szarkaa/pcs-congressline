package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import hu.congressline.pcs.domain.Room;
import hu.congressline.pcs.domain.enumeration.OnlineVisibility;

public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findAllByCongressHotelId(Long id);

    List<Room> findAllByCongressHotelCongressId(Long id);

    List<Room> findByOnlineVisibilityAndCongressHotelCongressUuidAndCurrencyCurrency(OnlineVisibility visibility, String uuid, String currency);

    Optional<Room> findOneByRoomTypeAndCongressHotelId(String roomType, Long congressId);

    Optional<Room> findOneByRoomTypeAndCongressHotelIdAndIdNot(String roomType, Long congressId, Long id);
}
