package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import hu.congressline.pcs.domain.OnlineRegDiscountCode;
import hu.congressline.pcs.domain.enumeration.ChargeableItemType;

public interface OnlineRegDiscountCodeRepository extends JpaRepository<OnlineRegDiscountCode, Long> {

    List<OnlineRegDiscountCode> findAllByDiscountTypeAndCongressId(ChargeableItemType chargeableItemType, Long id);

    List<OnlineRegDiscountCode> findAllByCongressId(Long id);

    Long countAllByCongressId(Long id);

    Optional<OnlineRegDiscountCode> findOneByCongressUuidAndCode(String uuid, String code);

    void deleteAllByCongressId(Long id);
}
