package models;

/**
 * Interface for all types of transactions
 * that occur to a Portfolio.
 *
 * Uses the Command Pattern.
 *
 * @author Bill Dybas
 *
 */
public interface Transaction {
	public boolean execute();
	public void unexecute();

	public Account getAccount();
}
