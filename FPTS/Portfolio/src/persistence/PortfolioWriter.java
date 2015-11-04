package persistence;

import models.*;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

/**
 * PortfolioWriter
 * 
 * Exports a Portfolio to a JSON text file.
 * 
 * @author Bill Dybas
 *
 */
public class PortfolioWriter {
    
    public static final File PORTFOLIO_DIRECTORY = new File("Portfolio");

	public PortfolioWriter(){}
	
	/**
	 * Writes out a User's Portfolio given the User
	 * 
	 * @param u		User
	 */
	public void write(User u){
		JsonWriter jw = null;
		
		try {		    
		    File portFile = getPortfolioFile(u.getUserName());
			jw = Json.createWriter(
			        new BufferedWriter(new FileWriter(portFile)));
			JsonObject job = buildPortfolio(u);
			jw.writeObject(job);
			jw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Builds a JsonObject representing a User's Portfolio
	 * 
	 * @param u		User
	 * 
	 * @return		JsonObject representing the Portfolio
	 */
	public JsonObject buildPortfolio(User u){
		Portfolio p = u.getPortfolio();
		Map<Equity, Holding> holdings = p.getHoldings();
		ArrayList<Account> accounts = p.getAccounts();
		Map<Transaction, LocalDate> allTransactions = p.getTransactions();
		
		// JSON Array of Equities
		JsonArrayBuilder userHoldings = Json.createArrayBuilder();
		
		// Iterate over a User's Holdings
		for(Holding h : holdings.values()){
			userHoldings.add(Json.createObjectBuilder()
							.add("tickerSymbol", h.getEquity().getTickerSymbol())
							.add("shares", h.getShares())
							.add("pricePerShare", h.getPricePerShare())
							.add("date", h.getDate().toString()));
		}
		
		// JSON Arrays of BankAccounts and MoneyMarketAccounts
		JsonArrayBuilder bankAccounts = Json.createArrayBuilder();
		JsonArrayBuilder moneyMarketAccounts = Json.createArrayBuilder();
		
		// Iterate over a User's Accounts
		for(Account a : accounts){
			if(a instanceof BankAccount){
				bankAccounts.add(Json.createObjectBuilder()
								.add("bankName", a.getName())
								.add("balance", a.getCurrentBalance())
								.add("dateCreated", a.getDateCreated().toString()));
			}
			else if (a instanceof MoneyMarketAccount){
				moneyMarketAccounts.add(Json.createObjectBuilder()
								.add("accountName", a.getName())
								.add("balance", a.getCurrentBalance())
								.add("dateCreated", a.getDateCreated().toString()));
			}
		}
		
		JsonObjectBuilder transactions = Json.createObjectBuilder();
		JsonArrayBuilder buyHolding = Json.createArrayBuilder();
		JsonArrayBuilder sellHolding = Json.createArrayBuilder();
		JsonArrayBuilder depositAccount = Json.createArrayBuilder();
		JsonArrayBuilder withdrawAccount = Json.createArrayBuilder();
		JsonArrayBuilder transferAccount = Json.createArrayBuilder();

		for(Map.Entry<Transaction, LocalDate> entry : allTransactions.entrySet()){
			Transaction t = entry.getKey();
			LocalDate d = entry.getValue();
			
			Account a = t.getAccount();
			String name = a.getName();
			String type = a.getType();
			
			if(t instanceof HoldingTransaction){
				Holding h = ((HoldingTransaction) t).getHolding();
				
				if(t instanceof BuyHoldingTransaction){
					buyHolding.add(Json.createObjectBuilder()
								.add("equity", h.getEquity().getTickerSymbol())
								.add("holding", Json.createObjectBuilder()
										.add("shares", h.getShares())
										.add("pricePerShare", h.getPricePerShare())
										.add("buyDate", h.getDate().toString()))
								.add("account", Json.createObjectBuilder()
										.add("name", name)
										.add("type", type)));
					
				}
				else if(t instanceof SellHoldingTransaction){
					sellHolding.add(Json.createObjectBuilder()
								.add("equity", h.getEquity().getTickerSymbol())
								.add("holding", Json.createObjectBuilder()
										.add("shares", h.getShares())
										.add("pricePerShare", h.getPricePerShare())
										.add("sellDate", h.getDate().toString()))
								.add("account", Json.createObjectBuilder()
										.add("name", name)
										.add("type", type)));
				}
			}
			else if(t instanceof AccountTransaction){
				if(t instanceof DepositAccountTransaction){
					depositAccount.add(Json.createObjectBuilder()
									.add("account", Json.createObjectBuilder()
											.add("name", name)
											.add("type", type))
									.add("amount", ((AccountTransaction) t).getAmount())
									.add("date", d.toString()));
				}
				else if(t instanceof WithdrawAccountTransaction){
					withdrawAccount.add(Json.createObjectBuilder()
									.add("account", Json.createObjectBuilder()
											.add("name", name)
											.add("type", type))
									.add("amount", ((AccountTransaction) t).getAmount())
									.add("date", d.toString()));
					
				}
				else if(t instanceof TransferAccountTransaction){
					Account o = ((TransferAccountTransaction) t).getOtherAccount();
					String otherName = null;
					String otherType = null;
					
					if(o != null){
						otherName = o.getName();
						otherType = o.getType();
					}
					
					transferAccount.add(Json.createObjectBuilder()
									.add("fromAccount", Json.createObjectBuilder()
											.add("name", name)
											.add("type", type))
									.add("toAccount", Json.createObjectBuilder()
											.add("name", otherName)
											.add("type", otherType))
									.add("amount", ((AccountTransaction) t).getAmount())
									.add("date", d.toString()));
				}
			}
		}
		
		transactions.add("buyHolding", buyHolding);
		transactions.add("sellHolding", sellHolding);
		transactions.add("depositAccount", depositAccount);
		transactions.add("withdrawAccount", withdrawAccount);
		transactions.add("transferAccount", transferAccount);
		
		JsonObjectBuilder portfolio = Json.createObjectBuilder();
		portfolio.add("userHoldings", userHoldings);
		portfolio.add("bankAccounts", bankAccounts);
		portfolio.add("moneyMarketAccounts", moneyMarketAccounts);
		portfolio.add("transactions", transactions);
		
		return portfolio.build();
	}
	
	public static File getPortfolioFile(String username) {
	    if(!PORTFOLIO_DIRECTORY.isDirectory()) {
            PORTFOLIO_DIRECTORY.mkdirs();
        }
	    
        return new File(PORTFOLIO_DIRECTORY, username + ".json");
	}
}
