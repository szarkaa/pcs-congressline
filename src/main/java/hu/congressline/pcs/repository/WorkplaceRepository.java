package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import hu.congressline.pcs.domain.Congress;
import hu.congressline.pcs.domain.Workplace;

public interface WorkplaceRepository extends JpaRepository<Workplace, Long> {

    List<Workplace> findByCongressIdOrderByName(Long id);

    List<Workplace> findByCongressIsNullOrderByName();

    @Query("select e from Workplace e where e.congress.id = :id or e.congress is null order by e.name")
    List<Workplace> findAllForCongressId(@Param("id") Long id);

    Optional<Workplace> findOneByCongressAndName(Congress congress, String name);
}
