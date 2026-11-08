package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import hu.congressline.pcs.domain.GroupDiscountPayment;

public interface GroupDiscountPaymentRepository extends JpaRepository<GroupDiscountPayment, Long> {

    @EntityGraph(
        type = EntityGraph.EntityGraphType.LOAD,
        attributePaths = {
            "congress",
            "congress.bankAccounts",
            "congress.currencies",
            "congress.onlineRegCurrencies",
            "payingGroup.payingGroupItems"
        }
    )
    @Override
    Optional<GroupDiscountPayment> findById(Long id);

    @EntityGraph(
        type = EntityGraph.EntityGraphType.LOAD,
        attributePaths = {
            "congress",
            "congress.bankAccounts",
            "congress.currencies",
            "congress.onlineRegCurrencies",
            "payingGroup.payingGroupItems"
        }
    )
    List<GroupDiscountPayment> findByCongressIdOrderByDateOfPaymentDesc(Long id);

    @EntityGraph(
        type = EntityGraph.EntityGraphType.LOAD,
        attributePaths = {
            "congress",
            "congress.bankAccounts",
            "congress.currencies",
            "congress.onlineRegCurrencies",
            "payingGroup.payingGroupItems"
        }
    )
    List<GroupDiscountPayment> findByPayingGroupIdOrderByDateOfPaymentDesc(Long id);
}
