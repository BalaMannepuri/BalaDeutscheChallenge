package com.db.awmd.challenge.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.TransferException;
import com.db.awmd.challenge.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AccountsRepositoryInMemory implements AccountsRepository {

	private final Map<String, Account> accounts = new ConcurrentHashMap<>();
	
	private final NotificationService notificationService;
	
	@Override
	public void createAccount(Account account) throws DuplicateAccountIdException {
		Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
		if (previousAccount != null) {
			throw new DuplicateAccountIdException("Account id " + account.getAccountId() + " already exists!");
		}
	}

	@Override
	public Account getAccount(String accountId) {
		return accounts.get(accountId);
	}

	@Override
	public void transfer(String toAccountId, String fromAccountId, BigDecimal transferBalance) {
		Account fromAccount = accounts.get(fromAccountId);

		if (fromAccount == null) {
			throw new TransferException("From Account id is not existing in Bank:" + fromAccountId);
		}
		BigDecimal availBalance = fromAccount.getBalance();
		if (availBalance.compareTo(transferBalance) >= 0) {
			Account toAccount = accounts.get(toAccountId);
			if (toAccount == null) {
				throw new TransferException("To Account id is not existing in Bank:" + toAccountId);
			}
			accounts.get(toAccountId).setBalance(accounts.get(toAccountId).getBalance().add(transferBalance));
			accounts.get(fromAccountId).setBalance(accounts.get(fromAccountId).getBalance().subtract(transferBalance));
			notificationService.notifyAboutTransfer(toAccount, "You Bank account is " + toAccountId + " credited for Rs." + transferBalance + " on " + LocalDate.now() +" your availiable balance is Rs."+toAccount.getBalance());
			notificationService.notifyAboutTransfer(toAccount, "You Bank account is " + fromAccountId + " debited for Rs." + transferBalance + " on " + LocalDate.now() +" your availiable balance is Rs."+fromAccount.getBalance());
		} else {
			throw new TransferException("InSufficient Balance:" + fromAccount.getBalance());
		}
	}

	@Override
	public void clearAccounts() {
		accounts.clear();
	}

}
