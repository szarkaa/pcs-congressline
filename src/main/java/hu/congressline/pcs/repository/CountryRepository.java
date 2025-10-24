package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import hu.congressline.pcs.domain.Country;

public interface CountryRepository extends JpaRepository<Country, Long> {

    Optional<Country> findOneByName(String name);

    Optional<Country> findOneByCodeIgnoreCase(String code);

    Optional<Country> findOneByCodeIgnoreCaseAndIdNot(String code, Long id);
}
