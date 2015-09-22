import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

public class CSVtoARFF {

	static FileWriter fw = null;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			fw= new FileWriter("cleaned_data/pizzaRequests.arff");
		} 
		catch (IOException e2) {
			e2.printStackTrace();
		}
		
		String splitby = ",";

		/**The following is the file to use. **/
		String file = args[0];

		/**Our buffered reader**/
		BufferedReader br = null;
        String line = "";

	    try {
			fw.write("@relation pizzarequests"+ "\n" +"\n");
		    fw.write("@attribute PostEdited {0,1}"+"\n");
		    fw.write("@attribute NumberOfUpVotes numeric" + "\n");
		    fw.write("@attribute NumberofDownVotes numeric" + "\n");
		    fw.write("@attribute CommentsOnRequest numeric" + "\n");
		    fw.write("@attribute CommentsOnRetrieval numeric" + "\n");
		    fw.write("@attribute WordScore numeric" + "\n");
		    fw.write("@attribute TitleScore numeric" + "\n");
		    fw.write("@attribute class {0,1}"+"\n");
		    fw.write("\n"+ "@data" + "\n" + "\n");
		} 
	    catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
        try {
			br = new BufferedReader(new FileReader(file));
	        line = br.readLine();
	        while( (line = br.readLine()) != null) {
				String[] read = line.split(splitby);
				stringParser(read);
			}
		} 
		catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		} 
		finally {
			
			if (br != null) {
				try{
					br.close();
				} 
				catch (IOException e){
					e.printStackTrace();
				}
			}
		}
		try {
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	/**
	 * Name: stringParser
	 *
	 * Loops through a line from the csv file and puts
	 * the double in the correct list.
	 *
	 * @param	args	List of strings to pass. NOT USED
	 * @return	N/A
	 **/
	public static void stringParser(String[] arg){
		int idx = 0;
		String reconstruct = "";
		for(;idx<arg.length-1;++idx){
			reconstruct+= arg[idx] + ",";
		}
		
		reconstruct += arg[arg.length-1]+ "\n";
		try {
			fw.write(reconstruct);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
}
