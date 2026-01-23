package hu.congressline.pcs.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import hu.congressline.pcs.domain.Room;
import hu.congressline.pcs.domain.enumeration.OnlineVisibility;
import hu.congressline.pcs.repository.CurrencyRepository;
import hu.congressline.pcs.repository.RoomRepository;
import hu.congressline.pcs.repository.RoomReservationEntryRepository;
import hu.congressline.pcs.repository.VatInfoRepository;
import hu.congressline.pcs.web.rest.vm.RoomVM;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class RoomService {

    private final RoomRepository repository;
    private final RoomReservationEntryRepository rreRepository;
    private final VatInfoRepository vatInfoRepository;
    private final CurrencyRepository currencyRepository;
    private final CongressHotelService congressHotelService;

    @SuppressWarnings("MissingJavadocMethod")
    public Room save(Room room) {
        log.debug("Request to save room : {}", room);
        return repository.save(room);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public Room save(@NonNull RoomVM viewModel) {
        Room room = viewModel.getId() != null ? getById(viewModel.getId()) : new Room();
        room.update(viewModel);
        room.setVatInfo(viewModel.getVatInfoId() != null ? vatInfoRepository.findById(viewModel.getVatInfoId()).orElse(null) : null);
        room.setCurrency(viewModel.getCurrencyId() != null ? currencyRepository.findById(viewModel.getCurrencyId()).orElse(null) : null);
        if (room.getCongressHotel() == null) {
            room.setCongressHotel(congressHotelService.getById(viewModel.getCongressHotelId()));
        }
        return repository.save(room);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Optional<Room> findById(Long id) {
        log.debug("Request to find Room : {}", id);
        return repository.findById(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Room getById(Long id) {
        log.debug("Request to get Room : {}", id);
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Room not found with id: " + id));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<Room> findAllOnlineRooms(String congressUuid, String currency) {
        log.debug("Request to find all online available Room congressUuid: {}, currency: {}", congressUuid, currency);
        return repository.findByOnlineVisibilityAndCongressHotelCongressUuidAndCurrencyCurrency(OnlineVisibility.VISIBLE, congressUuid, currency.toUpperCase());
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Optional<Room> findOneByRoomTypeAndCongressHotelId(@NotNull String roomType, @NotNull Long congressHotelId) {
        return repository.findOneByRoomTypeAndCongressHotelId(roomType, congressHotelId);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Optional<Room> findOneByRoomTypeAndCongressHotelIdAndIdNot(@NotNull String roomType, @NotNull Long congressHotelId, @NotNull Long roomId) {
        return repository.findOneByRoomTypeAndCongressHotelIdAndIdNot(roomType, congressHotelId, roomId);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<Room> findAllByCongressHotelId(Long id) {
        return repository.findAllByCongressHotelId(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<Room> findAllByCongressId(Long id) {
        return repository.findAllByCongressHotelCongressId(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void delete(Long roomId) {
        rreRepository.deleteAllByRoomId(roomId);
        repository.deleteById(roomId);
    }
}
