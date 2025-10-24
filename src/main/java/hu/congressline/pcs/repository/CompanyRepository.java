package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hu.congressline.pcs.domain.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {

}
