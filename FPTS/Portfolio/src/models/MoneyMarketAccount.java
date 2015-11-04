package models;

import java.time.LocalDate;

/**
 * A Money Market Account
 * 
 * @author Bill Dybas
 *
 */
public class MoneyMarketAccount extends Account{
	private double currentBalance;
	private LocalDate dateCreated;
	private static String type = "Money Market Account";
	private String accountName;
	
	public MoneyMarketAccount(String accountName) {
		this(accountName, 0);
	}
	
	public MoneyMarketAccount(String accountName, double initialAmount) {
		this(accountName, initialAmount, LocalDate.now());
	}
	
	public MoneyMarketAccount(String accountName, double initialAmount, LocalDate dateCreated) {
		this.accountName = accountName;
		this.currentBalance = initialAmount;
		this.dateCreated = dateCreated;
	}
	
	@Override
	public String toString(){
		return this.accountName;
	}

	@Override
	public String getName() {
		return this.accountName;
	}

	@Override
	public String getType() {
		return type;
	}
	
	@Override
	public void deposit(double amount) {
		this.currentBalance += amount;		
	}

	@Override
	public boolean withdraw(double amount) {
		if(this.currentBalance - amount >= 0) {
			this.currentBalance = this.currentBalance - amount;
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public boolean transfer(Account a, double amount) {
		if(this.withdraw(amount)) {
			a.deposit(amount);
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public double getCurrentBalance() {
		return this.currentBalance;
	}

	@Override
	public LocalDate getDateCreated() {
		return this.dateCreated;
	}
}
