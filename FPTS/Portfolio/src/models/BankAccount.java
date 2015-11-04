package models;

import java.time.LocalDate;

/**
 * A Bank Account
 * 
 * @author Bill Dybas
 *
 */
public class BankAccount extends Account {
	private double currentBalance;
	private LocalDate dateCreated;
	private static String type = "Bank Account";
	private String bankName;
	
	public BankAccount(String bankName) {
		this(bankName, 0);
	}
	
	public BankAccount(String bankName, double initialAmount) {
		this(bankName, initialAmount, LocalDate.now());
		
	}
	
	public BankAccount(String bankName, double initialAmount, LocalDate dateCreated){
		this.bankName = bankName;
		this.currentBalance = initialAmount;
		this.dateCreated = dateCreated;
	}
	
	@Override
	public String toString(){
		return this.bankName;
	}

	@Override
	public String getName() {
		return this.bankName;
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
