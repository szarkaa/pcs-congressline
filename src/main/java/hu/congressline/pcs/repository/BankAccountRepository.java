package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hu.congressline.pcs.domain.BankAccount;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

}
