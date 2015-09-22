import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.opencsv.*;

/*
 * @author Tyler Russell, Bill Alsibai, Victor Paiz, Akia Vongdara
 * Class that parses in the data that was cleaned by DataCleaner.java
 */
public class Parser {

	static FileReader reader = null;
	static BufferedReader br = null;
	
	@SuppressWarnings({ "unused", "resource" })
	public static void main(String[] args) throws IOException {
		
		//Data to be read in...
		String request_text, request_title;
		boolean pizza, edited;
		Double upvotes, downvotes, num_comments_request, num_comments_retrieval;
		HashMap<String, Double> sortedTitleMap;
		ArrayList<HashMap<String, Double>> listOfSortedTitleMaps = new ArrayList<HashMap<String, Double>>();
		
		String filename = "cleaned_requests.csv";
		reader = new FileReader(filename);
		CSVReader parser = new CSVReader(reader);
		String[] nextLine;
		String temp = "";
		
		//For every request/line in the csv file
		while((nextLine = parser.readNext()) != null) {
			
			request_text = nextLine[0];
			request_title = nextLine[1];
			pizza = Boolean.parseBoolean(nextLine[2]);
			edited = Boolean.parseBoolean(nextLine[3]);
			upvotes = Double.parseDouble(nextLine[4]);
			downvotes = Double.parseDouble(nextLine[5]);
			num_comments_request = Double.parseDouble(nextLine[6]);
			num_comments_retrieval = Double.parseDouble(nextLine[7]);
			
			temp = nextLine[8];
			temp = temp.substring(1, temp.length() - 1);
			
			sortedTitleMap = new HashMap<String, Double>();
			
			//This parses the hashmap.toString() output that was in the CSV file
			//back into a hashmap
			for(String keyValue: temp.split(" *, *")) {
				//System.out.println("AAAA");
				String[] pairs = keyValue.split(" *= *", 2);
	
				sortedTitleMap.put(pairs[0], pairs.length == 1 ? null : Double.parseDouble(pairs[1]));

			}
			listOfSortedTitleMaps.add(sortedTitleMap);
		}
		
		/*
		 * From here you have access to
		 *
		 *	ArrayList<<HashMap<String, Double>>
		 *		-This contains a list hashmaps. Each String in the hashmap is a word in "this" request's title. The Double is the number of 
		 *			times that word appears in all titles in the dataset. The map is sorted in ascending order by values.
		 *
		 *	String request_text, request_title
		 *		- Simple Strings of both the request's text itself, and the string for the title. It is already cleaned, no punctuation,
		 *			and is separated by spaces.
		 *
		 *	Boolean pizza, edited
		 *		- Boolean values for each request whether or not this request got the requester pizza, and if they edited their request at any point
		 *
		 *	Double upvotes, downvotes, num_comments_request, num_comments_retrieval
		 *		- These variables names are self explanatory. Called almost identical to what they are in English, and in 
		 *			the JSON request themselves.
		 */	
	
	}
	
}
