package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hu.congressline.pcs.domain.ChargeableItem;

public interface ChargeableItemRepository extends JpaRepository<ChargeableItem, Long> {

}
