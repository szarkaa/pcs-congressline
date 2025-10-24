package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import hu.congressline.pcs.domain.Congress;
import hu.congressline.pcs.domain.VatInfo;
import hu.congressline.pcs.domain.enumeration.ChargeableItemType;

public interface VatInfoRepository extends JpaRepository<VatInfo, Long> {
    List<VatInfo> findByCongressId(Long id);

    List<VatInfo> findByCongressIsNull();

    @Query("select e from VatInfo e where e.congress.id = :id or e.congress is null")
    List<VatInfo> findAllForCongressId(@Param("id") Long id);

    @Query("select e from VatInfo e where e.chargeableItemType = :itemType and (e.congress.id = :id or e.congress is null)")
    List<VatInfo> findAllForCongressIdAndItemType(@Param("id") Long id, @Param("itemType")ChargeableItemType itemType);

    @Query("select e from VatInfo e where e.chargeableItemType = :chargeableItemType and e.szj = :szj and (e.congress is null or e.congress = :congress)")
    VatInfo getVatBySZJ(@Param("congress") Congress congress, @Param("chargeableItemType") ChargeableItemType chargeableItemType, @Param("szj") String szj);

}
