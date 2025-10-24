package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import hu.congressline.pcs.domain.OnlineRegistration;

public interface OnlineRegistrationRepository extends JpaRepository<OnlineRegistration, Long> {

    List<OnlineRegistration> findByCongressIdOrderByDateOfAppDesc(Long id);

    List<OnlineRegistration> findByPaymentTrxStatusIn(List<String> statusCodes);

    Optional<OnlineRegistration> findOneByPaymentTrxId(String txId);

    List<OnlineRegistration> findByIdInOrderByDateOfAppDesc(List<Long> onlineRegIdList);
}
