package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

import hu.congressline.pcs.domain.PcsFile;

public interface PcsFileRepository extends JpaRepository<PcsFile, Long> {

    List<PcsFile> findAllByOnlineRegistrationId(Long id);

    @Query("select e.onlineRegistrationId from PcsFile e where e.onlineRegistrationId in :onlineRegIds")
    Set<Long> findAllOnlineRegistrationIdsWithAttachment(@Param("onlineRegIds") Set<Long> onlineRegIds);

    void deleteAllByOnlineRegistrationId(Long id);
}
