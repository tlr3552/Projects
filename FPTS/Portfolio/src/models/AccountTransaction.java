package models;

import java.time.LocalDate;

/**
 * Represents a particular type of 
 * Transaction that affects Accounts
 * 
 * @author Bill Dybas
 *
 */
public interface AccountTransaction extends Transaction{
	
	public LocalDate getDate();
	
	public Double getAmount();
}
