package hu.congressline.pcs.service;

import hu.congressline.pcs.domain.enumeration.OnlineVisibility;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import hu.congressline.pcs.domain.Room;
import hu.congressline.pcs.repository.RoomRepository;
import hu.congressline.pcs.repository.RoomReservationEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomReservationEntryRepository rreRepository;

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Optional<Room> findById(Long id) {
        log.debug("Request to find Room : {}", id);
        return roomRepository.findById(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Room getById(Long id) {
        log.debug("Request to get Room : {}", id);
        return roomRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Room not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Room> findAllOnlineRooms(String congressUuid, String currency) {
        log.debug("Request to find all online available Room congressUuid: {}, currency: {}", congressUuid, currency);
        return roomRepository.findByOnlineVisibilityAndCongressHotelCongressUuidAndCurrencyCurrency(OnlineVisibility.VISIBLE, congressUuid, currency.toUpperCase());
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void delete(Long roomId) {
        rreRepository.deleteAllByRoomId(roomId);
        roomRepository.deleteById(roomId);
    }
}
