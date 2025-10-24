package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import hu.congressline.pcs.domain.Congress;

public interface CongressRepository extends JpaRepository<Congress, Long> {

    @Query("select distinct congress from Congress congress left join fetch congress.currencies left join fetch congress.onlineRegCurrencies left join fetch congress.bankAccounts")
    List<Congress> findAllWithEagerRelationships();

    @Query("select congress from Congress congress left join fetch congress.currencies left join fetch congress.onlineRegCurrencies "
            + "left join fetch congress.bankAccounts where congress.id =:id")
    Congress findOneWithEagerRelationships(@Param("id") Long id);

    Optional<Congress> findOneByMeetingCode(String meetingCode);

    Optional<Congress> findOneByUuid(String uuid);
}
