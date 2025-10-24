package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import hu.congressline.pcs.domain.OrderedOptionalService;

public interface OrderedOptionalServiceRepository extends JpaRepository<OrderedOptionalService, Long> {

    List<OrderedOptionalService> findAllByRegistrationId(Long id);

    List<OrderedOptionalService> findAllByIdIn(List<Long> ids);

    void deleteAllByRegistrationId(Long id);

}
