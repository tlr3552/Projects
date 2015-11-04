package controllers;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import models.Account;
import models.AccountTransaction;
import models.BankAccount;
import models.BuyHoldingTransaction;
import models.Equity;
import models.Holding;
import models.MoneyMarketAccount;
import models.NullAccount;
import models.Portfolio;
import models.SellHoldingTransaction;
import models.Transaction;
import models.TransferAccountTransaction;
import persistence.EquitiesSingleton;
import persistence.PortfolioWriter;
import persistence.UpdateFrequency;
import simulation.CombinedStrategy;
import simulation.MarketStrategy;
import simulation.SimulationDriver;
import simulation.TimeInterval;

import views.AppComponent;
import views.MainApp;
import views.SimTableData;

/**
 * Mediating controller for the main_frame.fxml view.
 *
 * @author Drew Heintz
 * @author Tyler Russell
 * @author Fawaz Alhenaki
 * @author Bill Dybas
 *
 */
public class MediatorController implements AppComponent {

	//Constants that size the dynamically added buttons
	private final Double BUTTON_WIDTH = 78.0;
	private final Double BUTTON_HEIGHT = 16.0;

    // Buttons visible all the time
    @FXML private Button btnSave;
    @FXML private Button btnLogout;

    // view components for the home tab
    @FXML private Label holdingsBalance;
    @FXML private Label accountsBalance;
    @FXML private Label totalBalance;
    @FXML private Label dowJones;
    @FXML private Button updateButton;
    @FXML private ChoiceBox<UpdateFrequency> updateFrequency;
    private UpdateFrequency frequencySelected;

    // view components for the simulation tab
    @FXML private TableColumn<SimTableData, String> colEquity;
    @FXML private TableColumn<SimTableData, Double> colCurrent;
    @FXML private TableColumn<SimTableData, Double> colSimulated;
    @FXML private ChoiceBox<MarketStrategy> comboMarketType;
    @FXML private TextField txtPercentAnnum;
    @FXML private ChoiceBox<TimeInterval> comboTimeInterval;
    @FXML private TextField txtSteps;
    @FXML private TableView<SimTableData> simulationView;
    private SimulationDriver simulation;
    private ObservableList<SimTableData> simulationData;

    //view components for the equities listing tab
    @FXML private ListView<String> equitiesListView;
    @FXML private Label equitiesDisplay;
    @FXML private ObservableList<String> equitiesObservableList;

    //view components for the accounts tab
    @FXML private ListView<String> accountsListView;
    @FXML private ObservableList<String> accountsObservableList;
    @FXML private Label accountsDisplay;
    private String accountNameForAdd;
	private String accountTypeForAdd;

    //view components for the transactions tab
    @FXML private ListView<String> transactionsListView;
    @FXML private ObservableList<String> transactionsObservableList;

    //Wildcard list used to populate the currentListView
    private List<String> displayList = new ArrayList<String>();

    //The list containing elements being displayed in the left side of screen.
    @FXML private ListView<String> currentListView;
    @FXML private ListView<String> allEquitiesListView;

    private ObservableList<Button> optionsObservableList;
    @FXML private ToolBar optionsBar;
    @FXML private ToolBar holdingOptionsBar;
    @FXML private ToolBar accountOptionsBar;

    /* Transaction FXML components */
    @FXML private DatePicker fromPicker;
    @FXML private DatePicker toPicker;
    @FXML private Button genButton;
    @FXML private Label fromPickerLabel;
    @FXML private Label toPickerLabel;
    @FXML private ScrollPane statementPane;
    @FXML private Label statement;
    private LocalDate fromDate;
	private LocalDate toDate;


    /** Our reference to the main application */
    private MainApp application;

    @Override
    public void setApp(MainApp application) {
        this.application = application;
    }

