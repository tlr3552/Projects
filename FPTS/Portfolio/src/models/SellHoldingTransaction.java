package models;

import java.util.Map;

/**
 * Represents the action of selling a Holding.
 *
 * @author Bill Dybas
 *
 */
public class SellHoldingTransaction implements HoldingTransaction{
	private Map<Equity, Holding> holdings;
	private Equity e;
	private Holding h;
	private Account a;

	public SellHoldingTransaction(Map<Equity, Holding> holdings, Equity e, Holding h){
		this(holdings, e, h, new NullAccount());
	}

	public SellHoldingTransaction(Map<Equity, Holding> holdings, Equity e, Holding h, Account a){
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
		// Don't accept mismatched Equity / Holding
		if(!(h.getEquity().equals(e))){
			return false;
		}

		Holding holding = holdings.get(e);

		if(holding != null){
			// If there are enough shares, they will be removed
			if(!(holding.removeShares(h.getShares()))){
				// The number of shares in the Holding
				// was less than the number trying to sell
				return false;
			}

			// If an Account is specified, add the sale to it
			a.deposit(h.getPricePerShare() * h.getShares());
			
			// If by selling the Holding
			// there are no shares left, remove it from the HashMap
			if(holding.getShares() == 0){
				holdings.remove(e);
			}
			return true;
		}
		else{
			// The Holding wasn't in the HashMap
			return false;
		}
	}

	@Override
	public void unexecute(){
		new BuyHoldingTransaction(holdings, e, h, a).execute();
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
