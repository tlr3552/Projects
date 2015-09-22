import java.io.FileReader;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray; 
import org.json.simple.JSONObject;

/*
 * @author Tyler Russell, Bill Alsibai, Victor Paiz, Akia Vongdara
 * Data Cleaning program. It reads in the data set which is in .json format, and
 * creates three text files in the directory where it is executed:
 * 
 * 	wordbank.txt - contains mappings from word to probability of it appearing overall in the data
 * 	freq_true.txt - contains mappings from word to frequency the word appeared in a true request
 * 	freq-_false - contains mappings from word to frequency the word appeared in a false request
 */

@SuppressWarnings("rawtypes")
public class Cleaner {
	
	static final String[] articles = {"i'm","a","an", "the", "many", "few" ,"some","this","that","these","those","each","every","either","neither","much"
			,"enough","which","what","who","where","when","have","i","to","am","and","my","of","for","in","pizza","it","me","is","would"
			, "be","but","on","out","with","you","so","get","just","been","not","we","if", "it", "can","at", "are", "as"};
	
	//Static class variables representing attributes from a JSON request(reddit post)
	static String text = "";
	static String request_id = "";
	static ArrayList<String> request_title;
	static double num_downvotes, num_upvotes;
	static boolean was_post_edited;
	static double num_comments_at_request;
	static double num_comments_at_retrieval;
	static Boolean found;
	static JSONParser parser = new JSONParser();
	static JSONArray array = null;
	static JSONObject request = null;
	static ArrayList<String> words = null;
	static ArrayList<String> titlewords = null;
	static ArrayList<Request> requests = new ArrayList<Request>();
	static HashSet<String> trueSet = new HashSet<String>();
	static HashSet<String> falseSet = new HashSet<String>();
	static HashMap<String, Integer> trues = new HashMap<String, Integer>();
	static HashMap<String, Integer> falses = new HashMap<String, Integer>();
	static HashMap<String, Integer> totalOccurencesRequest = new HashMap<String, Integer>();
	static HashMap<String, Integer> totalOccurencesTitle = new HashMap<String, Integer>();
	static HashMap<String, Double> wordbank = new HashMap<String, Double>();
	static HashMap sortedTitles = null;
	
	public static void main(String[] args) {

		try {
			array = (JSONArray) parser.parse(new FileReader(
					"dataset/pizza_request_dataset.json"));
		}
		catch(Exception e) {
			System.err.println("Something went wrong reading in the json file.");
			System.exit(0);
		}
		
		for(Object o: array) {
			
			//Get the edited request text and parse it accordingly.
			//Remove words that are articles
			//Get the requests result of pizza or no pizza
			request = (JSONObject) o;
			
			words = getCleanedRequestText(request.get("request_text_edit_aware").toString());//Get a list of unique words that are longer than 1 letter, used locally
			//request_id = request.get("request_id").toString();
			
			//Get the request result
			found = Boolean.parseBoolean(request.get("requester_received_pizza").toString());//Get request result
			//Pull all wanted attributes from the request
			titlewords = getCleanedRequestText(request.get("request_title").toString());
			num_downvotes =  Double.parseDouble(request.get("number_of_downvotes_of_request_at_retrieval").toString());
			num_upvotes = Double.parseDouble(request.get("number_of_upvotes_of_request_at_retrieval").toString());
			was_post_edited = Boolean.parseBoolean(request.get("post_was_edited").toString());
			num_comments_at_request = Double.parseDouble(request.get("requester_number_of_comments_at_request").toString());
			num_comments_at_retrieval = Double.parseDouble(request.get("requester_number_of_comments_at_retrieval").toString());
			
			if(!words.isEmpty()) {
				
				//Create new request object with these values retrieved from the json request
				Request rq = new Request(words, num_upvotes, num_downvotes, was_post_edited, num_comments_at_request, num_comments_at_retrieval, titlewords, found);
				//Save this request in a list
				requests.add(rq);
			
			
				//For each word in the title of this request
				for(String str: titlewords) {
					
					if(!isArticle(str) && !str.equalsIgnoreCase("request")) {//Ignore "request", since its
						if(totalOccurencesTitle.get(str) == null)			 //prefixed at the beginning of every requests title
							totalOccurencesTitle.put(str, 1);
						else {
							int i = totalOccurencesTitle.get(str);
							totalOccurencesTitle.put(str, i+1);
						}
					}
				}
			
				//For each word in this request
				for(String str: words) {
					if(!isArticle(str)) {
						//Add words in true requests to respective container
						if(found) {
							if(trues.get(str) == null)
								trues.put(str, 1);
							else {
								int c = trues.get(str);
								trues.put(str, c+1);
							}
						}
						//Else the request is false, add it to its respective container
						else {
							if(falses.get(str) == null)
								falses.put(str, 1);
							else {
								int c = falses.get(str);
								falses.put(str, c+1);
							}
						}
						//Keep track of total occurrences of all words, regardless of true or false
						if(totalOccurencesRequest.get(str) == null) {
							totalOccurencesRequest.put(str, 1);
						}
						else {
							int i = totalOccurencesRequest.get(str);
							totalOccurencesRequest.put(str, i+1);
						}
					}//End If this word isn't an article
				}//End For each word in JSON object text
			}//End If the request has text in it(ignore ones that dont)
		}//End For each raw JSON object
		
		FileWritingUtils utils = new FileWritingUtils();
		utils.writeWordbank(totalOccurencesRequest, wordbank);//wordbank is empty before calling this method
		System.out.println("Wordbank created.");
		
		//Sort the map that has the frequencies with words in the title
		sortedTitles = sortByValues(totalOccurencesTitle);
		
		//In each request, save a map of words from the title to each Request object
		for(Request req: requests) {
			for(String str: req.getTitleList()) {
				Iterator it = sortedTitles.entrySet().iterator();
				while(it.hasNext()) {
					Map.Entry pair = (Map.Entry)it.next();
					if(pair.getKey().equals(str)) {
						req.putStringTitlePopularity(str, (double)(Integer)pair.getValue());
					}
				}
			}
		}
		
		tallyWordscore();//Count up the scores for all requests
		tallyTitlescore();
		utils.writeCSV(requests);
		utils.writeTitleScores(requests);
		utils.writebank(totalOccurencesRequest);
		System.out.println("Requests CSV file created.");
		System.out.println("Done.");
		
	}//END MAIN
	
