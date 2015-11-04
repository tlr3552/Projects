package models;

import java.time.LocalDate;

/**
 * Represents the act of transferring
 * money from one Account to another.
 *
 * @author Bill Dybas
 *
 */
public class TransferAccountTransaction implements AccountTransaction{
	private Account from;
	private Account to;
	private Double amount;
	private LocalDate d;

	public TransferAccountTransaction(Account from, Account to, Double amount, LocalDate d){
		this.from = from;
		this.to = to;
		this.amount = amount;
		this.d = d;
	}

	@Override
	public boolean execute() {
		return from.transfer(to, amount);
	}

	@Override
	public void unexecute() {
		// Should be careful when unexecuting not to
		// take money from an account that doesn't have enough
		to.transfer(from, amount);
	}

	@Override
	public LocalDate getDate() {
		return d;
	}

	@Override
	public Account getAccount() {
		return this.from;
	}

	public Account getOtherAccount(){
		return this.to;
	}

	@Override
	public Double getAmount() {
		return this.amount;
	}
}
