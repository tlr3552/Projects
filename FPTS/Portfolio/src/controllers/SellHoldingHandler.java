package controllers;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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
 * 
 * @author Bill Dybas
 * @author Tyler Russell
 *
 */
public class SellHoldingHandler implements EventHandler<ActionEvent> {

	private MediatorController mc;
	private Stage accountsDialog;
	
	private Equity equityChosen;
	private double sharesSold;
	private Account accountChosen = null;
	
	// Get the Equities in the System
	private HashMap<String, Equity> equities = EquitiesSingleton.getInstance().getEquities();
	
	public SellHoldingHandler(MediatorController mc, String tickerSymbol) {
		this.mc = mc;
		equityChosen = equities.get(tickerSymbol);
	}
	
	@Override
	public void handle(ActionEvent event) {
		if(showSellNumber()){
			showAccountChoice();
		}
	}

	private boolean showSellNumber(){
		boolean finished = false;
		boolean correct = false;
		double amount = 0;
		
		// Show a Dialog to input how many shares to buy
		TextInputDialog t = new TextInputDialog();
		t.setContentText("Sell how many shares?");
		
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
			alert.setContentText("Sell " + df.format(amount) + " shares?" );

			Optional<ButtonType> res = alert.showAndWait();
			if (res.get() == ButtonType.OK){
				sharesSold = amount;
				finished = true;
			}
		}
		
		return finished;
	}
	
	private void showAccountChoice(){
		// If they have an Account, show them this window,
		// otherwise, just sell the Holding
		if(!mc.getPortfolio().getAccounts().isEmpty()){
			Alert al = new Alert(AlertType.CONFIRMATION);
			al.setTitle("Accounts");
			al.setHeaderText("Deposit Earnings into an Account?");
			
			al.getButtonTypes().setAll(ButtonType.NO, ButtonType.YES);
			Optional<ButtonType> result = al.showAndWait();
			
			if(result.get() == ButtonType.YES){
				showAccounts();
			}
			else if(result.get() == ButtonType.NO){
				sellHolding();
			}
		}
		else{
			sellHolding();
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
				sellHolding();
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
	
	private void sellHolding(){
		Holding h = new Holding(equityChosen, sharesSold, equityChosen.getCurrentPrice());
		boolean r;
		
		// Attempt to add the Equity purchase to the Portfolio
		// based on whether an Account was selected
		if(accountChosen == null){
			r = mc.getPortfolio().removeHolding(equityChosen, h);
		}
		else{
			r = mc.getPortfolio().removeHolding(equityChosen, h, accountChosen);
		}
		
		if(r){
			// Update the Display List
			mc.performManageHoldings();
		}
		else{
			Alert cantBuyAlert = new Alert(AlertType.INFORMATION);
			cantBuyAlert.setTitle("Information Dialog");
			cantBuyAlert.setHeaderText(null);
			cantBuyAlert.setContentText("You don't have that many shares to sell.");
			cantBuyAlert.showAndWait();
		}
	}
	
}
