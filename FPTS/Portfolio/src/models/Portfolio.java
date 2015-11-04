package models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Portfolio
 *
 * A Portfolio represents what a user owns in Equities
 *
 * @author Bill Dybas
 * @author Drew Heintz
 *
 */

public class Portfolio {
	private Map<Equity, Holding> holdings; // Equity & number of owned shares
	private Map<Equity, Holding> simulatedHoldings;
	private ArrayList<Account> accounts;
	
	private Map<Transaction, LocalDate> transactions;
	private ArrayList<Transaction> pendingTransactions;
	private ArrayList<Transaction> redoTransactions;
	
	/**
	 * Have the holdings changed? If they have we need to re-create the
	 * simulation map.
	 */
	private boolean holdingsChanged;

	/**
	 * Default Constructor for Portfolio
	 */
	public Portfolio(){
		this(new HashMap<Equity, Holding>());
	}

	/**
	 * Constructor for Portfolio when there is a specified HashMap of equities
	 *
	 * @param equities		the collection equities in this Portfolio
	 */
	public Portfolio(HashMap<Equity, Holding> holdings){
		this(holdings, new ArrayList<Account>());
	}

	public Portfolio(HashMap<Equity, Holding> holdings, ArrayList<Account> accounts){
		this(holdings, new ArrayList<Account>(), new HashMap<Transaction, LocalDate>());
	}

	public Portfolio(HashMap<Equity, Holding> holdings, ArrayList<Account> accounts, HashMap<Transaction, LocalDate> transactions){
		this.holdings = holdings;
		this.simulatedHoldings = new HashMap<>();
		this.accounts = accounts;
		this.transactions = transactions;
		this.pendingTransactions = new ArrayList<Transaction>();
		this.holdingsChanged = true;
		this.redoTransactions = new ArrayList<Transaction>();
	}

	public Map<Equity, Holding> getHoldings() {
		return this.holdings;
	}

	public Map<Equity, Holding> getSimulatedHoldings() {
	    if(holdingsChanged) {
	        resetSimulation();
	    }
	    return this.simulatedHoldings;
	}

	public ArrayList<Account> getAccounts() {
		return this.accounts;
	}
	
	/**
	 * Finds the Account with the given name
	 * 
	 * @param name	the name of the Account
	 * 
	 * @return		the Account, or null if it doesn't exist
	 */
	public Account getAccount(String name){
		for(Account a : accounts){
			if(a.getName().equals(name)){
				return a;
			}
		}
		return null;
	}

	public Map<Transaction, LocalDate> getTransactions(){
		return this.transactions;
	}

	/**
	 * Adds an Account to this Portfolio
	 *
	 * @param a		the Account to add
	 * @return		True if the Account was added
	 */
	public boolean addAccount(Account a){
		if(!accounts.contains(a)){
			accounts.add(a);
			return true;
		}
		return false;
	}
	
	public boolean addAccount(Account a, Double amount){
		if(addAccount(a) && addMoney(a, amount)){
			return true;
		}
		return false;
	}

	/**
	 * Removes an Account if it is in this Portfolio
	 *
	 * @param a		the Account to remove
	 *
	 * @return		True if the account was successfully removed
	 */
	public boolean removeAccount(Account a){
		return accounts.remove(a);
	}


	/**
	 * Adds a Holding to the Portfolio or adds to the amount
	 * of an Holding if it is already in the Portfolio.
	 *
	 * @param e			the Equity to add
	 * @param h			the Holding that specified how many bought, etc.
	 *
	 * @return			True if the Holding was successfully added/modified
	 */
	public boolean addHolding(Equity e, Holding h) {
		Transaction t = new BuyHoldingTransaction(holdings, e, h);

		if(t.execute()){
			clearRedo();
			pendingTransactions.add(t);
			holdingsChanged = true;
			return true;
		}
		else{
			return false;
		}
	}

	/**
	 * Adds a Holding to the Portfolio or adds to the amount
	 * of an Holding if it is already in the Portfolio. Withdraws
	 * money from the Account specified
	 *
	 * @param e			the Equity to add
	 * @param h			the Holding that specified how many bought, etc.
	 * @param a			the Account to withdraw money from
	 *
	 * @return			True if the Holding was successfully added/modified
	 */
	public boolean addHolding(Equity e, Holding h, Account a){
		Transaction t = new BuyHoldingTransaction(holdings, e, h, a);

		if(t.execute()){
			clearRedo();
			pendingTransactions.add(t);
			holdingsChanged = true;
			return true;
		}
		else{
			return false;
		}
	}