    @Override
    public void setup() {

    	//Make the stage centered on any screen
    	final Double SCREEN_WIDTH = Screen.getPrimary().getVisualBounds().getWidth();
    	final Double SCREEN_HEIGHT = Screen.getPrimary().getVisualBounds().getHeight();
    	final Double STAGE_WIDTH = application.getStage().getWidth();
    	final Double STAGE_HEIGHT = application.getStage().getHeight();
    	final Double STAGE_X = (SCREEN_WIDTH / 2) - (STAGE_WIDTH / 2);
    	final Double STAGE_Y = (SCREEN_HEIGHT / 2) - (STAGE_HEIGHT / 2);
    	application.getStage().setX(STAGE_X);
    	application.getStage().setY(STAGE_Y);

    	// Initialize update frequency on Home Tab
        initializeEnumChoiceBox(updateFrequency, UpdateFrequency.values());

    	// Set the initial data for the Home Tab
    	buildHomeTab();
    	frequencySelected = UpdateFrequency.Five;
    	updateFrequency.setValue(frequencySelected);

        // Initialize simulations
        initializeEnumChoiceBox(comboMarketType, MarketStrategy.values());
        initializeEnumChoiceBox(comboTimeInterval, TimeInterval.values());

        optionsObservableList = FXCollections.observableArrayList();
        allEquitiesListView = new ListView<String>();
        allEquitiesListView.setMinWidth(500);
        allEquitiesListView.setMinHeight(500);

        simulationData = FXCollections.observableArrayList();
        simulationView.setItems(simulationData);
        colEquity.setCellValueFactory(new PropertyValueFactory<>("equity"));
        colCurrent.setCellValueFactory(new PropertyValueFactory<>("current"));
        colSimulated.setCellValueFactory(new PropertyValueFactory<>("simulated"));

        //Get equities from models
        equitiesListView = new ListView<String>();
        equitiesListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        equitiesObservableList = FXCollections.observableArrayList();

        //Get accounts from models
        accountsListView = new ListView<String>();
        accountsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        accountsObservableList = FXCollections.observableArrayList();
        accountTypeForAdd = "";
        accountNameForAdd = "";

        //Get transactions from models
        transactionsListView = new ListView<String>();
        transactionsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        transactionsObservableList = FXCollections.observableArrayList();
        statementPane.setHmax(Double.MAX_VALUE);
        fromDate = null;
    	toDate = null;
    }

    //////////////////////////
    ///  Helper Functions  ///
    //////////////////////////

    private <E extends Enum<?>> void initializeEnumChoiceBox(ChoiceBox<E> box, E[] values) {
        box.setItems(FXCollections.observableArrayList(values));
        box.setValue(values[0]);
    }

    /**
     * Clears the List on the Left Side of the Application
     */
    public void clearList() {
    	displayList.clear();

    	// These might be null, so check them
    	// to prevent errors
    	if(optionsBar != null){
        	optionsBar.getItems().clear();
    	}
    	if(holdingOptionsBar != null){
    		holdingOptionsBar.getItems().clear();
    	}
    	if(accountOptionsBar != null){
    		accountOptionsBar.getItems().clear();
    	}
    	if(equitiesObservableList != null){
    		equitiesObservableList.clear();
    	}
    	if(transactionsObservableList != null){
    		transactionsObservableList.clear();
    	}
    	if(accountsObservableList != null){
        	accountsObservableList.clear();
    	}
    }

    /**
     * @return	the current User's Portfolio
     */
    protected Portfolio getPortfolio() {
    	return application.getUser().getPortfolio();
    }

    /**
     * Saves any pending Transactions a User might have
     */
    public void performSave(ActionEvent event) {
    	getPortfolio().saveTransactions();
    	PortfolioWriter pw = new PortfolioWriter();
    	pw.write(application.getUser());

    	Alert alert = new Alert(AlertType.INFORMATION);
    	alert.setTitle("Information Dialog");
    	alert.setHeaderText(null);
    	alert.setContentText("Save Complete!");
    	alert.showAndWait();
    }

    /**
     * Logs out a User
     */
    public void performLogout(ActionEvent event) throws Exception {
        application.logout();
    }

    /**
     * Undo's the Last Action a User Performed
     */
    public void undo(ActionEvent event) throws Exception {
        getPortfolio().undo();
    }

    /**
     * Redo's the Last Action a User Undid
     */
    public void redo(ActionEvent event) throws Exception {
    	getPortfolio().redo();
    }


    //////////////////////
    ///    Home Tab    ///
    //////////////////////