	/*
	 * Helper method to print out a given HashMap
	 * @param Map mp
	 */
	@SuppressWarnings("unused")
	private static void printMap(Map mp) {
	    Iterator it = mp.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        System.out.println(pair.getKey() + " = " + pair.getValue());
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	}
	
	/*
	 * Helper method to sort a HashMap by value
	 */
	@SuppressWarnings({ "unchecked" })
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
	
	/*
	 * This method goes through each request and tallys up the score
	 * of that requests title, or the sum of frequencies of the words
	 */
	@SuppressWarnings({ "unchecked" })
	public static void tallyTitlescore() {
		
		Iterator it = sortedTitles.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<String, Integer> pair = (Entry<String, Integer>)it.next();
			for(Request rq: requests)
				if(rq.containsTitleWord(pair.getKey()))
					rq.increaseTitleScore(pair.getValue());
		}
	}
	
	/*
	 * Tallys up the score of the requests actual text, or the sum of
	 * frequencies of the words in the request itself
	 */
	@SuppressWarnings({ "unchecked" })
	public static void tallyWordscore() {

		//Go through the wordbank and increase the wordscore of the request if it contains the word.
		Iterator it = totalOccurencesRequest.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<String, Integer> pair = (Entry<String, Integer>)it.next();
			for(Request req: requests)
				if(req.containsWord(pair.getKey()))
					req.increaseWordScore(pair.getValue());
		}
	}

	/*
	 * This method does the majority of the cleaning, including removing non-alpha
	 * characters and words with less than 2 characters.
	 * @param String text
	 * @return ArrayList<String> cleanedList
	 */
	public static ArrayList<String> getCleanedRequestText(String text) {
		
		ArrayList<String> cleanedList = new ArrayList<String>();
		text = removeUrl(text);
		String delim = "[^\\p{Alpha}]";
		String[] split = text.split(delim);
		for(String s: split) {
			s = s.toLowerCase();
			if(!cleanedList.contains(s) && s.length() > 1 && !s.equalsIgnoreCase("request") && !s.equals("ve"))
				cleanedList.add(s);
		}
		return cleanedList;
	}
	
	/*
	 * Checks a given string to see if it is a grammatical "article", 
	 * since we don't care about those words so much for this project.
	 * @param String str
	 * @return boolean true if "str" is an article, false otherwise
	 */
	public static boolean isArticle(String str) {
		for(String s: articles)
			if(str.equals(s))
				return true;
		return false;
	}
	
	/*
	 * Parses out the URL from the comment string.
	 * @param String commentstr
	 * @return String tempstr
	 */
	public static String removeUrl(String commentstr)
    {
		String tempstr = commentstr;
        String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern p = Pattern.compile(urlPattern,Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(tempstr);
        while (m.find()) {
            tempstr = tempstr.replaceAll(Pattern.quote(m.group()),"").trim();
        }
        return tempstr;
    }
}