	/**
	 * Removes a Holding from the Portfolio or removes the amount
	 * of an Holding if it is already in the Portfolio.
	 *
	 * @param e			the Equity to remove
	 * @param h			the Holding that specified how many sold, etc.
	 *
	 * @return			True if the Holding was successfully removed/modified
	 */
	public boolean removeHolding(Equity e, Holding h) {
		Transaction t = new SellHoldingTransaction(holdings, e, h);

		if(t.execute()){
			clearRedo();
			pendingTransactions.add(t);
			holdingsChanged = true;
			return true;
		}
		else{
			return false;
		}
	}

	/**
	 * Removes a Holding from the Portfolio or removes the amount
	 * of an Holding if it is already in the Portfolio. Deposits
	 * money into the Account specified
	 *
	 * @param e			the Equity to remove
	 * @param h			the Holding that specified how many sold, etc.
	 * @param a			the Account to deposit the money
	 *
	 * @return			True if the Holding was successfully added/modified
	 */
	public boolean removeHolding(Equity e, Holding h, Account a){
		Transaction t = new SellHoldingTransaction(holdings, e, h, a);

		if(t.execute()){
			clearRedo();
			pendingTransactions.add(t);
			holdingsChanged = true;
			return true;
		}
		else{
			return false;
		}
	}

	public boolean addMoney(Account a, Double amount){
		if(accounts.contains(a)){
			Transaction t = new DepositAccountTransaction(a, amount, LocalDate.now());

			if(t.execute()){
				clearRedo();
				pendingTransactions.add(t);
				return true;
			}
			else{
				return false;
			}
		}
		else{
			return false;
		}
	}

	public boolean removeMoney(Account a, Double amount){
		if(accounts.contains(a)){
			Transaction t = new WithdrawAccountTransaction(a, amount, LocalDate.now());

			if(t.execute()){
				clearRedo();
				pendingTransactions.add(t);
				return true;
			}
			else{
				return false;
			}
		}
		else{
			return false;
		}
	}

	public boolean transferMoney(Account from, Account to, Double amount){
		if(accounts.contains(from) && accounts.contains(to)){
			Transaction t = new TransferAccountTransaction(from, to, amount, LocalDate.now());

			if(t.execute()){
				clearRedo();
				pendingTransactions.add(t);
				return true;
			}
			else{
				return false;
			}
		}
		else{
			return false;
		}
	}

	public boolean hasPendingTransactions() {
		return !pendingTransactions.isEmpty();
	}
	
	/**
	 * Saves all the Transactions that have happened since the last save
	 */
	public void saveTransactions(){
		for(Transaction t : pendingTransactions){
			if(t instanceof HoldingTransaction){
				transactions.put(t, ((HoldingTransaction) t).getHolding().getDate());
			}
			else if(t instanceof AccountTransaction){
				transactions.put(t, ((AccountTransaction) t).getDate());
			}
		}
		// Reset the pending list
		pendingTransactions = new ArrayList<Transaction>();
		clearRedo();
	}

	public void exitWithoutSaving(){
		// Reverse the pending list to unexecute the most recent first
		Collections.reverse(pendingTransactions);

		for(Transaction t : pendingTransactions){
			t.unexecute();
		}

		// Reset the pending list
		pendingTransactions = new ArrayList<Transaction>();
	}

	public void resetSimulation() {
	    holdingsChanged = false;
	    simulatedHoldings = cloneHoldings();
	}

	/**
	 * Make a clone of the holdings map.
	 *
	 * @return the cloned equity map
	 */
	private Map<Equity, Holding> cloneHoldings() {
	    HashMap<Equity, Holding> copy = new HashMap<Equity, Holding>();
        for(Map.Entry<Equity, Holding> entry : holdings.entrySet()){
            Equity e = entry.getKey();
            Holding h = entry.getValue();

            Equity eCopy = new Equity(e.getTickerSymbol(), e.getEquityName(),
                                        e.getCurrentPrice(), e.getIndices());
            Holding hCopy = new Holding(eCopy, h.getShares(), h.getPricePerShare(), h.getDate());
            copy.put(eCopy, hCopy);
        }
        return copy;   
	}
	
	public void undo() {
		if(!pendingTransactions.isEmpty()){
			Transaction e = pendingTransactions.get(pendingTransactions.size() - 1);
			e.unexecute();
			redoTransactions.add(e);
			pendingTransactions.remove(e);
		}
	}
	
	public void redo(){
		if(!redoTransactions.isEmpty()){
			Transaction e = redoTransactions.get(redoTransactions.size() - 1);
			e.execute();
			//TODO: For some reason, when undoing the BuyHoldingTransaction
			//TODO: it doesn't add the shares back into the Holding
			pendingTransactions.add(e);
			redoTransactions.remove(e);
		}
	}
	
	/**
	 * If there are Transactions in the redo stack, they will be removed
	 */
	private void clearRedo(){
		if(!redoTransactions.isEmpty()){
			redoTransactions.clear();
		}
	}
}
