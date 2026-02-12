package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import hu.congressline.pcs.domain.OnlineRegistration;

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
}
