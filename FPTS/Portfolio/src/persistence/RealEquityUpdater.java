package persistence;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import models.Equity;

/**
 * Updates the Equities based on Real-time data from the Yahoo Finance API
 * 
 * @author Bill Dybas
 * @author Drew Heintz
 *
 */
public class RealEquityUpdater implements EquityUpdater{

	private HashMap<String, Equity> equities;
	private ArrayList<Callable<String>> tickerSymbols;
	private boolean callablesBuilt;
	private static ExecutorService executorPool;
	
	// Individual Threads for Each Call to the API
	// Each Call Queries 1 Ticker Symbol
	class PriceUpdater implements Callable<String>{
		private String tickerSymbol;
		
		public PriceUpdater(String tickerSymbol){
			this.tickerSymbol = tickerSymbol;
		}
		
		public String call(){
			// Replace '^' with their equivalent hex symbol
			String query = "%22" + this.tickerSymbol + "%22";
			if(query.contains("^")){
				query.replace("^", "%5E");
			}
			
			// Prepare the API call
			String url = "http://query.yahooapis.com/v1/public/yql?q=select%20" + 
	                "LastTradePriceOnly" + 
	                "%20from%20yahoo.finance.quotes%20where%20symbol%20in%20(" + 
	                query + ")&env=store://datatables.org/alltableswithkeys";
			
			InputStream in = null;
			
			try {
				// Open a connection
		        URL YahooURL = new URL(url);
		        HttpURLConnection con = (HttpURLConnection) YahooURL.openConnection();
		        
		        // Set the HTTP Request type method to GET (Default: GET)
		        con.setRequestMethod("GET");
			    con.setConnectTimeout(60000);	//1 minute timeout
			    con.setReadTimeout(60000);
			    
			    // Grab the input stream so we can read the contents and so
			    // we can close() it later.
			    in = con.getInputStream();
			    
			    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			    Document doc = dBuilder.parse(in);
                doc.getDocumentElement().normalize();
                
                // Parse it for LastTradePriceOnly
                NodeList nList = doc.getElementsByTagName("LastTradePriceOnly");
                
                
                // Update the price of the equity - should only be 1
                for(int i = 0; i < nList.getLength(); i++){
                    Equity equityToModify = equities.get(this.tickerSymbol);

                    try {
                        double lastTradePrice = Double.parseDouble(nList.item(i).getTextContent());
                        equityToModify.setCurrentPrice(lastTradePrice);
                        
                    } catch(NumberFormatException e){
                        // Yahoo Finance Doesn't Return Any 
                        // Data with the Given Ticker Symbol
                        // Just let the Loop Continue
                        // and Use their Prices from the CSV File
                        // Note: Usually Happens on Equities that have '^' in them
                    }
                }
			   
			} catch(IOException e){
				System.err.println("Error with Yahoo API Call");
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
                System.err.println("Error with reading XML Price file");
                e.printStackTrace();
            } catch (SAXException e) {
                System.err.println("Error with reading XML Price file");
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
			return this.tickerSymbol;
		}
	}
	
	public RealEquityUpdater(){
		this.equities = EquitiesSingleton.getInstance().getEquities();
		this.tickerSymbols = new ArrayList<Callable<String>>();
		this.callablesBuilt = false;
	}
	
	@Override
	public void update() {
		// Only build the list of equities to update once
		if(!callablesBuilt){
			for(String s : this.equities.keySet()){
				tickerSymbols.add(new PriceUpdater(s));
			}
			callablesBuilt = true;
		}
		
		// Execute the threads
		executorPool = Executors.newFixedThreadPool(25);
		try {
			executorPool.invokeAll(tickerSymbols);
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
            if (executorPool != null) {
                executorPool.shutdown();
            }
        }
	}
}
