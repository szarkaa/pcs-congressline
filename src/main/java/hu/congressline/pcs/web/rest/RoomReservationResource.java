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
import hu.congressline.pcs.service.dto.RoomReservationDTO;
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
    public ResponseEntity<RoomReservationDTO> create(@Valid @RequestBody RoomReservationVM viewModel) throws URISyntaxException {
        log.debug("REST request to save room reservation : {}", viewModel);
        if (viewModel.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil
                .createFailureAlert(ENTITY_NAME, "idexists", NEW_ROOM_RESERVATION_HAVE_NO_ID))
                .body(null);
        }

        Set<LocalDate> noAvailableRoomDates = new TreeSet<>();
        Room room = roomRepository.findById(viewModel.getRoomId()).orElseThrow(() -> new IllegalArgumentException("Room not found by id: " + viewModel.getRoomId()));
        final Stream<LocalDate> range = Stream.iterate(viewModel.getArrivalDate(), d -> d.plusDays(1))
                .limit(ChronoUnit.DAYS.between(viewModel.getArrivalDate(), viewModel.getDepartureDate()));
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

        RoomReservationRegistration result = rrService.save(viewModel);
        return ResponseEntity.created(new URI("/api/room-reservations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(new RoomReservationDTO(result));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/room-reservations/shared")
    public ResponseEntity<RoomReservationDTO> createShared(@RequestBody SharedRoomReservationVM viewModel) throws URISyntaxException {
        log.debug("REST request to save shared room reservation: {}", viewModel);
        RoomReservation rr = rrService.getById(viewModel.getRrId());
        Registration reg = registrationService.getById(viewModel.getRegistrationId());
        RoomReservationRegistration rrr = new RoomReservationRegistration();
        rrr.setRoomReservation(rr);
        rrr.setRegistration(reg);
        rrr.setCreatedDate(LocalDate.now());
        RoomReservationRegistration result = rrrService.save(rrr);

        return ResponseEntity.created(new URI("/api/" + "room-reservations/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
                .body(new RoomReservationDTO(result));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PutMapping("/room-reservations")
    public ResponseEntity<RoomReservationDTO> update(@Valid @RequestBody RoomReservationVM viewModel) throws URISyntaxException {
        log.debug("REST request to update RoomReservation : {}", viewModel);
        if (viewModel.getId() == null) {
            return create(viewModel);
        }

        RoomReservationRegistration result = rrService.update(viewModel);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, viewModel.getId().toString()))
            .body(new RoomReservationDTO(result));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/registrations/{registrationId}/room-reservations")
    public List<RoomReservationDTO> getAllVMsByRegistrationId(@PathVariable Long registrationId) {
        log.debug("REST request to get all room reservation by registration id: {}", registrationId);
        return rrService.findAllByRegistrationId(registrationId);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/congresses/{congressId}/registrations/{registrationId}/shared-room-reservations")
    public List<SharedRoomReservationDTO> getAllSharedByRegistrationId(@PathVariable Long congressId, @PathVariable Long registrationId) {
        log.debug("REST request to get all shared room reservations by congress id: {}, registration id: {}", congressId, registrationId);
        return rrService.findAllSharedRoomRegistrationsByRegistrationId(congressId, registrationId);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/registrations/{registrationId}/room-reservations/{id}")
    public ResponseEntity<RoomReservationDTO> getByIdAndRegistrationId(@PathVariable Long id, @PathVariable Long registrationId) {
        log.debug("REST request to get room reservation by id : {}", id);
        RoomReservationRegistration rrr = rrrService.getById(id);
        return rrr != null ? new ResponseEntity<>(new RoomReservationDTO(rrr), HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/room-reservations/{id}")
    public ResponseEntity<RoomReservationDTO> getById(@PathVariable Long id) {
        log.debug("REST request to get RoomReservation : {}", id);
        return rrrService.findById(id)
            .map(result -> new ResponseEntity<>(new RoomReservationDTO(result), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @DeleteMapping("/room-reservations/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete room reservation registration : {}", id);
        try {
            rrrService.delete(id);
            return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
        } catch (DataIntegrityViolationException e) {
            log.debug("Constraint violation exception during delete operation.", e);
            return ResponseEntity.badRequest().headers(HeaderUtil.createDeleteConstraintViolationAlert(ENTITY_NAME, e)).body(null);
        }
    }

}
