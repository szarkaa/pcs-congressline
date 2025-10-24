package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import hu.congressline.pcs.domain.PayingGroup;

public interface PayingGroupRepository extends JpaRepository<PayingGroup, Long> {

    List<PayingGroup> findByCongressIdOrderByName(Long id);
}
