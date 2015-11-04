package models;

import java.time.LocalDate;
import java.util.Map;

/**
 * Represents the action of buying a Holding.
 *
 * @author Bill Dybas
 *
 */
public class BuyHoldingTransaction implements HoldingTransaction{
	private Map<Equity, Holding> holdings;
	private Equity e;
	private Holding h;
	private Account a;

	public BuyHoldingTransaction(Map<Equity, Holding> holdings, Equity e, Holding h){
		this(holdings, e, h, new NullAccount());
	}

	public BuyHoldingTransaction(Map<Equity, Holding> holdings, Equity e, Holding h, Account a){
		this.holdings = holdings;
		this.e = e;
		this.h = h;
		this.a = a;
	}

	@Override
	public boolean execute(){
		// Don't accept null Equities or Holdings
		if(e == null || h == null) {
			return false;
		}
		
		// If there are enough funds, they will be withdrawn
		if(!(a.withdraw(h.getPricePerShare() * h.getShares()))){
			// Insufficient Funds
			return false;
		}
		
		// Try putting the Holding into the HashMap
		Holding holding = holdings.putIfAbsent(e, h);
		// The Holding is already in the HashMap
		if(holding != null) {
			Double total = h.getShares() + holding.getShares();
			// Get the weighted average
			Double average = (h.getShares() / total) * (h.getPricePerShare()) 
					+ (holding.getShares() / total) * (holding.getPricePerShare());
			
			// Round the average
			average = Math.round(average * 100.0) / 100.0; 
			
			// Add to the amount stored
			holding.addShares(h.getShares());
			
			// Make the Price the Average
			holding.setPricePerShare(average);
			
			// Updates the Date 
			holding.setDate(LocalDate.now());
		}
		// The Holding was added or updated successfully
		return true;
	}

	@Override
	public void unexecute(){
		new SellHoldingTransaction(holdings, e, h, a).execute();
	}

	@Override
	public Holding getHolding() {
		return this.h;
	}

	@Override
	public Account getAccount() {
		return this.a;
	}
}
