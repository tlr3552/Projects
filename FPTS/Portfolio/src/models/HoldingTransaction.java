package models;

/**
 * Represents a particular type of 
 * Transaction that affects Holdings
 * 
 * @author Bill Dybas
 *
 */
public interface HoldingTransaction extends Transaction{
	
	public Holding getHolding();
}
