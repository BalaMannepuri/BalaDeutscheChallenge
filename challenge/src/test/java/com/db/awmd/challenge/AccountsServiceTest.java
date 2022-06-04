package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.TransferException;
import com.db.awmd.challenge.service.AccountsService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountsServiceTest {

	@Autowired
	private AccountsService accountsService;

	@Test
	public void addAccount() throws Exception {
		Account account = new Account("Id-123");
		account.setBalance(new BigDecimal(1000));
		this.accountsService.createAccount(account);

		assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
	}

	@Test
	public void addAccount_failsOnDuplicateId() throws Exception {
		String uniqueId = "Id-" + System.currentTimeMillis();
		Account account = new Account(uniqueId);
		this.accountsService.createAccount(account);

		try {
			this.accountsService.createAccount(account);
			fail("Should have failed when adding duplicate account");
		} catch (DuplicateAccountIdException ex) {
			assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
		}

	}

	@Test
	public void transfer() throws Exception {
		String uniqueId = "Id-" + System.currentTimeMillis();
		Account fromAccount = new Account(uniqueId);
		fromAccount.setBalance(new BigDecimal(1000));
		this.accountsService.createAccount(fromAccount);
		
		uniqueId = "To_Id-" + System.currentTimeMillis();
		Account toAccount = new Account(uniqueId);
		toAccount.setBalance(new BigDecimal(100));
		this.accountsService.createAccount(toAccount);
		this.accountsService.transfer(toAccount.getAccountId(), fromAccount.getAccountId(), new BigDecimal(100));

		assertThat(this.accountsService.getAccount(fromAccount.getAccountId()).getBalance()).isEqualTo(new BigDecimal(900));
	}

	@Test
	public void transferUnknownFromAccount() throws Exception {
		try {
			this.accountsService.transfer("1235", "9234", new BigDecimal(100));
		} catch (TransferException ex) {
			assertThat(ex.getMessage()).isEqualTo("From Account id is not existing in Bank:9234");
		}
	}

	@Test
	public void transferInsufficientBalance() throws Exception {
		try {
			Account fromAccount = new Account("1234");
			fromAccount.setBalance(new BigDecimal(1000));
			this.accountsService.createAccount(fromAccount);
			this.accountsService.transfer("1235", fromAccount.getAccountId(), new BigDecimal(10000));
		} catch (TransferException ex) {
			assertThat(ex.getMessage()).isEqualTo("InSufficient Balance:" + 1000);
		}
	}

	@Test
	public void transferUnknownToAccount() throws Exception {
		try {
			Account fromAccount = new Account("2234");
			fromAccount.setBalance(new BigDecimal(1000));
			this.accountsService.createAccount(fromAccount);
			this.accountsService.transfer("9235", fromAccount.getAccountId(), new BigDecimal(100));
		} catch (TransferException ex) {
			assertThat(ex.getMessage()).isEqualTo("To Account id is not existing in Bank:9235");
		}
	}
}
