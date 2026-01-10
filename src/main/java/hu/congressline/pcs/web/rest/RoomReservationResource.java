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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import hu.congressline.pcs.domain.Registration;
import hu.congressline.pcs.domain.Room;
import hu.congressline.pcs.domain.RoomReservation;
import hu.congressline.pcs.domain.RoomReservationEntry;
import hu.congressline.pcs.domain.RoomReservationRegistration;
import hu.congressline.pcs.repository.RoomRepository;
import hu.congressline.pcs.repository.RoomReservationEntryRepository;
import hu.congressline.pcs.service.RegistrationService;
import hu.congressline.pcs.service.RoomReservationRegistrationService;
import hu.congressline.pcs.service.RoomReservationService;
import hu.congressline.pcs.service.dto.SharedRoomReservationDTO;
import hu.congressline.pcs.web.rest.util.HeaderUtil;
import hu.congressline.pcs.web.rest.vm.RoomReservationVM;
import hu.congressline.pcs.web.rest.vm.SharedRoomReservationVM;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class RoomReservationResource {
    private static final String ENTITY_NAME = "roomReservation";
    private static final String NEW_ROOM_RESERVATION_HAVE_NO_ID = "A new roomReservation cannot already have an ID";

    private final RoomReservationService rrService;
    private final RoomRepository roomRepository;
    private final RoomReservationRegistrationService rrrService;
    private final RegistrationService registrationService;
    private final RoomReservationEntryRepository rreRepository;

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/room-reservations")
    public ResponseEntity<RoomReservationVM> createReservation(@Valid @RequestBody RoomReservationVM roomReservation) throws URISyntaxException {
        log.debug("REST request to save RoomReservation : {}", roomReservation);
        if (roomReservation.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil
                .createFailureAlert(ENTITY_NAME, "idexists", NEW_ROOM_RESERVATION_HAVE_NO_ID))
                .body(null);
        }

        Set<LocalDate> noAvailableRoomDates = new TreeSet<>();
        Room room = roomRepository.findById(roomReservation.getRoomId()).orElseThrow(() -> new IllegalArgumentException("Room not found with id: " + roomReservation.getRoomId()));
        final Stream<LocalDate> range = Stream.iterate(roomReservation.getArrivalDate(), d -> d.plusDays(1))
                .limit(ChronoUnit.DAYS.between(roomReservation.getArrivalDate(), roomReservation.getDepartureDate()));
        range.forEach(localDate -> {
            final Optional<RoomReservationEntry> entry = rreRepository.findAllByRoomId(room.getId()).stream()
                .filter(e -> e.getReservationDate().isEqual(localDate)).findFirst();
            if (entry.isPresent() && room.getQuantity().equals(entry.get().getReserved())) {
                noAvailableRoomDates.add(localDate);
            }
        });

        if (!noAvailableRoomDates.isEmpty()) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(
                noAvailableRoomDates.stream().map(d -> DateTimeFormatter.ofPattern("yyyy-MM-dd").format(d)).collect(Collectors.joining(", ")),
                    "noAvailableRoom", NEW_ROOM_RESERVATION_HAVE_NO_ID, false)).body(null);
        }

        RoomReservationVM result = rrService.save(roomReservation);
        return ResponseEntity.created(new URI("/api/room-reservations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/room-reservations/shared")
    public ResponseEntity<RoomReservationVM> createSharedReservation(@RequestBody SharedRoomReservationVM roomReservation) throws URISyntaxException {
        log.debug("REST request to save Shared RoomReservation: {}", roomReservation);
        RoomReservation rr = rrService.getById(roomReservation.getRrId());
        Registration reg = registrationService.getById(roomReservation.getRegistrationId());
        RoomReservationRegistration rrr = new RoomReservationRegistration();
        rrr.setRoomReservation(rr);
        rrr.setRegistration(reg);
        rrr.setCreatedDate(LocalDate.now());
        RoomReservationRegistration result = rrrService.save(rrr);

        return ResponseEntity.created(new URI("/api/" + "room-reservations/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
                .body(new RoomReservationVM(result));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PutMapping("/room-reservations")
    public ResponseEntity<RoomReservationVM> updateReservation(@Valid @RequestBody RoomReservationVM roomReservation) throws URISyntaxException {
        log.debug("REST request to update RoomReservation : {}", roomReservation);
        if (roomReservation.getId() == null) {
            return createReservation(roomReservation);
        }

        RoomReservationVM result = rrService.update(roomReservation);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, roomReservation.getId().toString()))
            .body(result);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/registrations/{registrationId}/room-reservation-vms")
    public List<RoomReservationVM> getAllVMsByRegistrationId(@PathVariable Long registrationId) {
        log.debug("REST request to get all RoomReservationVMs by registration id: {}", registrationId);
        return rrService.findAllVMByRegistrationId(registrationId);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/registrations/{registrationId}/room-reservations")
    public List<RoomReservation> getAllByRegistrationId(@PathVariable Long registrationId) {
        log.debug("REST request to get all RoomReservations by registration id: {}", registrationId);
        return rrService.findAllByRegistrationId(registrationId);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/congresses/{congressId}/registrations/{registrationId}/shared-room-reservation-vms")
    public List<SharedRoomReservationDTO> getAllSharedByRegistrationId(@PathVariable Long congressId, @PathVariable Long registrationId) {
        log.debug("REST request to get all RoomReservations by congress id: {}, registration id: {}", congressId, registrationId);
        return rrService.findAllSharedRoomRegistrationsByRegistrationId(congressId, registrationId);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/registrations/{registrationId}/room-reservations/{id}")
    public ResponseEntity<RoomReservationVM> getVMByIdAndRegistrationId(@PathVariable Long id, @PathVariable Long registrationId) {
        log.debug("REST request to get RoomReservationVM : {}", id);
        RoomReservationRegistration rrr = rrrService.getById(id);
        return rrr != null ? new ResponseEntity<>(new RoomReservationVM(rrr), HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/room-reservations/{id}")
    public ResponseEntity<RoomReservationRegistration> getById(@PathVariable Long id) {
        log.debug("REST request to get RoomReservation : {}", id);
        return rrrService.findById(id)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @DeleteMapping("/room-reservations/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete RoomReservationRegistration : {}", id);
        try {
            rrrService.delete(id);
            return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
        } catch (DataIntegrityViolationException e) {
            log.debug("Constration violation exception during delete operation.", e);
            return ResponseEntity.badRequest().headers(HeaderUtil.createDeleteConstraintViolationAlert(ENTITY_NAME, e)).body(null);
        }

    }

}
