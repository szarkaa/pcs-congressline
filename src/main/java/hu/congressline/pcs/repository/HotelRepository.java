package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import hu.congressline.pcs.domain.Hotel;

public interface HotelRepository extends JpaRepository<Hotel, Long> {

    Optional<Hotel> findOneByCode(String code);
}
