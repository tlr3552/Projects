package controllers;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import models.Account;
import models.Equity;
import models.Holding;
import persistence.EquitiesSingleton;

/**
 * The EventHandler that handles the entire process
 * of buying a holding
 * 
 * @author Bill Dybas
 * @author Tyler Russell
 * 
 */
public class BuyHoldingHandler implements EventHandler<ActionEvent> {
	private MediatorController mc;
	private Stage equitiesDialog, accountsDialog;
	
	private String equitySymbolChosen;
	private Equity equityChosen;
	private double sharesPurchased;
	private Account accountChosen = null;
	
	private boolean displayEquityList;
	
	// Get the Equities in the System
	private HashMap<String, Equity> equities = EquitiesSingleton.getInstance().getEquities();

	/**
	 * Constructor for when the Equity to buy is unknown
	 */
	public BuyHoldingHandler(MediatorController mc) {
		this.mc = mc;
		this.displayEquityList = true;
	}
	
	/**
	 * Constructor for when the Equity to buy is known
	 * 
	 * @param tickerSymbol - the tickerSymbol of the Equity
	 */
	public BuyHoldingHandler(MediatorController mc, String tickerSymbol){
		this.mc = mc;
		this.displayEquityList = false;
		this.equitySymbolChosen = tickerSymbol;
	}

	@Override
	public void handle(ActionEvent event) {
		
		// If an Equity wasn't specified,
		// start by showing the list of equities to pick from
		if(displayEquityList){
			showEquities();
		}
		// Otherwise, start the flow with the buy amount
		else {
			equityChosen = equities.get(equitySymbolChosen);
			if(showBuyNumber()){
				showAccountChoice();
			}
		}
	} 
	
	private void showEquities(){
		final ArrayList<String> detailsList = new ArrayList<String>();

		// Sort the Equities by their tickerSymbol
		ArrayList<Equity> sortedEquities = new ArrayList<Equity>();
		for(Equity equity : equities.values()){
			sortedEquities.add(equity);
		}
		Collections.sort(sortedEquities, new Comparator<Equity>(){
			@Override
			public int compare(Equity e1, Equity e2) {
				return e1.getTickerSymbol().compareTo(e2.getTickerSymbol());
			}
		});

		// Display All the Equities a User can Choose From
		for(Equity equity : sortedEquities){
			String tickerSymbol = equity.getTickerSymbol();
			String name = equity.getEquityName();
			String price = String.valueOf(equity.getCurrentPrice());
			ArrayList<String> indicies = equity.getIndices();

			String item =  "Ticker Symbol: " + tickerSymbol + "\n";
		     	   item += "Equity Name: " + name + "\n";
		     	   item += "Price Per Share: " + price + "\n";
		     	   item += "Indicies: ";

		    for(String str: indicies)
		    	item += str + ", ";
		    // Remove the last comma
    		item = item.substring(0, item.length() - 2);

    		detailsList.add(item);
		}
		
		// Make those Equities able to be selected
		final ObservableList<String> equitiesList = FXCollections.observableArrayList(detailsList);
		mc.getAllHoldingsListView().setItems(equitiesList);
		
		// Add a Listener if an Equity is Selected
		mc.getAllHoldingsListView().setOnMouseClicked(new EventHandler<MouseEvent> () {
			@Override
			public void handle(MouseEvent click) {
				// Gets the text selected and parses it to get the Ticker Symbol
				equitySymbolChosen = mc.parseEquity(mc.getAllHoldingsListView().getSelectionModel().getSelectedItem());
				// Gets the Equity with that ticker symbol
				equityChosen = equities.get(equitySymbolChosen);
				equitiesDialog.close();
				if(showBuyNumber()){
					showAccountChoice();
				}
			}
		});
		
		// Set up and display the window with
		// the properties created above
		equitiesDialog = new Stage();
		equitiesDialog.initStyle(StageStyle.UTILITY);
		equitiesDialog.setTitle("Select An Equity to Purchase");
		Scene scene = new Scene(new Group(mc.getAllHoldingsListView()),
				mc.getAllHoldingsListView().getMinWidth(),
				mc.getAllHoldingsListView().getMinHeight());

		equitiesDialog.setScene(scene);
		equitiesDialog.show();
	}
	
