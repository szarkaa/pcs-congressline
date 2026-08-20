package hu.congressline.pcs.service;

import hu.congressline.pcs.domain.OnlineRegistration;
import hu.congressline.pcs.repository.OnlineRegistrationRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import hu.congressline.pcs.domain.Congress;
import hu.congressline.pcs.domain.Hotel;
import hu.congressline.pcs.domain.Registration;
import hu.congressline.pcs.domain.RoomReservation;
import hu.congressline.pcs.domain.RoomReservationRegistration;
import hu.congressline.pcs.repository.RegistrationRepository;
import hu.congressline.pcs.repository.RoomReservationRegistrationRepository;
import hu.congressline.pcs.repository.RoomReservationRepository;
import hu.congressline.pcs.service.dto.RoomReservationDTO;
import hu.congressline.pcs.service.dto.SharedRoomReservationDTO;
import hu.congressline.pcs.web.rest.vm.RoomReservationVM;
import lombok.NonNull;
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
    private final OnlineRegistrationRepository onlineRegistrationRepository;
    private final DiscountService discountService;
    private final RoomService roomService;
    private final PayingGroupItemService payingGroupItemService;

    @SuppressWarnings("MissingJavadocMethod")
    public RoomReservationRegistration save(RoomReservationVM viewModel) {
        log.debug("Request to save room reservation by viewModel : {}", viewModel);
        RoomReservation rr = new RoomReservation();
        rr.setRoom(roomService.getById(viewModel.getRoomId()));
        rr.setArrivalDate(viewModel.getArrivalDate());
        rr.setDepartureDate(viewModel.getDepartureDate());
        rr.setShared(viewModel.getShared());
        rr.setRoomReservationRegistrations(new ArrayList<>());
        final RoomReservation rrResult = rrRepository.save(rr);
        final Registration registration = registrationRepository.findById(viewModel.getRegistrationId())
            .orElseThrow(() -> new IllegalArgumentException("Registration not found by id: " + viewModel.getRegistrationId()));
        RoomReservationRegistration rrr = new RoomReservationRegistration();
        rrr.setRegistration(registration);
        rrr.setRoomReservation(rr);
        rrr.setPayingGroupItem(viewModel.getPayingGroupItemId() != null ? payingGroupItemService.getById(viewModel.getPayingGroupItemId()) : null);
        rrr.setComment(viewModel.getComment());
        RoomReservationRegistration rrrResult = rrrRepository.save(rrr);
        rr.getRoomReservationRegistrations().add(rrr);
        return rrrResult;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public RoomReservation save(RoomReservation roomReservation) {
        log.debug("Request to save room reservation: {}", roomReservation);
        return rrRepository.save(roomReservation);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public RoomReservationRegistration update(RoomReservationVM viewModel) {
        log.debug("Request to update room reservation by rrr id: {}", viewModel.getId());
        RoomReservationRegistration rrr = rrrRepository.findById(viewModel.getId())
            .orElseThrow(() -> new IllegalArgumentException("Room reservation registration not found by id: " + viewModel.getRegistrationId()));

        rrr.getRoomReservation().setRoom(roomService.getById(viewModel.getRoomId()));
        rrr.getRoomReservation().setArrivalDate(viewModel.getArrivalDate());
        rrr.getRoomReservation().setDepartureDate(viewModel.getDepartureDate());
        rrr.getRoomReservation().setShared(viewModel.getShared());
        rrr.setPayingGroupItem(viewModel.getPayingGroupItemId() != null ? payingGroupItemService.getById(viewModel.getPayingGroupItemId()) : null);
        rrr.setComment(viewModel.getComment());
        return rrrRepository.save(rrr);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Optional<RoomReservation> findById(Long id) {
        log.debug("Request to find room reservation : {}", id);
        return id != null ? rrRepository.findById(id) : Optional.empty();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public RoomReservation getById(Long id) {
        log.debug("Request to get room reservation : {}", id);
        return rrRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Room reservation not found by id: " + id));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<LocalDate> getAvailableReservedDates(Long roomId) {
        final List<LocalDate> reservationDates = rrRepository.findAllByRoomId(roomId).stream()
            .flatMap(reservation ->
                Stream.iterate(reservation.getArrivalDate(), date -> date.plusDays(1))
                    .limit(ChronoUnit.DAYS.between(reservation.getArrivalDate(), reservation.getDepartureDate()))
            )
            .distinct()
            .toList();

        final List<LocalDate> onlineRegDates = onlineRegistrationRepository.findAllByRoomId(roomId).stream()
            .flatMap(onlineRegistration ->
                Stream.iterate(onlineRegistration.getArrivalDate(), date -> date.plusDays(1))
                    .limit(ChronoUnit.DAYS.between(onlineRegistration.getArrivalDate(), onlineRegistration.getDepartureDate()))
            )
            .distinct()
            .toList();
        Set<LocalDate> dateSet = new HashSet<>(reservationDates);
        dateSet.addAll(onlineRegDates);
        return dateSet.stream().sorted().toList();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Long getReservationCountByRoomId(Long roomId, LocalDate date) {
        return rrRepository.countReservationsByRoomAndDate(roomId, date) + onlineRegistrationRepository.countReservationsByRoomAndDate(roomId, date);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Map<LocalDate, Long> getAllReservationCountByRoomId(Long roomId) {
        log.debug("Request to get room reservation numbers for dates by room id: {}", roomId);
        Map<LocalDate, Long> reservationNumbers = new LinkedHashMap<>();
        getAvailableReservedDates(roomId).forEach(date -> {
            reservationNumbers.put(date, getReservationCountByRoomId(roomId, date));
        });
        return reservationNumbers;
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<RoomReservationDTO> findAllByRegistrationId(final Long registrationId) {
        log.debug("Request to get all RoomReservationVMs");
        return rrrRepository.findAllByRegistrationId(registrationId).stream().map(rrr -> {
            RoomReservationDTO dto = new RoomReservationDTO(rrr);
            if (rrr.getPayingGroupItem() != null) {
                dto.setPriceWithDiscount(discountService.getRoomReservationPriceWithDiscount(rrr.getPayingGroupItem(), rrr));
            } else {
                dto.setPriceWithDiscount(dto.getChargeableItemPrice());
            }
            return dto;
        }).collect(Collectors.toList());
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<SharedRoomReservationDTO> findAllSharedRoomRegistrationsByRegistrationId(final Long congressId, final Long registrationId) {
        log.debug("Request to get all shared RoomReservationVMs");
        return rrRepository.findAllSharedOnesByRegistrationId(congressId, registrationId).stream().map(SharedRoomReservationDTO::new).collect(Collectors.toList());
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<RoomReservation> findAllRoomReservationByHotelAndCongress(@NonNull Congress congress, @NonNull Hotel hotel) {
        log.debug("Request to get all room reservation by congress id: {}, and hotel id: {}", congress.getId(), hotel.getId());
        return rrRepository.findAllByCongressAndHotel(congress, hotel);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void deleteAllByRegistrationId(Long registrationId) {
        log.debug("Request to delete all room reservation registration and room reservation by registration id : {}", registrationId);
        final List<RoomReservation> roomReservations = rrRepository.findAllByRegistrationId(registrationId);
        roomReservations.forEach(rr -> {
            if (rr.getRoomReservationRegistrations().size() == 1) {
                rrRepository.deleteById(rr.getId());
            }
        });

        rrrRepository.deleteAllByRegistrationId(registrationId);
    }
}
