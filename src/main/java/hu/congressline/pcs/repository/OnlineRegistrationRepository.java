package hu.congressline.pcs.repository;

import hu.congressline.pcs.domain.RoomReservation;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import hu.congressline.pcs.domain.OnlineRegistration;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OnlineRegistrationRepository extends JpaRepository<OnlineRegistration, Long> {

    @EntityGraph(
        type = EntityGraph.EntityGraphType.LOAD,
        attributePaths = {
            "congress",
            "congress.bankAccounts",
            "congress.currencies",
            "congress.onlineRegCurrencies"
        }
    )
    List<OnlineRegistration> findByCongressIdOrderByDateOfAppDesc(Long id);

    @EntityGraph(
        type = EntityGraph.EntityGraphType.LOAD,
        attributePaths = {
            "congress",
            "congress.bankAccounts",
            "congress.currencies",
            "congress.onlineRegCurrencies"
        }
    )
    List<OnlineRegistration> findByPaymentTrxStatusIn(List<String> statusCodes);

    @EntityGraph(
        type = EntityGraph.EntityGraphType.LOAD,
        attributePaths = {
            "congress",
            "congress.bankAccounts",
            "congress.currencies",
            "congress.onlineRegCurrencies"
        }
    )
    Optional<OnlineRegistration> findOneByPaymentTrxId(String txId);

    @EntityGraph(
        type = EntityGraph.EntityGraphType.LOAD,
        attributePaths = {
            "congress",
            "congress.bankAccounts",
            "congress.currencies",
            "congress.onlineRegCurrencies"
        }
    )
    Optional<OnlineRegistration> findEagerById(Long id);

    @EntityGraph(
        type = EntityGraph.EntityGraphType.LOAD,
        attributePaths = {
            "congress",
            "congress.bankAccounts",
            "congress.currencies",
            "congress.onlineRegCurrencies"
        }
    )

    List<OnlineRegistration> findByIdInOrderByDateOfAppDesc(List<Long> onlineRegIdList);

    List<OnlineRegistration> findAllByRoomId(Long roomId);

    @Query("select count(e) from OnlineRegistration e where e.room.id = :roomId and e.arrivalDate <= :date and e.departureDate > :date")
    long countReservationsByRoomAndDate(@Param("roomId") Long roomId, @Param("date") LocalDate date);

}
