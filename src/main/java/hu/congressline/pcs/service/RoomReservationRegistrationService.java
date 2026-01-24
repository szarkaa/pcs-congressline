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

    private final RoomReservationService roomReservationService;
    private final RoomReservationRegistrationRepository repository;
    private final RoomReservationRepository rrRepository;
    private final ChargeableItemInvoiceHistoryRepository ciihRepository;
    private final GroupDiscountInvoiceHistoryRepository gdihRepository;

    @SuppressWarnings("MissingJavadocMethod")
    public RoomReservationRegistration save(RoomReservationRegistration roomReservationRegistration) {
        log.debug("Request to save room reservation registration : {}", roomReservationRegistration);
        return repository.save(roomReservationRegistration);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Optional<RoomReservationRegistration> findById(Long id) {
        log.debug("Request to find room reservation registration by id: {}", id);
        return repository.findById(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public RoomReservationRegistration getById(Long id) {
        log.debug("Request to get room reservation registration by id: {}", id);
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("room reservation registration not found with id: " + id));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<RoomReservationRegistration> findAllByRegistrationId(Long registrationId) {
        return repository.findAllByRegistrationId(registrationId);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void delete(Long id) {
        log.debug("Request to delete room reservation registration by id: {}", id);
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
            rrRepository.deleteById(rrr.getRoomReservation().getId());
        } else {
            repository.deleteById(id);
        }
    }

}
