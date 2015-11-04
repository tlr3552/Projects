package controllers;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import models.Account;
import models.BankAccount;
import models.MoneyMarketAccount;

/*
 * Implements EventHandler, and is created when the user chooses to create a new Account.
 * @author Tyler Russell
 */

public class AddAccountHandler implements EventHandler<ActionEvent> {

	private MediatorController mc;

	public AddAccountHandler(MediatorController mc) {
		this.mc = mc;
	}

	@Override
	public void handle(ActionEvent event) {
		//Get the accounts name from the user
		TextInputDialog nameDialog = new TextInputDialog();
		nameDialog.setContentText("Enter the name of your account");
		Optional<String> result = nameDialog.showAndWait();
		if (result.isPresent()) {
		    mc.setAccountNameForAdd(result.get());
		    nameDialog.close();
		}
		else {
			nameDialog.close();
			return;
		}

		//Now prompt the user for the account type
		Stage dialog = new Stage();
		dialog.initStyle(StageStyle.UTILITY);
		BorderPane addAccountRootPane = new BorderPane();
		GridPane buttonPane = new GridPane();
		Button btnMM = new Button("Money Market");
		Button btnBank = new Button("Bank Account");
		Label directions = new Label("Select the account type");
		buttonPane.add(btnBank, 0,0);
	    buttonPane.add(btnMM,2,0);
	    BorderPane.setAlignment(directions, Pos.CENTER);
		addAccountRootPane.setCenter(buttonPane);
		addAccountRootPane.setTop(directions);
		Scene sceneAdd = new Scene(addAccountRootPane, 210,65);
		dialog.setScene(sceneAdd);
		dialog.show();

		btnBank.setOnAction(new EventHandler<ActionEvent> () {
			@Override
			public void handle(ActionEvent e) {
				mc.setAccountTypeForAdd("Bank Account");
				dialog.close();
				getAccountStartingBalanceChosen();
			}
		});

		btnMM.setOnAction(new EventHandler<ActionEvent> () {
			@Override
			public void handle(ActionEvent e) {
				mc.setAccountTypeForAdd("Money Market");
				dialog.close();
				getAccountStartingBalanceChosen();
			}
		});
	}

	public Double getAccountStartingBalanceChosen() {

		Double choice = null;
    	boolean correct = false;
    	final NumberFormat formatter = NumberFormat.getCurrencyInstance();
    	final DecimalFormat df = new DecimalFormat("#.00");
    	TextInputDialog balanceDialog = new TextInputDialog();
		balanceDialog.setContentText("Choose a starting balance");
		Optional<String> result = balanceDialog.showAndWait();
		while(!correct){
			try{
				if(result.isPresent()){
					choice = Double.parseDouble(result.get());
					correct = true;
				}
				else
					return Double.parseDouble(df.format(0.0));
			} catch(NumberFormatException e){
				Alert alert = new Alert(AlertType.INFORMATION);
		    	alert.setTitle("Information Dialog");
		    	alert.setHeaderText(null);
		    	alert.setContentText("You must enter a numeric value.");
		    	alert.showAndWait();
		    	result = balanceDialog.showAndWait();
			}
		}
		balanceDialog.close();
		Alert alert = new Alert(AlertType.CONFIRMATION);
		ButtonType buttonTypeOk = new ButtonType("Ok");
		ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		alert.getButtonTypes().setAll(buttonTypeOk, buttonTypeCancel);
		alert.setContentText(
				"Account name: " + mc.getAccountNameForAdd() + "\n" +
				"Account type: " + mc.getAccountTypeForAdd() + "\n" +
				"Initial Balance: " + formatter.format(choice) + "\n" +
				"Date created: " + LocalDate.now() + "\n\n" +
				"Confirm account creation?" + "\n"
		);
		Optional<ButtonType> res = alert.showAndWait();

		if(res.get() == buttonTypeOk) {
			Account newAcc;
			if(mc.getAccountTypeForAdd().equals("Bank Account"))
				newAcc = new BankAccount(mc.getAccountNameForAdd(), choice, LocalDate.now());
			else
				newAcc = new MoneyMarketAccount(mc.getAccountNameForAdd(), choice, LocalDate.now());
			if(mc.getPortfolio().addAccount(newAcc)) {
				mc.performManageAccounts();
				return new Double(0.0);
			}
			else {
				Alert alertFailed = new Alert(AlertType.CONFIRMATION);
				ButtonType buttonTypeFailedOk = new ButtonType("Ok");
				ButtonType buttonTypeFailedCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
				alertFailed.getButtonTypes().setAll(buttonTypeFailedOk, buttonTypeFailedCancel);
				alertFailed.setContentText("Encountered an error when creating your account.");
				Optional<ButtonType> results = alertFailed.showAndWait();
				if(results.isPresent())
					return new Double(0.0);//just return no matter what
			}


		}
		else if(res.get() == buttonTypeCancel){
			return new Double(0.0);
		}
    	return choice;
    }
}
