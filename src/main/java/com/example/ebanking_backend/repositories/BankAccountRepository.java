package com.example.ebanking_backend.repositories;

import com.example.ebanking_backend.entities.BankAccount;
import com.example.ebanking_backend.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BankAccountRepository extends JpaRepository<BankAccount, String> {
    List<BankAccount> findByCustomer(Customer customer);

}
