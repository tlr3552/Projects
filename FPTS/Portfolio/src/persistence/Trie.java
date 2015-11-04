package persistence;

import java.util.HashMap;

/**
 * A Trie Data Structure
 * 
 * Created based on information from:
 * http://stackoverflow.com/a/30846612
 * 
 * @author Bill Dybas
 *
 */
public class Trie {
	/**
	 * A Character in the Trie and a Map of its children
	 */
	class Node {
		HashMap<Character, Node> children;
	    boolean end;
	    
	    /**
	     * Node Constructor
	     * 
	     * @param b		whether this is an end node
	     */
	    public Node(boolean b){
	        children = new HashMap<Character, Trie.Node>();
	        end = b;
	    }
	}
	
	private Node root;
	
	/**
	 * Trie Constructor
	 */
	public Trie(){
	    root = new Node(false);
	}
	
	/**
	 * Adds a word to the Trie
	 * 
	 * @param word		the word to add
	 */
	public void add(String word) {
	    Node crawl = this.root;
	    int n = word.length();
	    
	    for (int i = 0; i < n; i++) {
	        char ch = word.charAt(i);
	        
	        if (crawl.children.containsKey(ch)) {
	            crawl = crawl.children.get(ch);
	        } 
	        else {
	            crawl.children.put(ch, new Node(false));
	            Node temp = crawl.children.get(ch);
	            if(i == n - 1) {
	                temp.end = true;
	            }
	            crawl = temp;
	        }
	    }
	}
	
	/**
	 * Searches the Trie to see if a word exists
	 * 
	 * @param word		the word to search
	 * @return			true is the word was found
	 */
	public boolean search(String word) {
		Node crawl = this.root;
	    int n = word.length();
	    for(int i = 0; i < n; i++) {
	        char ch = word.charAt(i);
	        if(crawl.children.get(ch) == null) {
	            return false;
	        }
	        else {
	            crawl = crawl.children.get(ch);
	            if(i == n - 1 && crawl.end == true) {
	                return true;
	            }

	        }
	    }
	    return false;
	}
}
