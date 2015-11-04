package models;

import java.time.LocalDate;

/**
 * An Account that holds money
 *
 * @author Bill Dybas
 * @author Fawaz Alhenaki
 *
 */
public abstract class Account {
	
	/**
	 * Deposits money in an Account
	 *
	 * @param amount	amount of money to be deposited
	 */
	public abstract void deposit(double amount);

	/**
	 * Withdraws money from an Account.
	 *
	 * @param amount	amount of money to be withdrawn
	 * @return 			True if the money was successfully withdrawn
	 *
	 */
	public abstract boolean withdraw(double amount);

	/**
	 * Transfers money from this account to another
	 *
	 * @param a			the Account to receive the funds
	 * @param amount	the amount to transfer
	 *
	 * @return			True if the money was successfully transferred
	 */
	public abstract boolean transfer(Account a, double amount);

	public abstract String getName();
	
	public abstract String getType();
	
	public abstract double getCurrentBalance();

	public abstract LocalDate getDateCreated();
	
	/**
	 * Two Accounts are equal if they have the same name and type
	 */
	public boolean equals(Object o){
		if(!(o instanceof Account)){
			return false;
		}
		else{
			if(((Account) o).getName().equals(this.getName()) 
					&& ((Account) o).getType().equals(this.getType())){
				return true;
			}
			else{
				return false;
			}
		}
	}
}