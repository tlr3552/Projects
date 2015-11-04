package persistence;

import java.util.ArrayList;

import models.Equity;

/**
 * EquityImporter
 * 
 * Imports and creates the Equities from the provided
 * equities.csv file
 * 
 * @author Bill Dybas
 * 
 */
public class EquitiesImporter {
	private EquitiesSingleton equities;
	private EquityTrie equitiesTrie;

	/**
	 * EquityImporter Constructor
	 * 
	 * Reads in the csv data, creates equities based
	 * on that data, and creates both an ArrayList of Equities
	 * and a Trie based on the Equity tickerSymbols and equityNames
	 */
	public EquitiesImporter(){
		this.equities = EquitiesSingleton.getInstance();
		this.equitiesTrie = EquityTrie.getInstance();
		
		// The equities.csv should be in the Portfolio project folder
		CSVReader c = new CSVReader("equities.csv");
		
		// Read in the data from the csv
		c.read();
		ArrayList<String[]> equityData = c.getData();
		
		// Turn the data into Equity objects
		for(String[] s : equityData){
			ArrayList<String> indices = new ArrayList<String>();
			// The indices start at the 3rd value
			for(int i = 3; i < s.length; i++){
				indices.add(s[i]);
			}
			Equity e = new Equity(s[0], s[1], new Double(s[2]), indices);
			equities.add(s[0], e);
		}
		
		// Add the data to the EquityTrie
		for(Equity e : this.equities.getEquities().values()) {
			this.equitiesTrie.add(e.getTickerSymbol());
			this.equitiesTrie.add(e.getEquityName());
		}
	}
}