	private boolean showBuyNumber(){
		boolean finished = false;
		boolean correct = false;
		double amount = 0;
		
		// Show a Dialog to input how many shares to buy
		TextInputDialog t = new TextInputDialog();
		t.setContentText("Buy how many shares?");
		
		Optional<String> result = t.showAndWait();
		
		// Only Accept Correct Input
		while(result.isPresent() && !correct){
			try{
				// Try to parse the input
				amount = Double.parseDouble(result.get());
				
				// Only Accept Positive Numbers
				if(amount > 0){
					correct = true;
				}
				else{
					Alert alert = new Alert(AlertType.INFORMATION);
			    	alert.setTitle("Information Dialog");
			    	alert.setHeaderText(null);
			    	alert.setContentText("You must enter a positive value.");
			    	alert.showAndWait();
			    	result = t.showAndWait();
				}
			} catch(NumberFormatException e){
				// If parseDouble tries to parse non-number input
				Alert alert = new Alert(AlertType.INFORMATION);
		    	alert.setTitle("Information Dialog");
		    	alert.setHeaderText(null);
		    	alert.setContentText("You must enter a numeric value.");
		    	alert.showAndWait();
		    	result = t.showAndWait();
			} catch (NoSuchElementException e){
				// The User Closed the Dialog
				// Break out of the while loop to 
				// prevent and infinite loop
				break;
			}
		}
		
		if(correct){
			// Display and modify the number to 2 decimal places,
			// as we only are supporting 2 decimal places
			DecimalFormat df = new DecimalFormat("#.00");
			amount = Math.floor(amount * 100) / 100;
			
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setContentText("Buy " + df.format(amount) + " shares?" );

			Optional<ButtonType> res = alert.showAndWait();
			if (res.get() == ButtonType.OK){
				sharesPurchased = amount;
				finished = true;
			}
		}
		
		return finished;
	}
	
	private void showAccountChoice(){
		// If they have an Account, show them this window,
		// otherwise, just buy the Holding
		if(!mc.getPortfolio().getAccounts().isEmpty()){
			Alert al = new Alert(AlertType.CONFIRMATION);
			al.setTitle("Accounts");
			al.setHeaderText("Buy Shares With An Account?");
			
			al.getButtonTypes().setAll(ButtonType.NO, ButtonType.YES);
			Optional<ButtonType> result = al.showAndWait();
			
			if(result.get() == ButtonType.YES){
				showAccounts();
			}
			else if(result.get() == ButtonType.NO){
				buyHolding();
			}
		}
		else{
			buyHolding();
		}	
	}
	
	private void showAccounts(){
		// Makes a list of Accounts which can be selected
		List<String> tempAccountsList = new ArrayList<String>();

		for(Account acc: mc.getPortfolio().getAccounts()){
			tempAccountsList.add(acc.getName());
		}

		mc.getAccountsObservableList().setAll(tempAccountsList);
		mc.getAccountsListView().setItems(mc.getAccountsObservableList());
		
		mc.getAccountsListView().setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent click) {
				// Get the name of the Account Selected
				String accountSelection = mc.getAccountsListView().getSelectionModel().getSelectedItem();
				// Find the Account based on its name
				accountChosen = mc.getPortfolio().getAccount(accountSelection);

				accountsDialog.close();
				buyHolding();
			}
		});
		
		// Set up and display the window with
		// the properties created above
		accountsDialog = new Stage();
		accountsDialog.initStyle(StageStyle.UTILITY);
		accountsDialog.setTitle("Choose An Account");
		Scene scene = new Scene(new Group(mc.getAccountsListView()),
				mc.getAccountsListView().getMinWidth(), 
				mc.getAccountsListView().getMinHeight());
		
		accountsDialog.setScene(scene);
		accountsDialog.show();
	}
	
	private void buyHolding(){
		Holding h = new Holding(equityChosen, sharesPurchased, equityChosen.getCurrentPrice());
		boolean r;
		
		// Attempt to add the Equity purchase to the Portfolio
		// based on whether an Account was selected
		if(accountChosen == null){
			r = mc.getPortfolio().addHolding(equityChosen, h);
		}
		else{
			r = mc.getPortfolio().addHolding(equityChosen, h, accountChosen);
		}
		
		if(r){
			// Update the Display List
			mc.performManageHoldings();
		}
		else{
			Alert cantBuyAlert = new Alert(AlertType.INFORMATION);
			cantBuyAlert.setTitle("Information Dialog");
			cantBuyAlert.setHeaderText(null);
			cantBuyAlert.setContentText("You have insufficient funds to buy that many shares.");
			cantBuyAlert.showAndWait();
		}
	}
} 
