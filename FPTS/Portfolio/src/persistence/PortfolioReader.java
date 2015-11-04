package persistence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import models.*;

/**
 * PortfolioReader
 * 
 * Imports a Portfolio from a properly formatted
 * JSON text file.
 * 
 * @author Bill Dybas
 *
 */
public class PortfolioReader {
	
	/**
	 * Constructor for PortfolioReader
	 * 
	 * @param allEquities	all the Equities in the System
	 */
	public PortfolioReader(){}
	
	/**
	 * Reads in a User's Portfolio given their username
	 * 
	 * @param username
	 * 
	 * @return		a fully-formed Portfolio
	 * 
	 * @throws FileNotFoundException
	 */
	public Portfolio read(String username) throws FileNotFoundException{
	    
		JsonReader jr = null;
		try {
		    File portFile = PortfolioWriter.getPortfolioFile(username);
		    jr = Json.createReader(
		            new BufferedReader(new FileReader(portFile)));
		    JsonObject jo = jr.readObject();
		    Portfolio p = buildPortfolio(jo);
		    return p;
		} finally {
		    if(jr != null){
	            jr.close();
	        }
		}
	}
	
	//TODO: CHECK FOR MALFORMED DATA AND REPLACE IT IF NECESSARY
	
	/**
	 * Builds a Portfolio object given a JsonObject representing a Portfolio
	 * 
	 * @param jo	the formated Portfolio data
	 * 
	 * @return		a fully-formed Portfolio
	 */
	public Portfolio buildPortfolio(JsonObject jo){
		HashMap<Equity, Holding> holdings = new HashMap<Equity, Holding>();
		ArrayList<Account> allBankAccounts = new ArrayList<Account>();
		ArrayList<Account> allMoneyMarketAccounts = new ArrayList<Account>();
		ArrayList<Account> allAccounts = new ArrayList<Account>();
		HashMap<Transaction, LocalDate> transactions = new HashMap<Transaction, LocalDate>();
		HashMap<String, Equity> allEquities = EquitiesSingleton.getInstance().getEquities();
		
		// Get the User's Holdings
		JsonArray userHoldings = jo.getJsonArray("userHoldings");
		for(int i = 0; i < userHoldings.size(); i++){
			JsonObject holding = userHoldings.getJsonObject(i);
			String tickerSymbol = holding.getString("tickerSymbol");
			double shares = holding.getJsonNumber("shares").doubleValue();
			double pricePerShare = holding.getJsonNumber("pricePerShare").doubleValue();
			LocalDate date = LocalDate.parse(holding.getString("date"));
			
			Equity e = allEquities.get(tickerSymbol);
			if(e != null) {
			    Holding h = new Holding(e, shares, pricePerShare, date);
	            holdings.put(e, h);
			} else {
			    System.err.println("Invalid ticker symbol " + tickerSymbol);
			}
		}
		
		// Get the User's BankAccounts
		JsonArray bankAccounts = jo.getJsonArray("bankAccounts");
		for(int i = 0; i < bankAccounts.size(); i++){
			JsonObject bankAccount = bankAccounts.getJsonObject(i);
			String bankName = bankAccount.getString("bankName");
			double balance = bankAccount.getJsonNumber("balance").doubleValue();
			LocalDate dateCreated = LocalDate.parse(bankAccount.getString("dateCreated"));
			allBankAccounts.add(new BankAccount(bankName, balance, dateCreated));
		}
		
		// Get the User's MoneyMarketAccounts
		JsonArray moneyMarketAccounts = jo.getJsonArray("moneyMarketAccounts");
		for(int i = 0; i < moneyMarketAccounts.size(); i++){
			JsonObject moneyMarketAccount = moneyMarketAccounts.getJsonObject(i);
			String accountName = moneyMarketAccount.getString("accountName");
			double balance = moneyMarketAccount.getJsonNumber("balance").doubleValue();
			LocalDate dateCreated = LocalDate.parse(moneyMarketAccount.getString("dateCreated"));
			allMoneyMarketAccounts.add(new MoneyMarketAccount(accountName, balance, dateCreated));
		}
		
		JsonObject t = jo.getJsonObject("transactions");
		JsonArray buyHolding = t.getJsonArray("buyHolding");
		JsonArray sellHolding = t.getJsonArray("sellHolding");
		JsonArray depositAccount = t.getJsonArray("depositAccount");
		JsonArray withdrawAccount = t.getJsonArray("withdrawAccount");
		JsonArray transferAccount = t.getJsonArray("transferAccount");
		
		for(int i = 0; i < buyHolding.size(); i++){
			JsonObject buyHoldingTransaction = buyHolding.getJsonObject(i);
			String equity = buyHoldingTransaction.getString("equity");
			
			JsonObject holding = buyHoldingTransaction.getJsonObject("holding");
			double shares = holding.getJsonNumber("shares").doubleValue();
			double pricePerShare = holding.getJsonNumber("pricePerShare").doubleValue(); 
			LocalDate buyDate = LocalDate.parse(holding.getString("buyDate"));
			
			JsonObject account = buyHoldingTransaction.getJsonObject("account");
			String name = account.getString("name");
			String type = account.getString("type");
			
			Equity e = allEquities.get(equity);
			Holding h = new Holding(e, shares, pricePerShare, buyDate);
			Account acct = null;
			
			if(type.equals("Bank Account")){
				for(Account a : allBankAccounts){
					if(a.getName().equals(name)){
						acct = a;
						break;
					}
				}
				acct = new BankAccount(name);
			}
			else if(type.equals("Money Market Account")){
				for(Account a : allMoneyMarketAccounts){
					if(a.getName().equals(name)){
						acct = a;
						break;
					}
				}
				acct = new MoneyMarketAccount(name);
			}
			else if(type.equals("None")){
				acct = new NullAccount();
			}
			
			transactions.put(new BuyHoldingTransaction(holdings, e, h, acct), buyDate);
		}
		
		for(int i = 0; i < sellHolding.size(); i++){
			JsonObject sellHoldingTransaction = sellHolding.getJsonObject(i);
			String equity = sellHoldingTransaction.getString("equity");
			
			JsonObject holding = sellHoldingTransaction.getJsonObject("holding");
			double shares = holding.getJsonNumber("shares").doubleValue();
            double pricePerShare = holding.getJsonNumber("pricePerShare").doubleValue();
			LocalDate sellDate = LocalDate.parse(holding.getString("sellDate"));
			
			JsonObject account = sellHoldingTransaction.getJsonObject("account");
			String name = account.getString("name");
			String type = account.getString("type");
			
			Equity e = allEquities.get(equity);
			Holding h = new Holding(e, shares, pricePerShare, sellDate);
			Account acct = null;
			
			if(type.equals("Bank Account")){
				for(Account a : allBankAccounts){
					if(a.getName().equals(name)){
						acct = a;
						break;
					}
				}
				acct = new BankAccount(name);
			}
			else if(type.equals("Money Market Account")){
				for(Account a : allMoneyMarketAccounts){
					if(a.getName().equals(name)){
						acct = a;
						break;
					}
				}
				acct = new MoneyMarketAccount(name);
			}
			else if(type.equals("None")){
				acct = new NullAccount();
			}
			
			transactions.put(new SellHoldingTransaction(holdings, e, h, acct), sellDate);
		}
		
		for(int i = 0; i < depositAccount.size(); i++){
			JsonObject depositAccountTransaction = depositAccount.getJsonObject(i);
			
			JsonObject account = depositAccountTransaction.getJsonObject("account");
			String name = account.getString("name");
			String type = account.getString("type");
			
			double amount = depositAccountTransaction.getJsonNumber("amount").doubleValue();
			LocalDate date = LocalDate.parse(depositAccountTransaction.getString("date"));
			
			Account acct = null;
			
			if(type.equals("Bank Account")){
				for(Account a : allBankAccounts){
					if(a.getName().equals(name)){
						acct = a;
						break;
					}
				}
				acct = new BankAccount(name);
			}
			else if(type.equals("Money Market Account")){
				for(Account a : allMoneyMarketAccounts){
					if(a.getName().equals(name)){
						acct = a;
						break;
					}
				}
				acct = new MoneyMarketAccount(name);
			}
			
			transactions.put(new DepositAccountTransaction(acct, amount, date), date);
		}
		
		for(int i = 0; i < withdrawAccount.size(); i++){
			JsonObject withdrawAccountTransaction = withdrawAccount.getJsonObject(i);
			
			JsonObject account = withdrawAccountTransaction.getJsonObject("account");
			String name = account.getString("name");
			String type = account.getString("type");
			
			double amount = withdrawAccountTransaction.getJsonNumber("amount").doubleValue();
			LocalDate date = LocalDate.parse(withdrawAccountTransaction.getString("date"));
			
			Account acct = null;
			
			if(type.equals("Bank Account")){
				for(Account a : allBankAccounts){
					if(a.getName().equals(name)){
						acct = a;
						break;
					}
				}
				acct = new BankAccount(name);
			}
			else if(type.equals("Money Market Account")){
				for(Account a : allMoneyMarketAccounts){
					if(a.getName().equals(name)){
						acct = a;
						break;
					}
				}
				acct = new MoneyMarketAccount(name);
			}
			else if(type.equals("None")){
				acct = new NullAccount();
			}
			
			transactions.put(new WithdrawAccountTransaction(acct, amount, date), date);
		}
		
		for(int i = 0; i < transferAccount.size(); i++){
			JsonObject transferAccountTransaction = transferAccount.getJsonObject(i);
			
			JsonObject fromAccount = transferAccountTransaction.getJsonObject("fromAccount");
			String fromName = fromAccount.getString("name");
			String fromType = fromAccount.getString("type");
			
			JsonObject toAccount = transferAccountTransaction.getJsonObject("toAccount");
			String toName = toAccount.getString("name");
			String toType = toAccount.getString("type");

			double amount = transferAccountTransaction.getJsonNumber("amount").doubleValue();
			LocalDate date = LocalDate.parse(transferAccountTransaction.getString("date"));
			
			Account fromAcct = null;
			Account toAcct = null;
			
			if(fromType.equals("Bank Account")){
				for(Account a : allBankAccounts){
					if(a.getName().equals(fromName)){
						fromAcct = a;
						break;
					}
				}
				fromAcct = new BankAccount(fromName);
			}
			else if(fromType.equals("Money Market Account")){
				for(Account a : allMoneyMarketAccounts){
					if(a.getName().equals(fromName)){
						fromAcct = a;
						break;
					}
				}
				fromAcct = new MoneyMarketAccount(fromName);
			}
			
			if(toType.equals("Bank Account")){
				for(Account a : allBankAccounts){
					if(a.getName().equals(toName)){
						toAcct = a;
						break;
					}
				}
				toAcct = new BankAccount(toName);
			}
			else if(toType.equals("Money Market Account")){
				for(Account a : allMoneyMarketAccounts){
					if(a.getName().equals(toName)){
						toAcct = a;
						break;
					}
				}
				toAcct = new MoneyMarketAccount(toName);
			}
			
			transactions.put(new TransferAccountTransaction(fromAcct, toAcct, amount, date), date);
		}
		
		for(Account a : allBankAccounts){
			allAccounts.add(a);
		}
		
		for(Account a : allMoneyMarketAccounts){
			allAccounts.add(a);
		}
		
		return new Portfolio(holdings, allAccounts, transactions);
	}
}
