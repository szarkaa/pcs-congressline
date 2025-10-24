package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import hu.congressline.pcs.domain.PayingGroupItem;
import hu.congressline.pcs.domain.enumeration.ChargeableItemType;

public interface PayingGroupItemRepository extends JpaRepository<PayingGroupItem, Long> {

    List<PayingGroupItem> findAllByChargeableItemTypeAndPayingGroupCongressId(ChargeableItemType chargeableItemType, Long id);

    List<PayingGroupItem> findAllByPayingGroupId(Long id);

    void deleteAllByPayingGroupId(Long id);
}
