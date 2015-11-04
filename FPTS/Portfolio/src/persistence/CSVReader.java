package persistence;

import java.util.ArrayList;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * CSVReader
 * 
 * Given a path, this will read in the csv file, line by line.
 * 
 * @author Bill Dybas
 * 
 */
public class CSVReader {
	private File csvfile;
	private ArrayList<String[]> lines;
	
	/**
	 * CSVReader Constructor
	 * 
	 * @param path		the path of the csv file to read in
	 */
	public CSVReader(String path) {
		this.csvfile = new File(path);
		this.lines = new ArrayList<String[]>();
	}
	
	/**
	 * Reads in the data from the specified csv file
	 */
	public void read() {
		BufferedReader br = null;
		String line = "";
		
		try {
			// Make a new BufferedReader
			br = new BufferedReader(new FileReader(this.csvfile));
			
			// Read in every line
			while ((line = br.readLine()) != null) {
				// Turn each line into an array, split based on commas that are not in quotes
				String[] parsedLine = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
				
				// Get rid of the quotes
				for(int i = 0; i < parsedLine.length; i++) {
					parsedLine[i] = parsedLine[i].replace("\"", "");
				}
				
				// Add the line to the ArrayList of all lines
				this.lines.add(parsedLine);
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Provides the data that was read in 
	 * (read() must be called first otherwise this will be empty)
	 * 
	 * @return ArrayList of String arrays, each array 
	 * representing a line that was read in
	 */
	public ArrayList<String[]> getData(){
		return this.lines;
	}
}
