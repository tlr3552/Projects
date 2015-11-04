package persistence;

import java.util.HashMap;

import models.Equity;

public class EquitiesSingleton {
	private HashMap<String, Equity> equities;
	private static EquitiesSingleton instance;
	private final String[] dowStocks = {
	"MMM", "AXP", "AAPL", "BA", "CAT", 
	"CVX", "CSCO", "KO", "DIS", "DD", 
	"XOM", "GE", "GS", "HD", "IBM", 
	"INTC", "JNJ", "JPM", "MCD", "MRK",
	"MSFT", "NKE", "PFE", "PG", "TRV", 
	"UTX", "UNH", "VZ", "V", "WMT"
	};
	private final double dowDivisor = 0.14967727343149;
	
	private EquitiesSingleton(){
		equities = new HashMap<String, Equity>();
	}
	
	public static EquitiesSingleton getInstance(){
		if(instance == null){
			instance = new EquitiesSingleton();
		}
		return instance;
	}
	
	public HashMap<String, Equity> getEquities(){
		return this.equities;
	}
	
	public void add(String s, Equity e){
		equities.put(s, e);
	}
	
	public Double getDowJones(){
		double sum = 0.0;
		for(int i = 0; i < dowStocks.length; i++){
			sum += equities.get(dowStocks[i]).getCurrentPrice();
		}
		return Double.valueOf(sum / dowDivisor);
	}
}
