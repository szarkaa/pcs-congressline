package hu.congressline.pcs.web.rest;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import hu.congressline.pcs.domain.CongressHotel;
import hu.congressline.pcs.domain.Room;
import hu.congressline.pcs.repository.CongressHotelRepository;
import hu.congressline.pcs.repository.RoomRepository;
import hu.congressline.pcs.repository.RoomReservationEntryRepository;
import hu.congressline.pcs.service.RoomService;
import hu.congressline.pcs.service.dto.RoomDTO;
import hu.congressline.pcs.service.dto.RoomReservationEntryDTO;
import hu.congressline.pcs.web.rest.util.HeaderUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class RoomResource {
    private static final String ENTITY_NAME = "room";
    private static final String ROOM_TYPE_EXISTS = "roomtypeexists";
    private static final String ROOM_TYPE_EXISTS_MSG = "Room type already exists";
    private static final String CONGRESS_HOTEL_NOT_FOUND = "CongressHotel not found with id: ";

    private final RoomService roomService;
    private final RoomRepository roomRepository;
    private final CongressHotelRepository congressHotelRepository;
    private final RoomReservationEntryRepository rreRepository;

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/rooms")
    public ResponseEntity<Room> create(@Valid @RequestBody Room room) throws URISyntaxException {
        log.debug("REST request to save Room : {}", room);
        final CongressHotel congressHotel = congressHotelRepository.findById(room.getCongressHotel().getId())
            .orElseThrow(() -> new IllegalArgumentException(CONGRESS_HOTEL_NOT_FOUND + room.getCongressHotel().getId()));

        if (room.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil
                .createFailureAlert(ENTITY_NAME, "idexists", "A new room cannot already have an ID"))
                .body(null);
        } else if (roomRepository.findOneByRoomTypeAndCongressHotelId(room.getRoomType(), congressHotel.getId()).isPresent()) {
            return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, ROOM_TYPE_EXISTS, ROOM_TYPE_EXISTS_MSG))
                    .body(null);
        }

        Room result = roomRepository.save(room);
        return ResponseEntity.created(new URI("/api/rooms/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PutMapping("/rooms")
    public ResponseEntity<Room> update(@Valid @RequestBody Room room) throws URISyntaxException {
        log.debug("REST request to update Room : {}", room);
        final CongressHotel congressHotel = congressHotelRepository.findById(room.getCongressHotel().getId())
            .orElseThrow(() -> new IllegalArgumentException(CONGRESS_HOTEL_NOT_FOUND + room.getCongressHotel().getId()));

        if (room.getId() == null) {
            return create(room);
        } else if (roomRepository.findOneByRoomTypeAndCongressHotelIdAndIdNot(room.getRoomType(), congressHotel.getId(), room.getId()).isPresent()) {
            return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, ROOM_TYPE_EXISTS, ROOM_TYPE_EXISTS_MSG))
                    .body(null);
        }

        Room result = roomRepository.save(room);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, room.getId().toString()))
            .body(result);
    }

    /*
    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/rooms")
    public List<RoomDTO> getAll() {
        log.debug("REST request to get all Rooms");
        return roomRepository.findAll().stream().map(RoomDTO::new).collect(Collectors.toList());
    }
    */

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/congress-hotel/{id}/rooms")
    public List<RoomDTO> getAllByCongressHotelId(@PathVariable Long id) {
        log.debug("REST request to get all Rooms by congressHotel id: {}", id);
        final List<Room> roomList = roomRepository.findAllByCongressHotelId(id);
        final List<RoomDTO> roomDTOList = roomList.stream().map(RoomDTO::new).collect(Collectors.toList());
        roomDTOList.forEach(roomDTO -> {
            roomDTO.setReservations(rreRepository.findAllByRoomId(roomDTO.getId()).stream().map(RoomReservationEntryDTO::new)
                .filter(dto -> !dto.getReserved().equals(0)).collect(Collectors.toList()));
            roomDTO.getReservations().sort(Comparator.comparing(RoomReservationEntryDTO::getReservationDate));
        });

        return roomDTOList;
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/congress/{id}/rooms")
    public List<RoomDTO> getAllByCongressId(@PathVariable Long id) {
        log.debug("REST request to get all Rooms by congress id: {}", id);
        final List<Room> roomList = roomRepository.findAllByCongressHotelCongressId(id);
        final List<RoomDTO> roomDTOList = roomList.stream().map(RoomDTO::new).collect(Collectors.toList());
        roomDTOList.forEach(roomDTO -> {
            roomDTO.setReservations(rreRepository.findAllByRoomId(roomDTO.getId()).stream().map(RoomReservationEntryDTO::new)
                .filter(dto -> !dto.getReserved().equals(0)).collect(Collectors.toList()));
        });
        return roomDTOList;

    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/rooms/{id}")
    public ResponseEntity<RoomDTO> getById(@PathVariable Long id) {
        log.debug("REST request to get Room : {}", id);
        return roomRepository.findById(id)
            .map(r -> {
                RoomDTO dto = new RoomDTO(r);
                dto.setReservations(rreRepository.findAllByRoomId(r.getId()).stream().map(RoomReservationEntryDTO::new)
                    .filter(rdto -> !rdto.getReserved().equals(0)).collect(Collectors.toList()));
                return dto;
            })
            .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @DeleteMapping("/rooms/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete Room : {}", id);
        try {
            roomService.delete(id);
            return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
        } catch (DataIntegrityViolationException e) {
            log.debug("Constraint violation exception during delete operation.", e);
            return ResponseEntity.badRequest().headers(HeaderUtil.createDeleteConstraintViolationAlert(ENTITY_NAME, e)).body(null);
        }
    }

}
