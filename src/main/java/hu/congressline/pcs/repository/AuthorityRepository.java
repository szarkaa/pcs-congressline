package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hu.congressline.pcs.domain.Authority;

public interface AuthorityRepository extends JpaRepository<Authority, String> {
}
