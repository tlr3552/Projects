package models;

import java.util.ArrayList;

/**
 * Equity
 *
 * An Equity represents a Stock, Bond, or Mutual Fund
 * read in from a provided csv file.
 *
 * @author Bill Dybas
 *
 */
public class Equity {
	private String tickerSymbol;
	private String equityName;
	private double currentPrice;
	private ArrayList<String> indices;

	/**
	 * Equity Constructor
	 *
	 * @param tickerSymbol		the equity's ticker symbol
	 * @param equityName		the entity that the equity represents
	 * @param currentPrice		the current price of the equity
	 * @param index				the index/market on which the equity is bought/sold
	 */
	public Equity(String tickerSymbol, String equityName, double currentPrice, ArrayList<String> indices){
		this.tickerSymbol = tickerSymbol;
		this.equityName = equityName;
		this.currentPrice = currentPrice;
		this.indices = indices;
	}

	/**
	 * @return the tickerSymbol
	 */
	public String getTickerSymbol(){
		return this.tickerSymbol;
	}

	/**
	 * @return the equityName
	 */
	public String getEquityName(){
		return this.equityName;
	}

	/**
	 * @return the currentPrice
	 */
	public double getCurrentPrice(){
		return this.currentPrice;
	}

	/**
	 * Set the Equity's price to another price
	 *
	 * @param newPrice		the new price (must be positive)
	 *
	 * @return true if the price was successfully changed.
	 */
	public boolean setCurrentPrice(double newPrice){
		if(newPrice > 0) {
			//TODO: may also need to check the precision of the double
			this.currentPrice = newPrice;
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * @return the index
	 */
	public ArrayList<String> getIndices(){
		return this.indices;
	}

	/**
	 * An Equity is represented by it's tickerSymbol
	 */
	@Override
	public String toString(){
		return this.tickerSymbol;
	}

	/**
	 * Two equities are equal if their ticker symbol is equal
	 */
	@Override
	public boolean equals(Object o){
		if (!(o instanceof Equity)){
			return false;
		}
		else {
			if (((Equity) o).tickerSymbol.equals(this.tickerSymbol)) {
				return true;
			}
			else {
				return false;
			}
		}
	}

	@Override
	public int hashCode() {
	    return tickerSymbol.hashCode();
	}

	@Override
	public Object clone() {
	    return new Equity(tickerSymbol, equityName, currentPrice, indices);
	}
}
