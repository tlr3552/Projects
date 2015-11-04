package controllers;

import java.text.NumberFormat;
import java.util.NoSuchElementException;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;

import models.Account;

/**
 * 
 * @author Bill Dybas
 *
 */
public class DepositHandler implements EventHandler<ActionEvent> {
	private MediatorController mc;
	private Account account;
	private double amountDeposited;
	
	public DepositHandler(MediatorController mc, Account a) {
		this.mc = mc;
		this.account = a;
	}
	
	@Override
	public void handle(ActionEvent event) {
		if(showDepositNumber()){
			deposit();
		}
	}
	
	private boolean showDepositNumber(){
		boolean finished = false;
		boolean correct = false;
		double amount = 0;
		
		// Show a Dialog to input how many shares to buy
		TextInputDialog t = new TextInputDialog();
		t.setContentText("How much would you like to Deposit?");
		
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
			alert.setContentText("Deposit " + df.format(amount) + "?");

			Optional<ButtonType> res = alert.showAndWait();
			if (res.get() == ButtonType.OK){
				amountDeposited = amount;
				finished = true;
			}
		}
		
		return finished;
	}
	
	private void deposit(){
		mc.getPortfolio().addMoney(account, amountDeposited);
	}
}
