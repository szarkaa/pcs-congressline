package hu.congressline.pcs.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import hu.congressline.pcs.domain.ChargeableItemInvoiceHistory;
import hu.congressline.pcs.domain.GroupDiscountInvoiceHistory;
import hu.congressline.pcs.domain.RoomReservation;
import hu.congressline.pcs.domain.RoomReservationRegistration;
import hu.congressline.pcs.repository.ChargeableItemInvoiceHistoryRepository;
import hu.congressline.pcs.repository.GroupDiscountInvoiceHistoryRepository;
import hu.congressline.pcs.repository.RoomReservationRegistrationRepository;
import hu.congressline.pcs.repository.RoomReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class RoomReservationRegistrationService {

    private final RoomReservationRegistrationRepository roomReservationRegistrationRepository;
    private final RoomReservationRepository roomReservationRepository;
    private final RoomReservationService roomReservationService;
    private final ChargeableItemInvoiceHistoryRepository ciihRepository;
    private final GroupDiscountInvoiceHistoryRepository gdihRepository;

    @SuppressWarnings("MissingJavadocMethod")
    public RoomReservationRegistration save(RoomReservationRegistration roomReservationRegistration) {
        log.debug("Request to save RoomReservationRegistration : {}", roomReservationRegistration);
        return roomReservationRegistrationRepository.save(roomReservationRegistration);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<RoomReservationRegistration> findAll() {
        log.debug("Request to get all RoomReservationRegistrations");
        return roomReservationRegistrationRepository.findAll();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Optional<RoomReservationRegistration> findById(Long id) {
        log.debug("Request to find RoomReservationRegistration : {}", id);
        return roomReservationRegistrationRepository.findById(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public RoomReservationRegistration getById(Long id) {
        log.debug("Request to get RoomReservationRegistration : {}", id);
        return roomReservationRegistrationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("RoomReservationRegistration not found with id: " + id));
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void delete(Long id) {
        log.debug("Request to delete RoomReservationRegistration : {}", id);
        // if last invoice this item is on is a storno then it is deleteable
        final RoomReservationRegistration rrr = getById(id);

        List<ChargeableItemInvoiceHistory> ciihList = ciihRepository.findAllByChargeableItemIdOrderByIdDesc(id);
        final ChargeableItemInvoiceHistory lastHistoryItem = ciihList.stream().findFirst().orElse(null);
        if (lastHistoryItem != null && lastHistoryItem.getInvoice().getStorno()) {
            ciihRepository.deleteAll(ciihList);
        }

        // if last group invoice this item is on is a storno then it is deleteable
        List<GroupDiscountInvoiceHistory> gdihList = gdihRepository.findAllByChargeableItemIdOrderByIdDesc(id);
        final GroupDiscountInvoiceHistory lastGroupHistoryItem = gdihList.stream().findFirst().orElse(null);
        if (lastGroupHistoryItem != null && lastGroupHistoryItem.getInvoice().getStorno()) {
            gdihRepository.deleteAll(gdihList);
        }

        if (rrr.getRoomReservation().getRoomReservationRegistrations().size() == 1) {
            RoomReservation rr = rrr.getRoomReservation();
            final Stream<LocalDate> range = Stream.iterate(rr.getArrivalDate(), d -> d.plusDays(1))
                    .limit(ChronoUnit.DAYS.between(rr.getArrivalDate(), rr.getDepartureDate()));
            range.forEach(localDate -> roomReservationService.decreaseRoomReservedNumber(rr.getRoom(), localDate));
            roomReservationRepository.deleteById(rrr.getRoomReservation().getId());
        } else {
            roomReservationRegistrationRepository.deleteById(id);
        }
    }
}
