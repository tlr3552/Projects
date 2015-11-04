package persistence;

/**
 * A Trie made which should hold the tickerSymbols and equityNames
 * of all Equities read into the system.
 * 
 * @author Bill Dybas
 *
 */
public class EquityTrie {
	private Trie actualTrie;
	private static EquityTrie instance = null;
	
	private EquityTrie(){
		actualTrie = new Trie();
	}
	
	public static EquityTrie getInstance(){
		if(instance == null){
			instance = new EquityTrie();
		}
		return instance;
	}
	
	public void add(String word){
		actualTrie.add(word);
	}
	
	public boolean search(String word){
		return actualTrie.search(word);
	}
	
}
