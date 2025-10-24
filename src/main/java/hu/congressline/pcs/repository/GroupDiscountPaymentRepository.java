package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import hu.congressline.pcs.domain.GroupDiscountPayment;

public interface GroupDiscountPaymentRepository extends JpaRepository<GroupDiscountPayment, Long> {

    List<GroupDiscountPayment> findByCongressIdOrderByDateOfPaymentDesc(Long id);

    List<GroupDiscountPayment> findByPayingGroupIdOrderByDateOfPaymentDesc(Long id);
}
