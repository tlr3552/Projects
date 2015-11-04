package models;

import java.time.LocalDate;

/**
 * Represents the act of withdrawing
 * money from an Account.
 *
 * @author Bill Dybas
 *
 */
public class WithdrawAccountTransaction implements AccountTransaction{
	private Account a;
	private Double amount;
	private LocalDate d;

	public WithdrawAccountTransaction(Account a, Double amount, LocalDate d){
		this.a = a;
		this.amount = amount;
		this.d = d;
	}

	@Override
	public boolean execute() {
		return a.withdraw(amount);
	}

	@Override
	public void unexecute() {
		new DepositAccountTransaction(a, amount, d).execute();
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
