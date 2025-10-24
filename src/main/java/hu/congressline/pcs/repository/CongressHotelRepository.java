package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import hu.congressline.pcs.domain.CongressHotel;

public interface CongressHotelRepository extends JpaRepository<CongressHotel, Long> {

    List<CongressHotel> findByCongressId(Long id);
}
