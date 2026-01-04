package hu.congressline.pcs.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import hu.congressline.pcs.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findOneByActivationKey(String activationKey);

    List<User> findAllByActivatedIsFalseAndCreatedDateBefore(ZonedDateTime dateTime);

    Optional<User> findOneByResetKey(String resetKey);

    Optional<User> findOneByEmail(String email);

    Optional<User> findOneByLogin(String login);

    Optional<User> findOneById(Long userId);

    @Query(value = "select user.id from User user", countQuery = "select count(user) from User user")
    Page<Long> findAllIds(Pageable pageable);

    @Query("select distinct user from User user left join fetch user.authorities left join fetch user.congresses where user.id in :ids")
    List<User> findAllEagerlyByIdIn(@Param("ids") List<Long> ids);

    @Override
    void delete(User user);

}
