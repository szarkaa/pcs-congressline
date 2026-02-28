package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import hu.congressline.pcs.domain.Registration;
import hu.congressline.pcs.domain.Workplace;
import hu.congressline.pcs.domain.enumeration.RegistrationTypeType;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    List<Registration> findAllByCongressId(Long id);

    List<Registration> findAllByCongressIdAndIdIn(Long id, Set<Long> ids);

    Long countByCongressId(Long id);

    Long countByCongressIdAndOnSpot(Long id, Boolean onSpot);

    @Query("select count(e) from Registration e where e.workplace.id = :workplaceId and e.id <> :id")
    Long countByWorkplaceId(@Param("workplaceId") Long workplaceId, @Param("id") Long registrationId);

    @Query("select sum(e.accPeople) from RegistrationRegistrationType e where e.registration.congress.id = :congressId and "
            + "e.registrationType.registrationType = :regType")
    Long countAccPeopleByCongressId(@Param("congressId")Long id, @Param("regType") RegistrationTypeType regType);

    @Query("select max(e.regId) from Registration e where e.congress.id = :congressId")
    Integer findLastRegistrationId(@Param("congressId") Long congressId);

    @Query("select e from Registration e where e.id = (select min(se.id) from Registration se where se.congress.id = :congressId)")
    Optional<Registration> findFirstRegistrationByCongressId(@Param("congressId") Long id);

    @Query("select e.id from Registration e where e.id > :id and e.congress.id = :congressId  order by e.id asc")
    List<Long> findNextIdAfterDeletedId(@Param("id")Long id, @Param("congressId") Long congressId);

    @Query("select e.id from Registration e where e.id < :id and e.congress.id = :congressId order by e.id desc")
    List<Long> findPreviousIdAfterDeletedId(@Param("id") Long id, @Param("congressId") Long congressId);

    @Modifying
    @Query("update Registration e set e.workplace = :workplace where e.workplace.id in :workplaceIds")
    void updateMergedWorkplaceInRegistrations(@Param("workplace") Workplace workplace, @Param("workplaceIds") List<Long> workplaceIds);

}
