package models;

import java.time.LocalDate;

public class Holding {
	private Equity equity;
	private Double shares;
	private Double pricePerShare;
	private LocalDate date;
	
	public Holding(Equity equity, Double shares, Double pricePerShare){
		this(equity, shares, pricePerShare, LocalDate.now());
	}
	
	public Holding(Equity equity, Double shares, Double pricePerShare, LocalDate date){
		this.equity = equity;
		this.shares = shares;
		this.pricePerShare = pricePerShare;
		this.date = date;
	}
	
	public Equity getEquity(){
		return this.equity;
	}
	
	public Double getShares(){
		return this.shares;
	}
	
	public void addShares(Double d){
		this.shares = this.shares + d;
	}
	
	public boolean removeShares(Double d){
		if(this.shares - d >= 0){
			this.shares = this.shares - d;
			return true;
		}
		else{
			return false;
		}
	}
	
	public Double getPricePerShare(){
		return this.pricePerShare;
	}
	
	public LocalDate getDate(){
		return this.date;
	}
	
	public void setPricePerShare(Double d){
		this.pricePerShare = d;
	}
	
	public void setDate(LocalDate date){
		this.date = date;
	}
}
