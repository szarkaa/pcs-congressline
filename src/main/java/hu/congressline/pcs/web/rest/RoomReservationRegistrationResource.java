package hu.congressline.pcs.web.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;

import hu.congressline.pcs.domain.RoomReservation;
import hu.congressline.pcs.domain.RoomReservationRegistration;
import hu.congressline.pcs.service.RoomReservationRegistrationService;
import hu.congressline.pcs.service.RoomReservationService;
import hu.congressline.pcs.web.rest.util.HeaderUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class RoomReservationRegistrationResource {
    private static final String ENTITY_NAME = "roomReservationRegistration";

    private final RoomReservationRegistrationService rrrService;
    private final RoomReservationService rrService;

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/room-reservation-registrations")
    public ResponseEntity<RoomReservationRegistration> create(@RequestBody RoomReservationRegistration roomReservationRegistration) throws URISyntaxException {
        log.debug("REST request to save RoomReservationRegistration : {}", roomReservationRegistration);
        if (roomReservationRegistration.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil
                .createFailureAlert(ENTITY_NAME, "idexists", "A new roomReservationRegistration cannot already have an ID"))
                .body(null);
        }
        //todo: change this hack below an rrr vm should be come from the frontend
        RoomReservation roomReservation = rrService.getById(roomReservationRegistration.getRoomReservation().getId());
        roomReservationRegistration.setRoomReservation(roomReservation);
        RoomReservationRegistration result = rrrService.save(roomReservationRegistration);
        return ResponseEntity.created(new URI("/api/room-reservation-registrations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PutMapping("/room-reservation-registrations")
    public ResponseEntity<RoomReservationRegistration> update(@RequestBody RoomReservationRegistration roomReservationRegistration) throws URISyntaxException {
        log.debug("REST request to update RoomReservationRegistration : {}", roomReservationRegistration);
        if (roomReservationRegistration.getId() == null) {
            return create(roomReservationRegistration);
        }

        RoomReservationRegistration result = rrrService.save(roomReservationRegistration);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, roomReservationRegistration.getId().toString()))
            .body(result);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/room-reservation-registrations/{id}")
    public ResponseEntity<RoomReservationRegistration> getById(@PathVariable Long id) {
        log.debug("REST request to get RoomReservationRegistration : {}", id);
        return rrrService.findById(id)
            .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}
