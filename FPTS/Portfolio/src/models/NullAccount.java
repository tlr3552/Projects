package models;

import java.time.LocalDate;

public class NullAccount extends Account{
	public static final String defaultDate = "0001-01-01";
	
	@Override
	public String getName() {
		return "None";
	}

	@Override
	public String getType() {
		return "None";
	}

	@Override
	public void deposit(double amount) {
		return;
	}

	@Override
	public boolean withdraw(double amount) {
		return true;
	}

	@Override
	public boolean transfer(Account a, double amount) {
		return true;
	}

	@Override
	public double getCurrentBalance() {
		return 0;
	}

	@Override
	public LocalDate getDateCreated() {
		return LocalDate.parse(defaultDate);
	}
}
