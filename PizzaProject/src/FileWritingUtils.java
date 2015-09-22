import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.opencsv.*;

import java.util.Map.Entry;

/*
 * @author Bill Alsibai, Tyler Russell, Victor Paiz, Akia Vongdara
 */

public class FileWritingUtils {
	
	public FileWritingUtils(){}//Empty constructor.
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void writeTitleScores(ArrayList<Request> list) {
		String titles = "cleaned_data/titlescores.txt";
		try {
			FileWriter writer = new FileWriter(new File(titles));
			PrintWriter printer = new PrintWriter(writer);
			HashMap<String, Double> sortedTitleMap;
			
			for(Request req: list) {
				sortedTitleMap = req.getTitlePopularities();
				Iterator it = sortedTitleMap.entrySet().iterator();
				String output = "";
				while(it.hasNext()) {
					Map.Entry pair = (Map.Entry) it.next();
					output = pair.getKey() + "=" + pair.getValue() + ",";
					printer.print(output);
				}
				printer.println();
			}
			printer.close();
			
		} catch (IOException e) {}
	}
	
	public void writeCSV(ArrayList<Request> list) {
		try {
			
			String csv = "cleaned_data/cleaned_requests.csv";
			CSVWriter writer = new CSVWriter(new FileWriter(csv));
			
			boolean pizza, edited;
			double upvotes, downvotes, num_comments_request, num_comments_retrieval;
			int tscore, wscore;
			
			for(Request req: list) {
				
				pizza = req.getPizza();
				edited = req.wasEdited();
				upvotes = req.getUpVotes();
				downvotes = req.getDownVotes();
				num_comments_request = req.getNumCommentsRequest();
				num_comments_retrieval = req.getNumCommentsRetrieval();
				tscore = req.getTitleScore();
				wscore = req.getWordScore();
				int t, f;
				if(pizza)
					t = 1;
				else 
					t = 0;
				if(edited)
					f = 1;
				else
					f = 0;
				String[] arr = new String[]{String.valueOf(f), 
											String.valueOf(upvotes), String.valueOf(downvotes), String.valueOf(num_comments_request), 
											String.valueOf(num_comments_retrieval), String.valueOf(wscore), String.valueOf(tscore),
											String.valueOf(t)};
				
				writer.writeNext(arr);
			}
			writer.close();
		}
		catch(Exception e){ e.printStackTrace(); }
	}
	
	
	public void writebank(HashMap<String, Integer> occurences) {
		try {
			HashMap<String, Integer> map = new HashMap<String, Integer>();
			File file = new File("cleaned_data/freqbank.txt");
			FileWriter writer = new FileWriter(file);
			PrintWriter printer = new PrintWriter(writer);
			int total = occurences.size();
			for(Entry<String, Integer> element: occurences.entrySet()) {
				String word = element.getKey();
				Integer i = element.getValue();
				map.put(word,  i);
			}
			
			HashMap<String, Integer> bank = sortByValues(map);
			System.out.println("\n-----------------------------------\n");
			for(Entry<String, Integer> element: bank.entrySet()) {
				System.out.println(element.getKey() + "," + element.getValue());
				printer.println(element.getKey() + "," + element.getValue());
			}
		}
		catch(Exception e){};
	}

	/*
	 * Creates the wordbank.txt that contains a mapping from word to its probability
	 * of existing in the dataset
	 * @param HashMap<String, Integer> totalOccurences, HashMap<String, Double> wordbank
	 */
	@SuppressWarnings("resource")
	public void writeWordbank(HashMap<String, Integer> totalOccurences, HashMap<String, Double> wordbank) {
		try {
			File frequencies = new File("cleaned_data/wordbank.txt");
			FileWriter writer = new FileWriter(frequencies);
			PrintWriter printer = new PrintWriter(writer);
			int total = totalOccurences.size();
			double num = total;//Cast total number of words to double
			for(Entry<String, Integer> element: totalOccurences.entrySet()) {
				String word = element.getKey();
				Integer i = element.getValue();
				Double probability = i / num;
				wordbank.put(word, round(probability, 5));
			}
			
			HashMap<String, Double> bank = sortByValues(wordbank);
			
			NumberFormat formatter = new DecimalFormat("#.#####");
			for(Entry<String, Double> element: bank.entrySet())
				printer.println(element.getKey() + "," + formatter.format(element.getValue()));
		}
		catch(IOException io) {
			System.err.println("Error writing results to output text file. Exiting...");
			System.exit(0);
		}
		catch(Exception e) {
			System.err.println("Non-IOException caught. Exiting...");
			System.exit(0);
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
	 * Given a double and an integer number of decimal places,
	 * rounds the given value to the number of places.
	 * @param double value, int places
	 * @return double
	 */
	public double round(double value, int places) {
		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}
}
