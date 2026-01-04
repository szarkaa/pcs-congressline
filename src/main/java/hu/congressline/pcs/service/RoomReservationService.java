package hu.congressline.pcs.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import hu.congressline.pcs.domain.Congress;
import hu.congressline.pcs.domain.Hotel;
import hu.congressline.pcs.domain.Registration;
import hu.congressline.pcs.domain.Room;
import hu.congressline.pcs.domain.RoomReservation;
import hu.congressline.pcs.domain.RoomReservationEntry;
import hu.congressline.pcs.domain.RoomReservationRegistration;
import hu.congressline.pcs.repository.RegistrationRepository;
import hu.congressline.pcs.repository.RoomReservationEntryRepository;
import hu.congressline.pcs.repository.RoomReservationRegistrationRepository;
import hu.congressline.pcs.repository.RoomReservationRepository;
import hu.congressline.pcs.service.dto.SharedRoomReservationDTO;
import hu.congressline.pcs.web.rest.vm.RoomReservationVM;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class RoomReservationService {

    private final RegistrationRepository registrationRepository;
    private final RoomReservationRepository rrRepository;
    private final RoomReservationRegistrationRepository rrrRepository;
    private final DiscountService discountService;
    private final RoomService roomService;
    private final RoomReservationEntryRepository rreRepository;

    @SuppressWarnings("MissingJavadocMethod")
    public RoomReservationVM save(RoomReservationVM vm) {
        log.debug("Request to save RoomReservation by vm : {}", vm);
        RoomReservation rr = new RoomReservation();
        rr.setRoom(roomService.getById(vm.getRoomId()));
        rr.setArrivalDate(vm.getArrivalDate());
        rr.setDepartureDate(vm.getDepartureDate());
        rr.setShared(vm.getShared());
        rr.setRoomReservationRegistrations(new ArrayList<>());
        final RoomReservation rrResult = rrRepository.save(rr);

        final Registration registration = registrationRepository.findById(vm.getRegistrationId())
                .orElseThrow(() -> new IllegalArgumentException("Registration not found with id: " + vm.getRegistrationId()));
        RoomReservationRegistration rrr = new RoomReservationRegistration();
        rrr.setRegistration(registration);
        rrr.setRoomReservation(rr);
        rrr.setPayingGroupItem(vm.getPayingGroupItem());
        rrr.setComment(vm.getComment());
        RoomReservationRegistration rrrResult = rrrRepository.save(rrr);

        rr.getRoomReservationRegistrations().add(rrr);
        final Stream<LocalDate> range = Stream.iterate(rrResult.getArrivalDate(), d -> d.plusDays(1))
                .limit(ChronoUnit.DAYS.between(rrResult.getArrivalDate(), rrResult.getDepartureDate()));
        range.forEach(localDate -> increaseRoomReservedNumber(rrResult.getRoom(), localDate));
        return new RoomReservationVM(rrrResult);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public RoomReservation save(RoomReservation roomReservation) {
        log.debug("Request to save RoomReservation by entity: {}", roomReservation);
        return rrRepository.save(roomReservation);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Optional<RoomReservation> findById(Long id) {
        log.debug("Request to find RoomReservation : {}", id);
        return rrRepository.findById(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public RoomReservation getById(Long id) {
        log.debug("Request to get RoomReservation : {}", id);
        return rrRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("RoomReservation not found with id: " + id));
    }

    @SuppressWarnings("MissingJavadocMethod")
    public RoomReservationVM update(RoomReservationVM vm) {
        log.debug("Request to update RoomReservation : {}", vm);
        RoomReservationRegistration rrr = rrrRepository.findById(vm.getId())
                .orElseThrow(() -> new IllegalArgumentException("RoomReservationRegistration not found with id: " + vm.getId()));
        // decrease the room reservations on dates according to the old date values
        Stream<LocalDate> range = Stream.iterate(rrr.getRoomReservation().getArrivalDate(), d -> d.plusDays(1))
                .limit(ChronoUnit.DAYS.between(rrr.getRoomReservation().getArrivalDate(), rrr.getRoomReservation().getDepartureDate()));
        range.forEach(localDate -> decreaseRoomReservedNumber(rrr.getRoomReservation().getRoom(), localDate));

        rrr.getRoomReservation().setRoom(roomService.getById(vm.getRoomId()));
        rrr.getRoomReservation().setArrivalDate(vm.getArrivalDate());
        rrr.getRoomReservation().setDepartureDate(vm.getDepartureDate());
        rrr.getRoomReservation().setShared(vm.getShared());
        rrr.setPayingGroupItem(vm.getPayingGroupItem());
        rrr.setComment(vm.getComment());
        RoomReservationRegistration result = rrrRepository.save(rrr);
        // increase the room reservations on dates according to the new date values
        range = Stream.iterate(result.getRoomReservation().getArrivalDate(), d -> d.plusDays(1))
                .limit(ChronoUnit.DAYS.between(result.getRoomReservation().getArrivalDate(), result.getRoomReservation().getDepartureDate()));
        range.forEach(localDate -> increaseRoomReservedNumber(result.getRoomReservation().getRoom(), localDate));

        return new RoomReservationVM(result);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<RoomReservationVM> findAllVMByRegistrationId(final Long registrationId) {
        log.debug("Request to get all RoomReservationVMs");
        return rrrRepository.findAllByRegistrationId(registrationId).stream().map(rrr -> {
            RoomReservationVM vm = new RoomReservationVM(rrr);
            if (rrr.getPayingGroupItem() != null) {
                vm.setPriceWithDiscount(discountService.getRoomReservationPriceWithDiscount(rrr.getPayingGroupItem(), rrr));
            } else {
                vm.setPriceWithDiscount(vm.getChargeableItemPrice());
            }
            return vm;
        }).collect(Collectors.toList());
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<RoomReservation> findAllByRegistrationId(final Long registrationId) {
        log.debug("Request to get all RoomReservations");
        return rrRepository.findAllByRegistrationId(registrationId);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<SharedRoomReservationDTO> findAllSharedRoomRegistrationsByRegistrationId(final Long congressId, final Long registrationId) {
        log.debug("Request to get all shared RoomReservationVMs");
        return rrRepository.findAllSharedOnesByRegistrationId(congressId, registrationId).stream().map(SharedRoomReservationDTO::new).collect(Collectors.toList());
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<RoomReservation> findAllRoomReservationByHotelAndCongress(final Congress congress, final Hotel hotel) {
        log.debug("Request to get all Room Reservation by congressId: {}, and hotel: {}", congress, hotel);
        return rrRepository.findAllByCongressAndHotel(congress, hotel);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void deleteAllByRegistrationId(Long registrationId) {
        log.debug("Request to delete all RoomReservationRegistration and RoomReservation by registration id : {}", registrationId);
        final List<RoomReservation> roomReservations = rrRepository.findAllByRegistrationId(registrationId);
        roomReservations.forEach(rr -> {
            if (rr.getRoomReservationRegistrations().size() == 1) {
                final Stream<LocalDate> range = Stream.iterate(rr.getArrivalDate(), d -> d.plusDays(1))
                        .limit(ChronoUnit.DAYS.between(rr.getArrivalDate(), rr.getDepartureDate()));
                range.forEach(localDate -> decreaseRoomReservedNumber(rr.getRoom(), localDate));
                rrRepository.deleteById(rr.getId());
            }
        });

        rrrRepository.deleteAllByRegistrationId(registrationId);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void increaseRoomReservedNumber(Room room, LocalDate reservationDate) {
        RoomReservationEntry entry = rreRepository.findAllByRoomId(room.getId()).stream()
                .filter(rre -> rre.getReservationDate().isEqual(reservationDate)).findFirst().orElse(new RoomReservationEntry());
        entry.setRoom(room);
        entry.setReservationDate(reservationDate);
        entry.setReserved(entry.getReserved() + 1);
        rreRepository.save(entry);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void decreaseRoomReservedNumber(Room room, LocalDate reservationDate) {
        RoomReservationEntry entry = rreRepository.findAllByRoomId(room.getId()).stream()
                .filter(rre -> rre.getReservationDate().isEqual(reservationDate)).findFirst().orElse(new RoomReservationEntry());
        entry.setRoom(room);
        entry.setReservationDate(reservationDate);
        entry.setReserved(Math.max(0, entry.getReserved() - 1));
        rreRepository.save(entry);
    }
}
