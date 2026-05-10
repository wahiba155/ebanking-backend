package com.example.ebanking_backend;

import com.example.ebanking_backend.dtos.*;
import com.example.ebanking_backend.entities.*;
import com.example.ebanking_backend.enums.AccountStatus;
import com.example.ebanking_backend.enums.OperationType;
import com.example.ebanking_backend.exceptions.CustomerNotFoundException;
import com.example.ebanking_backend.repositories.AccountOperationRepository;
import com.example.ebanking_backend.repositories.BankAccountRepository;
import com.example.ebanking_backend.repositories.CustomerRepository;
import com.example.ebanking_backend.services.AccountService;
import com.example.ebanking_backend.services.BankAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class EbankingBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(EbankingBackendApplication.class, args);
	}
	@Bean
	CommandLineRunner initUsers(AccountService accountService) {
		return args -> {
			// Créer les rôles
			accountService.addNewRole("ADMIN");
			accountService.addNewRole("USER");

			// Créer les utilisateurs
			NewUserDTO admin = new NewUserDTO();
			admin.setUsername("admin");
			admin.setPassword("1234");
			admin.setEmail("admin@gmail.com");
			admin.setRoles(List.of("ADMIN", "USER"));
			accountService.addNewUser(admin);

			NewUserDTO user1 = new NewUserDTO();
			user1.setUsername("user1");
			user1.setPassword("1234");
			user1.setEmail("user1@gmail.com");
			user1.setRoles(List.of("USER"));
			accountService.addNewUser(user1);
		};
	}

	@Bean
	CommandLineRunner commandLineRunner(BankAccountService bankAccountService) {
		return args -> {
			// Créer 3 clients
			Stream.of("Hassan", "Imane", "Mohamed").forEach(name -> {
				CustomerDTO customer = new CustomerDTO();
				customer.setName(name);
				customer.setEmail(name + "@gmail.com");
				bankAccountService.saveCustomer(customer);
			});

			// Créer des comptes pour chaque client
			bankAccountService.listCustomers().forEach(customer -> {
				try {
					bankAccountService.saveCurrentBankAccount(
							Math.random() * 90000, 9000, customer.getId());
					bankAccountService.saveSavingBankAccount(
							Math.random() * 120000, 5.5, customer.getId());
				} catch (CustomerNotFoundException e) {
					e.printStackTrace();
				}
			});

			// Créer des opérations sur chaque compte
			List<BankAccountDTO> bankAccounts = bankAccountService.bankAccountList();
			for (BankAccountDTO bankAccount : bankAccounts) {
				for (int i = 0; i < 10; i++) {
					String accountId;
					if (bankAccount instanceof SavingBankAccountDTO) {
						accountId = ((SavingBankAccountDTO) bankAccount).getId();
					} else {
						accountId = ((CurrentBankAccountDTO) bankAccount).getId();
					}
					try {
						bankAccountService.credit(accountId,
								10000 + Math.random() * 120000, "Credit");
						bankAccountService.debit(accountId,
								1000 + Math.random() * 9000, "Debit");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
	}

	// @Bean  ← désactivé, gardé pour référence
	CommandLineRunner start(CustomerRepository customerRepository,
							BankAccountRepository bankAccountRepository,
							AccountOperationRepository accountOperationRepository) {
		return args -> {
			Stream.of("Hassan", "Yassine", "Aicha").forEach(name -> {
				Customer customer = new Customer();
				customer.setName(name);
				customer.setEmail(name + "@gmail.com");
				customerRepository.save(customer);
			});

			customerRepository.findAll().forEach(cust -> {
				CurrentAccount currentAccount = new CurrentAccount();
				currentAccount.setId(UUID.randomUUID().toString());
				currentAccount.setBalance(Math.random() * 90000);
				currentAccount.setCreatedAt(new Date());
				currentAccount.setStatus(AccountStatus.CREATED);
				currentAccount.setCustomer(cust);
				currentAccount.setOverDraft(9000);
				bankAccountRepository.save(currentAccount);

				SavingAccount savingAccount = new SavingAccount();
				savingAccount.setId(UUID.randomUUID().toString());
				savingAccount.setBalance(Math.random() * 90000);
				savingAccount.setCreatedAt(new Date());
				savingAccount.setStatus(AccountStatus.CREATED);
				savingAccount.setCustomer(cust);
				savingAccount.setInterestRate(5.5);
				bankAccountRepository.save(savingAccount);
			});

			bankAccountRepository.findAll().forEach(acc -> {
				for (int i = 0; i < 10; i++) {
					AccountOperation accountOperation = new AccountOperation();
					accountOperation.setOperationDate(new Date());
					accountOperation.setAmount(Math.random() * 12000);
					accountOperation.setType(Math.random() > 0.5 ?
							OperationType.DEBIT : OperationType.CREDIT);
					accountOperation.setBankAccount(acc);
					accountOperationRepository.save(accountOperation);
				}
			});
		};
	}
}