    /*
     * Handle displaying stuff on the home tab
     * Initially clears the displayList since its not used in this tab
     */
    public void performHome() {
    	clearList();
    	try{
        	buildHomeTab();
        	// Make sure that the frequency
        	// selected is the one that is shown
        	updateFrequency.setValue(frequencySelected);
    	} catch(NullPointerException e){
    		//For some reason, when logging in, a NullPointerException
    		//is thrown even though we can access a User and their Portfolio
    		//I think it has something to do with the 'On Selection Change' Property

    		//Do Nothing, as the tab is rendered fine
    	}
    }

    private void buildHomeTab(){
    	Double totalHoldings = 0.0;
    	for(Holding h : getPortfolio().getHoldings().values()){
    		totalHoldings += (h.getShares() * h.getEquity().getCurrentPrice());
    	}
    	Double totalAccounts = 0.0;
    	for(Account a : getPortfolio().getAccounts()){
    		totalAccounts += a.getCurrentBalance();
    	}
    	Double total = totalHoldings + totalAccounts;
    	total = Math.floor(total * 100) / 100;

    	final NumberFormat f = NumberFormat.getCurrencyInstance();

    	holdingsBalance.setText(f.format(totalHoldings));
    	accountsBalance.setText(f.format(totalAccounts));
    	totalBalance.setText(f.format(total));

    	Double dj = EquitiesSingleton.getInstance().getDowJones();
    	dj = Math.floor(dj * 100) / 100;
    	dowJones.setText(dj.toString());

    	updateButton.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				frequencySelected = updateFrequency.getValue();
				application.setUpdateInterval(frequencySelected.getSeconds());
			}
    	});
    }


    //////////////////////
    ///   Search Tab   ///
    //////////////////////


    /*
     * Handle displaying stuff on the search tab
     * Initially clears the displayList since its not used in this tab
     */
    public void performSearch() {
    	//TODO
    	clearList();
    }


    //////////////////////
    ///  Holdings Tab  ///
    //////////////////////


    /*
     * Helper method to parse through our standard String display format
     * to get the ticker symbol.
     * SAMPLE
     * --------------------------
     * Ticker Symbol: SNDK
     * Equity Name: SanDisk Corporation
	 * Price Per Share: 55.33
	 * Indicies: NASDAQ100
	 * ---------------------------
	 *
	 * returns SNDK
     *
     * @param String str
     * @return String s
     */
    public String parseEquity(String str) {
    	String s = "";
		//Removes everything before the ticker symbol
		String before = str.substring(str.indexOf("Ticker Symbol")+15);
		//Removes everything after the ticker symbol
		String after = str.substring(str.indexOf("Equity Name: "));
		s = str.substring(str.indexOf(before), str.indexOf(after));
    	return s.replace("\n", "");
    }

    /*
     * Search the users equities given a String ticker symbol and return the
     * corresponding equity.
     *
     * @param String str - the ticker symbol
     *
     * @return Equity - the Equity with that ticker symbol or null if none exists
     */
    private Equity getEquityByTickerSymbol(String str) {
		HashMap<String, Equity> equities = EquitiesSingleton.getInstance().getEquities();
		return equities.get(str);
    }

    /*
     * Shows the Holdings a User Currently Has
     */
    public void performManageHoldings() {
    	clearList();
    	equitiesDisplay.setText("Choose a Holding to Display Its Information.");

    	// Add the option to acquire a new holding in here,
    	// separating it from the option to increase holding currently owned

    	// Make a button that handles buying a new Holding
    	Button btnCreate = new Button("Buy New Holding");
    	// Add it to the Options Bar at the top
    	final ArrayList<Button> optionsList = new ArrayList<Button>();
    	optionsList.add(btnCreate);
    	// Give it an Event Handler
    	EventHandler<ActionEvent> buyNewHandler = new BuyHoldingHandler(this);
    	btnCreate.setOnAction(buyNewHandler);

    	optionsBar.getItems().addAll(optionsList);

    	// Gets a User's Holdings' Ticker Symbols to Display on the Left Side
    	for(Holding h : getPortfolio().getHoldings().values())
    		displayList.add(h.getEquity().getTickerSymbol());

    	// Sorts the List in Alphabetical Order
    	Collections.sort(displayList);
    	// set the list
    	equitiesObservableList.setAll(displayList);
    	// render the holdings list
    	currentListView.setItems(equitiesObservableList);
    	// give each holding a listener
    	handleHoldingSelected(currentListView);
    }

    /*
     * Handle when the user selects a holding to manage
     * @param ListView<String> listview
     */
    private void handleHoldingSelected(ListView<String> listview) {
    	listview.setOnMouseClicked(new EventHandler<MouseEvent> () {
    		@Override
    		public void handle(MouseEvent event) {
    			// Get the String of the Selected Ticker Symbol
				final String selected = listview.getSelectionModel().getSelectedItem();

				// If the selection is not an empty element
				if(selected != null){
					// Finds the specified Equity and Holding
					final Equity eq = getEquityByTickerSymbol(selected);
					final Holding h = getPortfolio().getHoldings().get(eq);

					// Get the information about the Holding
					final String sharesOwned = String.valueOf(h.getShares());
					final String pricePerShare = String.valueOf(h.getPricePerShare());
					final String date = String.valueOf(h.getDate());

					// Get the information about the Equity
					final String tickerSymbol = eq.getTickerSymbol();
					final String name = eq.getEquityName();
					final String currentPrice = String.valueOf(eq.getCurrentPrice());

					// Set the display using the strings from above
					equitiesDisplay.setText(
						"Ticker Symbol: " + tickerSymbol + "\n" +
						"Name: " + name + "\n" +
						"Current Price: " + currentPrice + "\n" +
						"\n" +
						"Number of Shares Owned: " + sharesOwned + "\n" +
						"Average Price Per Share: " + pricePerShare + "\n" +
						"Last Purchase Date: " + date
					);

					// Set the holding options toolbar on the bottom
					setHoldingOptionsToolBar(tickerSymbol);
				}
    		}
    	});
    }

    /*
     * Creates the appropriate buttons for Holding actions.
     * Calls the different Holding-related Handler classes
     */
    private void setHoldingOptionsToolBar(String tickerSymbol) {
    	Button btnBuy = new Button("Buy More");
    	Button btnSell = new Button("Sell");

    	//Make button the same size as Logout and Save
    	btnBuy.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
    	btnSell.setPrefSize(BUTTON_WIDTH,BUTTON_HEIGHT);

    	// Make them Observable and give them Event Handlers
    	final ArrayList<Button> optionsList = new ArrayList<Button>();
    	optionsList.add(btnBuy);
    	optionsList.add(btnSell);
    	optionsObservableList = FXCollections.observableArrayList(optionsList);
    	holdingOptionsBar.getItems().clear();
    	holdingOptionsBar.getItems().addAll(optionsObservableList);

    	EventHandler<ActionEvent> buyHandler = new BuyHoldingHandler(this, tickerSymbol);
    	btnBuy.setOnAction(buyHandler);
    	EventHandler<ActionEvent> sellHandler = new SellHoldingHandler(this, tickerSymbol);
    	btnSell.setOnAction(sellHandler);
    }


    //////////////////////
    ///  Accounts Tab  ///
    //////////////////////


    /*
     * Root method that is called when the user selects the Accounts tab
     */
    public void performManageAccounts() {
    	clearList();

    	// Make a button that handles adding new Accounts
    	Button btnCreate = new Button("Add New Account");
    	final ArrayList<Button> optionsList = new ArrayList<Button>();
    	optionsList.add(btnCreate);
    	EventHandler<ActionEvent> creationHandler = new AddAccountHandler(this);
    	btnCreate.setOnAction(creationHandler);

    	optionsBar.getItems().addAll(optionsList);

    	ArrayList<Account> accs = getPortfolio().getAccounts();
    	// Tell the User to make an Account if they have none created
    	if(accs.isEmpty()){
    		accountsDisplay.setText("Add an Account by Clicking the 'New Account' Button.");
    	}
    	else{
    		// Add the Accounts to the list on the left
        	accountsDisplay.setText("Choose an Account to display its information here.");
    		for(Account acc: accs){
    			displayList.add(acc.toString());
    		}

    		accountsObservableList.setAll(displayList);
    		currentListView.setItems(accountsObservableList);
    		handleAccountSelected(currentListView);
    	}
    }

    /*
     * This method is called when the user chosen an account in the listview on the left
     * @param ListView<String> listview
     */
    private void handleAccountSelected(ListView<String> listview) {
    	listview.setOnMouseClicked(new EventHandler<MouseEvent> () {
    		@Override
			public void handle(MouseEvent event) {
    			// Get the String of the Selected Account
				final String selected = currentListView.getSelectionModel().getSelectedItem();

				// If the selection is not an empty element
				if(selected != null) {
					// Number formatter so we can display the balance as currency
					final NumberFormat formatter = NumberFormat.getCurrencyInstance();

					// Gets the Account Selected
					final Account temp = getPortfolio().getAccount(selected);

					// Prepares the Type of Account
					String accType = "";
					if(temp instanceof BankAccount)
						accType = "Bank Account";
					else if(temp instanceof MoneyMarketAccount)
						accType = "Money Market";

					final String name = temp.getName();
					final Double balance = temp.getCurrentBalance();
					final LocalDate date = temp.getDateCreated();

					// Displays the Information about the Account
					accountsDisplay.setText(
							"Account Name: " + name + "\n" +
							"Type: " + accType + "\n" +
						    "Balance: " + formatter.format(balance) + "\n" +
						    "Date Created: " + date + "\n"
					);

					setAccountOptionsToolBar(temp);
				}
    		}
    	});
    }

    /*
     * Sets the components and listeners for each option the user has to manage an account
     * @param Account a
     */
    private void setAccountOptionsToolBar(Account a) {
    	Button btnDeposit = new Button("Deposit");
    	Button btnWithdraw = new Button("Withdraw");
    	Button btnTransfer = new Button("Transfer");

    	//Make button the same size as Logout and Save
    	btnDeposit.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
    	btnWithdraw.setPrefSize(BUTTON_WIDTH,BUTTON_HEIGHT);
    	btnTransfer.setPrefSize(BUTTON_WIDTH,BUTTON_HEIGHT);

    	// Make them Observable and give them Event Handlers
    	final ArrayList<Button> optionsList = new ArrayList<Button>();
    	optionsList.add(btnDeposit);
    	optionsList.add(btnWithdraw);
    	optionsList.add(btnTransfer);
    	optionsObservableList = FXCollections.observableArrayList(optionsList);
    	accountOptionsBar.getItems().clear();
    	accountOptionsBar.getItems().addAll(optionsObservableList);

    	EventHandler<ActionEvent> depositHandler = new DepositHandler(this, a);
    	btnDeposit.setOnAction(depositHandler);
    	EventHandler<ActionEvent> withdrawHandler = new WithdrawHandler(this, a);
    	btnWithdraw.setOnAction(withdrawHandler);
    	EventHandler<ActionEvent> transferHandler = new TransferHandler(this, a);
    	btnTransfer.setOnAction(transferHandler);
	}


    ////////////////////////
    ///  Simulation Tab  ///
    ////////////////////////

    /**
     * Clears the list on the left when this tab is selected
     */
    public void performManageSimulation(){
    	clearList();
		currentListView.setItems(null);
    }

    /**
     * Runs the Simulation One Step
     */
    public void stepSimulation(ActionEvent event) {
        if(simulation == null) {
            createSimulation();
        }
        if(!simulation.step()) {
            simulation = null;
        }
        displaySimulation();
    }

    /**
     * Runs the Simulation to the End
     */
    public void runSimulation(ActionEvent event) {
        if(simulation == null) {
            createSimulation();
        }
        simulation.runToEnd();
        simulation = null;
        displaySimulation();
    }

    /**
     * Resets the Simulation
     */
    public void resetSimulation(ActionEvent event) {
        simulation = null;
        getPortfolio().resetSimulation();
        displaySimulation();
    }

    /**
     * Builds the Simulation
     */
    private void createSimulation() {
        try {
            double annumPercent = Double.parseDouble(txtPercentAnnum.getText());
            int numSteps = Integer.parseInt(txtSteps.getText());
            annumPercent = comboMarketType.getValue().calculatePercent(annumPercent);
            Portfolio port = getPortfolio();
            simulation = new SimulationDriver(new CombinedStrategy(annumPercent),
                    port.getSimulatedHoldings(), comboTimeInterval.getValue(),
                    numSteps);
        } catch (IllegalArgumentException e) {
            // They didn't input a number so just do nothing for now
            System.err.println(e.toString());
        }
    }

    /**
     * Display the updated Simulation
     */
    private void displaySimulation() {
        simulationData.clear();
        for(Equity eq : getPortfolio().getHoldings().keySet()) {
            String ticker = eq.getTickerSymbol();
            double current = getPortfolio().getHoldings().get(eq).getShares()
                    * eq.getCurrentPrice();
            simulationData.add(new SimTableData(ticker, current, 0.0));
        }
        int index = 0;
        for(Equity eq : getPortfolio().getSimulatedHoldings().keySet()) {
            double simulated = getPortfolio().getHoldings().get(eq).getShares()
                    * eq.getCurrentPrice();
            simulationData.get(index).setSimulated(simulated);
            index++;
        }
    }


    //////////////////////////
    ///  Transactions Tab  ///
    //////////////////////////

    /*
     * Given a string of the form: "class models.SomeModel"
     * returns "Some Model"
     * @param String str
     * @return String s;
     */
    public String parseTransactionType(String str) {
    	String s = "";
    	//Removes everything before the ticker symbol
    	String before = str.substring(str.indexOf("models.")+7);
    	String after = str.substring(str.indexOf("Transaction"));
    	s = str.substring(str.indexOf(before), str.indexOf(after));

    	switch(s) {
    		case "BuyHolding":
    			return "Bought Holding";
    		case "SellHolding":
	    		return "Holding Sold";
			case "DepositAccount":
	    		return "Account Deposit";
			case "WithdrawAccount":
	    		return "Account Withdrawal";
			case "TransferAccount":
	    		return "Account Transfer";
			default:
	    		return "N/A";
    	}
    }


    public void performViewTransactions() {
    	clearList();
    }

    /*
     * Actually sets the content to be displayed in the scrollable pane,
     * using helper methods below to get what it needs
     */
    public void generateStatement() {

    	clearList();
    	statement = new Label("");
    	String output = "\nTransaction Statement:\n";
    	Map<Transaction, LocalDate> transactions = getPortfolio().getTransactions();
    	
    	for(Transaction t: transactions.keySet()) {
    		String type = parseTransactionType(t.getClass().toString());
    		
    		//if this transaction date is either before, after or the same date as the requested date ranges
    		if(withinRange(transactions.get(t))){    			
    			output += "\nDescription: " + type + "\n" + 
    					  "Date: " + transactions.get(t).toString() + "\n";
    			
    			switch(type){
					case("Bought Holding"):
						BuyHoldingTransaction bt = (BuyHoldingTransaction) t;
						output += "Equity: " + bt.getHolding().getEquity() + "\n";
						output += "Shares: " + bt.getHolding().getShares() + "\n";
						output += "Price Per Share: " + bt.getHolding().getPricePerShare() + "\n";
						break;
					case("Holding Sold"):
						SellHoldingTransaction st = (SellHoldingTransaction) t;
						output += "Equity: " + st.getHolding().getEquity() + "\n";
						output += "Shares: " + st.getHolding().getShares() + "\n";
						output += "Price Per Share: " + st.getHolding().getPricePerShare() + "\n";
						break;
    			}
    			
    			output += "Account Used: " + t.getAccount().getName() + "\n" +
    					  "Account Type: " + t.getAccount().getType() + "\n";
    			
    			switch(type){
	    			case("Account Deposit"):
	    			case("Account Withdrawal"):
	    			case("Account Transfer"):
	    				AccountTransaction at = (AccountTransaction) t;
	    				output += "Amount: " + at.getAmount() + "\n";
	    				break;
    			}
    			
    			switch(type){
	    			case("Account Transfer"):
	    				TransferAccountTransaction tat = (TransferAccountTransaction) t;
	    				output += "Other Account: " + tat.getOtherAccount().getName() + "\n";
	    				break;
    			}
    				
    			output += "---------------------------------------";
    		}
    	}
    	if(output.equals("\nTransaction Statement:\n")) {
    		statement.setText("No results found");
    		statementPane.setContent(statement);
    	}
    	else {
    		statement.setText(output);
    		statementPane.setContent(statement);
    	}
    }

    /*
     * Given a LocalDate it checks if this day is within the user requested date range
     * @param LocalDate date
     * @return boolean
     */
    public boolean withinRange(LocalDate date) {
    	//Checks within range, ends included
    	if(date.isAfter(getFromDate()) && date.isBefore(getToDate())
    			|| date.isEqual(getFromDate()) || date.isEqual(getToDate()))
    		return true;
    	return false;
    }

    /*
     * "Entry" method for the FXML file, is called when the user pushes the Generate button
     */
    public void performGenerate() {
		if(validateEnteredDates()) {
			generateStatement();
		}
	}

    /*
     * Validation method for the user entering funny stuff for their dates...
     * @return boolean b
     */
    public boolean validateEnteredDates() {
    	//reset the warning labels
    	fromPickerLabel.setText("");
    	toPickerLabel.setText("");
    	boolean b = true;
    	try {
	    	if(validateEmptyFromDate()) {
	    		fromPickerLabel.setText("No date chosen");
	    		fromPickerLabel.setTextFill(Color.RED);
	    		b = false;
	    	}
	    	if(validateEmptyToDate()) {
	    		toPickerLabel.setText("No date chosen");
	    		toPickerLabel.setTextFill(Color.RED);
	    		b = false;
	    	}
	    	if(getFromDate().isAfter(LocalDate.now())) {
	    		fromPickerLabel.setText("Can't choose a future date");//TODO need to not let this even be a choice...
	    		fromPickerLabel.setTextFill(Color.RED);
	    		b = false;
	    	}
	    	if(getToDate().isAfter(LocalDate.now())) {
	    		toPickerLabel.setText("Can't choose a future date");//TODO need to not let this even be a choice...
	    		toPickerLabel.setTextFill(Color.RED);
	    		b = false;
	    	}
	    	//  TODO: do we want to let the from date to be after the To date? Its a valid range, yet doesnt make sense.
	    	if(getToDate().isBefore(getFromDate())) {
	    		fromPickerLabel.setText("From cant be after To");
	    		fromPickerLabel.setTextFill(Color.RED);
	    		toPickerLabel.setText("To can't be before From");
	    		toPickerLabel.setTextFill(Color.RED);
	    		b = false;
	    	}
    	} catch(NullPointerException npe) {}//already handled, its null when they dont enter a date.
    	return b;
    }

    /*
     * Validation methods, error checking the dates chosen by the user
     * @return boolean
     */
    private boolean validateEmptyFromDate() {
    	return getFromDate() == null;
    }
    private boolean validateEmptyToDate() {
    	return getToDate() == null;
    }
    public void performFromPicker() {
		setFromDate(fromPicker.getValue());
    }
    public void performToPicker() {
    	setToDate(toPicker.getValue());
    }

    /////////////////////////////////
    ///     Getters/Setters  	  ///
    /////////////////////////////////

    public void setFromLabel(String str) {
    	fromPickerLabel.setText(str);
    	fromPickerLabel.setFont(Font.font("Arial", 11));
    	fromPickerLabel.setTextFill(Color.RED);
    }

    public void setToLabel(String str) {
    	toPickerLabel.setText(str);
    	toPickerLabel.setFont(Font.font("Arial", 11));
    	toPickerLabel.setTextFill(Color.RED);
    }

    public void setFromDate(LocalDate date) {
    	fromDate = date;
    }

    public void setToDate(LocalDate date) {
    	toDate = date;
    }

    public LocalDate getFromDate() {
    	return fromDate;
    }

    public LocalDate getToDate() {
    	return toDate;
    }

    public void setFromPickerLabel(String str) {
    	fromPickerLabel.setText(str);
    }

    public void setToPickerLabel(String str) {
    	toPickerLabel.setText(str);
    }

    public DatePicker getFromDatePicker() {
    	return fromPicker;
    }

    public DatePicker getToDatePicker() {
    	return toPicker;
    }

    public ListView<String> getAllHoldingsListView() {
    	return allEquitiesListView;
    }

    public ListView<String> getAccountsListView() {
    	return accountsListView;
    }

    public ObservableList<String> getAccountsObservableList() {
    	return accountsObservableList;
    }

    public String getAccountNameForAdd() {
    	return accountNameForAdd;
    }

    public String getAccountTypeForAdd() {
    	return accountTypeForAdd;
    }

    public void setAccountNameForAdd(String str) {
    	accountNameForAdd = str;
    }

    public void setAccountTypeForAdd(String str) {
    	accountTypeForAdd = str;
    }
}
