package hu.congressline.pcs.repository;

import hu.congressline.pcs.domain.ChargeableItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChargeableItemRepository extends JpaRepository<ChargeableItem, Long> {

}
