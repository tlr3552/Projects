package controllers;

import java.text.NumberFormat;
import java.util.ArrayList;
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

/**
 * 
 * @author Bill Dybas
 *
 */
public class TransferHandler implements EventHandler<ActionEvent> {
	private MediatorController mc;
	private Account account, accountChosen;
	private Stage accountsDialog;
	private double amountTransferred;
	
	public TransferHandler(MediatorController mc, Account a) {
		this.mc = mc;
		this.account = a;
	}
	
	@Override
	public void handle(ActionEvent event) {
		showAccounts();
	}
	
	private void showAccounts(){
		ArrayList<Account> allExceptThis = new ArrayList<Account>();
		
		for(Account a : mc.getPortfolio().getAccounts()){
			if(!a.equals(account)){
				allExceptThis.add(a);
			}
		}
		
		// If they have another Account, show them this window,
		// otherwise, tell them they don't have another Account
		if(!allExceptThis.isEmpty()){
			// Makes a list of Accounts which can be selected
			List<String> tempAccountsList = new ArrayList<String>();

			for(Account acc: allExceptThis){
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
					if(showTransferNumber()){
						transfer();
					}
				}
			});
			
			// Set up and display the window with
			// the properties created above
			accountsDialog = new Stage();
			accountsDialog.initStyle(StageStyle.UTILITY);
			accountsDialog.setTitle("Choose Another Account");
			Scene scene = new Scene(new Group(mc.getAccountsListView()),
					mc.getAccountsListView().getMinWidth(), 
					mc.getAccountsListView().getMinHeight());
			
			accountsDialog.setScene(scene);
			accountsDialog.show();
		}
		else{
			Alert cantTransferAlert = new Alert(AlertType.INFORMATION);
			cantTransferAlert.setTitle("Information Dialog");
			cantTransferAlert.setHeaderText(null);
			cantTransferAlert.setContentText("You don't have any other Accounts.");
			cantTransferAlert.showAndWait();
		}
	}
	
	private boolean showTransferNumber(){
		boolean finished = false;
		boolean correct = false;
		double amount = 0;
		
		// Show a Dialog to input how many shares to buy
		TextInputDialog t = new TextInputDialog();
		t.setContentText("How much would you like to Transfer?");
		
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
			final NumberFormat df = NumberFormat.getCurrencyInstance();
			amount = Math.floor(amount * 100) / 100;
			
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setContentText("Transfer " + df.format(amount) + "?");

			Optional<ButtonType> res = alert.showAndWait();
			if (res.get() == ButtonType.OK){
				amountTransferred = amount;
				finished = true;
			}
		}
		
		return finished;
	}
	
	private void transfer(){
		if(mc.getPortfolio().transferMoney(account, accountChosen, amountTransferred)){
			// Update the Accounts List
			mc.performManageAccounts();
		}
		else{
			Alert cantTransferAlert = new Alert(AlertType.INFORMATION);
			cantTransferAlert.setTitle("Information Dialog");
			cantTransferAlert.setHeaderText(null);
			cantTransferAlert.setContentText("You don't have that much money to transfer.");
			cantTransferAlert.showAndWait();
		}
	}
	
}
