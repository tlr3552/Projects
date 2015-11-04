package models;

import java.time.LocalDate;

/**
 * Represents the act of depositing
 * money into an Account
 *
 * @author Bill Dybas
 *
 */
public class DepositAccountTransaction implements AccountTransaction {
	private Account a;
	private Double amount;
	private LocalDate d;

	public DepositAccountTransaction(Account a, Double amount, LocalDate d){
		this.a = a;
		this.amount = amount;
		this.d = d;
	}

	@Override
	public boolean execute() {
		a.deposit(amount);
		return true;
	}

	@Override
	public void unexecute() {
		new WithdrawAccountTransaction(a, amount, d).execute();
	}

	@Override
	public LocalDate getDate() {
		return d;
	}

	@Override
	public Account getAccount() {
		return this.a;
	}

	@Override
	public Double getAmount() {
		return this.amount;
	}
}
