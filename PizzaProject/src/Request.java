import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/*
 * @author Tyler Russell, Bill Alsibai, Victor Paiz, Akia Vongdara
 * The request class holds various aspects we need to keep track of,
 * primarily the wordscore and titlescore so we can calculate distances between
 * requests.
 */
public class Request {
	
	//each score is a sum of the frequencies of each word in the request text and the title text
	private int wordscore;
	private int titlescore;
	
	//Other important class attributes
	private boolean pizza;
	private ArrayList<String> words = null;//Contains the words in the reddit post
	private ArrayList<String> request_title = null;//Contains the words in the posts title
	private double downvotes, upvotes;
	private boolean was_post_edited;
	private double num_comments_at_request;
	private double num_comments_at_retrieval;
	private HashMap<String, Double> titlePopularity = null;
	
	/*
	 * Constructor, accepts every request attribute we need from the dataset.
	 * ArrayList<String> words - list of words in the request_text
	 * double upvotes - the number of upvotes this request got
	 * 
	 */
	public Request(ArrayList<String> words, double upvotes, double downvotes, boolean edited, double num_comments_at_request, double num_comments_at_retrieval,
				   ArrayList<String> request_title, boolean pizza) {

		this.words = words;
		this.upvotes = upvotes;
		this.downvotes = downvotes;
		was_post_edited = edited;
		this.num_comments_at_request = num_comments_at_request;
		this.num_comments_at_retrieval = num_comments_at_retrieval;
		this.request_title = request_title;
		this.pizza = pizza;
		titlePopularity = new HashMap<String, Double>();
	}
	
	public int getTitleScore() {
		return titlescore;
	}
	
	@SuppressWarnings("rawtypes")
	public HashMap getTitlePopularities() {
		return titlePopularity;
	}
	
	@SuppressWarnings("unchecked")
	public void putStringTitlePopularity(String str, Double d) {
		titlePopularity.put(str, d);
		titlePopularity = sortByValues(titlePopularity);
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static HashMap sortByValues(HashMap map) {
		List list = new LinkedList(map.entrySet());
		
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o1)).getValue())
						.compareTo(((Map.Entry)(o2)).getValue());
			}
		});
		
		HashMap sortedMap = new LinkedHashMap();
		for(Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
	
	public boolean getPizza() {
		return pizza;
	}
	
	public boolean wasEdited() {
		return was_post_edited;
	}
	
	public double getDownVotes() {
		return downvotes;
	}
	
	public double getUpVotes() {
		return upvotes;
	}
	
	public double getNumCommentsRequest() {
		return num_comments_at_request;
	}
	
	public double getNumCommentsRetrieval() {
		return num_comments_at_retrieval;
	}
	
	public ArrayList<String> getWordList() {
		return words;
	}
	
	public ArrayList<String> getTitleList() {
		return request_title;
	}
	
	public String listToString(ArrayList<String> list) {
		String str = "";
		for(String s: list)
			str += s + " ";
		return str;
	}
	
	public ArrayList<String> stringToList(String str) {
		String[] arr = str.split(" ");
		ArrayList<String> list = new ArrayList<String>(Arrays.asList(arr));
		return list;
	}
	
	public void printRequest() {
		String str = "";
		
		str += "Wordscore:\t" + wordscore + "\n";
		str += "Titlescore:\t" + titlescore + "\n";
		str += "Got Pizza:\t" + pizza + "\n";
		str += "Edited post:\t" + was_post_edited + "\n";
		str += "Request text:\t" + listToString(words) + "\n";
		str += "Title text:\t" + listToString(request_title) + "\n";
		str += "Upvotes:\t" + upvotes + "\n";
		str += "Downvotes:\t" + downvotes + "\n";
		str += "Number of comments @ request:\t" + num_comments_at_request + "\n";
		str += "Number of comments @ retrieval:\t" + num_comments_at_retrieval + "\n";
		System.out.println(str + "------------------------------------------------------");
	}
	
	public boolean containsTitleWord(String str) {
		return request_title.contains(str);
	}
	
	public boolean containsWord(String str) {
		return words.contains(str);
	}
	
	public void increaseTitleScore(int i) {
		titlescore += i;
	}
	
	public void increaseWordScore(int i) {
		wordscore += i;
	}
	
	public void setWordScore(int i) {
		wordscore = i;
	}
	
	public int getWordScore() {
		return wordscore;
	}
}
