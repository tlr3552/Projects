import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import javafx.application.Application;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.concurrent.Task;
import java.lang.Exception;
import javafx.application.Platform;




/**
*
* Connect3.java
* 
* Driver class for the game of Stones. This class is deisigned as a test of the
* GameEvaler class. It is used to play the game of stones against the computer.
* This file is the driver for the program. It handles the initial input, and builds
* the board.
* 
* @author Tyler Russell, Arthur Lunn
*
*/
public class Connect3 extends Application 
{
    
    static private FileReader reader = null;
    static private BufferedReader br = null;
    static private boolean play;
    static private String[][] setup = null;
    static private C3Position position;
    static protected int column;
    static protected boolean buttonPressed = false;
    static private Button[][] btns = null;
    static private Button btn = null;


    
    /**
     * Main method for the Connect3 class. Establishes the play mode based on command line
     * arguments and then initiates the game.
     * 
     * @param args Array of strings
     */
    public static void main(String[] args) 
    {

        // The current position
        position = null;

        char[][] board;

        // An instance of the evaluator to run game-play
        GameEvaler robot = new GameEvaler();

        // The best choice given a specific position on the board
        C3Move bestChoice;

        //Display usage message if the required arguments are missing, and quit
        if(args.length == 0 || args.length > 2) {
            System.out.println("Usage: java Connect3 [play] -");
            System.exit(0);
        }
        
        //If theres one parameter of "-", need to read in specs from file
        if(args.length == 1) {
            
            String arg = args[0];
            
            //If the user chose setup from standard input
            if(arg.equals("-")) {
               
                
            }
            //Else if they chose setup from a file
            else if(fileFound(arg)) {
                setup = getSetupFromFile(arg);

                //These lines setup the games position initially
                board = string2DToChar2D(setup);
                position = new C3Position(board);

                // Send the "best move" to standard output
                position.getPositionStatus();

                // Evaluate the best move for this position.
                bestChoice = (C3Move)robot.evaluatePosition(position, 0, false)[0];

                System.out.println("The best move would be to select column " + bestChoice.getMove() + ".");
                System.exit(0);

                
                //Start game
                //Application.launch();
            }
            
            //Else display usage message
            else {
                System.err.println("Usage: java Connect3 [play] -");
                System.exit(0);
            }
        }
        //Else there are two arguments, user wants to play
        else if(args.length == 2) {
            
            final String arg1 = args[0];
            final String arg2 = args[1];
            
            //If the first argument isn't "play", it isn't valid
            if(!arg1.equals("play")) {
                System.err.println("Usage: java Connect3 [play] -");
                System.exit(0);
            }
            //Else if the first argument is play, and the second isn't valid
            else if(!fileFound(arg2) && !arg2.equals("-")) {
                System.err.println("Usage: java Connect3 [play] -");
                System.exit(0);
            }
            //Else everything is all set to start the game
            else {
            	play = true;
                //If they chose file config
                if(!arg2.equalsIgnoreCase("-")) {
                	
                    setup = getSetupFromFile(arg2);
                    
                    //*****************************
                    //These lines setup the games position initially
                    board = string2DToChar2D(setup);
                    position = new C3Position(board);
                
                    //Start game
                    Application.launch();
                }
                //Else they want ???
                else {
                    //TODO Awaiting Duncans directions
                }
            }
        }
    }
    

    /**
    * Helper method to convert a 2d String array to a 2d char array.
    *
    * @param s The String array to be converted.
    * @return The 2d String array converted into a 2d Char array.
    *
    */
    private static char[][] string2DToChar2D(String s[][])
    {
        char[][] returnArray = new char[s.length][s[0].length];
        for (int col =0; col < s.length; col++)
        {
            for (int row =0; row < s[col].length; row++)
            {
                returnArray[col][row] = s[col][row].charAt(0);
            }
        }
        return returnArray;
    }

    /**
     * Helper method to see if a given string is a valid file
     * to read in.
     *
     * @param String str The file that is being searched for.
     * @return true if the given string is a file and can be 
     *              successfully read in. False otherwise.
     */
    private static boolean fileFound(String str) {
        
        try {
            reader = new FileReader(new File(str));
        }
        catch(FileNotFoundException fnf) {
            return false;
        }
        return true;
    }
    
    /**
     * Helper method to keep main clean. Called if the user chose 
     * to get the initial position of the board from a separate file.
     * 
     * @param String filename
     * @return int[] specs Array containing initial position of board
     */
    private static String[][] getSetupFromFile(String filename) {
        
        String[][] initialsetup = null;
        String line = null;
        String[] splitline = null;
        int width = 0;
        int height = 0;
        
        try {
            reader = new FileReader(new File(filename));
            br = new BufferedReader(reader);
            
            //Get the first line, containing width and height
            line = br.readLine();
            splitline = line.split(" ");
            try {
                width = Integer.parseInt(splitline[0]);
                height = Integer.parseInt(splitline[1]);
                initialsetup = new String[height][width];
                initialsetup = new String[height][width];
            }
            catch(NumberFormatException nfe) {
                System.err.println("One of the dimensions chosen for the board is a non-integer.");
                System.exit(0);
            }
            int counter = 0;
            //Then, while EOF isnt reached
            while((line = br.readLine()) != null) {
                splitline = line.split(" ");
                initialsetup[counter] = splitline;
                ++counter;
            }
            
        } 
        catch(FileNotFoundException e) {
            System.err.println("The configuration file you entered does not exist.");
            System.exit(0);
        }
        catch(IOException io) {
            System.err.println("Error encountered when reading in the configuration file.");
            System.exit(0);
        }
        finally {

            try {
                reader.close();
            } 
            catch (IOException e) {
                System.err.println("Error closing the FileReader data stream.");
                System.exit(0);
            }
        }
        return initialsetup;
    }//End getSetupFromFile
    
    /**
    * Helper method to print out the board
    *
    * @param String[][] board The board's current position.
    */
    @SuppressWarnings("unused")
    private static void printBoard(String[][] board) 
    {
        for(int i= 0; i < board.length; i++) 
        {
            for(int j = 0; j < board[i].length; j++) 
            {
                System.out.print(" " + board[i][j] + " ");
            }
            System.out.println();
        }
    }


    /**
    * The method used to start the Display application. Handles generating the GUI
    * and playing the game against a human opponent.
    *
    * @param stage The Primary stage for the applicaiton. 
    */ 
    @Override
    public void start(Stage stage) throws Exception {

        BorderPane mainpane = new BorderPane();
        Text directions = new Text("SELECT A COLUMN TO DROP INTO");
        
        BorderPane.setAlignment(directions, Pos.BOTTOM_CENTER);
        mainpane.setTop(directions);
        
        
        BorderPane middlepane = new BorderPane();
        BorderPane bottompane = new BorderPane();
        GridPane tiles = new GridPane();

        
        tiles.setAlignment(Pos.CENTER);
        
        int tilewidth = setup.length + 1;//Extra row for choosing a move
        int tileheight = setup[0].length;

        btns = new Button[tileheight][tilewidth]; 
        
        for(int i = 0; i < tilewidth; i++) 
        {
            for(int j = 0; j < tileheight; j++) 
            {
                
                if(i == 0)
                {
                    btns[j][i]  = new Button(j+"");
                    btn = btns[j][i];
                    btn.setText(j+"");
                    int col = j;
                    btns[j][i].setOnAction(new EventHandler<ActionEvent>() 
                    {
                        @Override
                        public void handle(ActionEvent event) 
                        {
                            column = col;
                            buttonPressed = true;
                        }
                    });
                }
                else 
                {
                    btns[j][i] = new Button();
                    btn = btns[j][i];
                    if(i != tilewidth) 
                    {
                       char text = (position.getPosition())[i-1][j];
                       btn.setText(String.valueOf(text));
                    }
                }
                btn.setPrefSize(30, 30);                
                tiles.add(btn, j, i);
            }
        }
        Scene scene = new Scene(mainpane);
        stage.setTitle("Connect 3");
        stage.setMinWidth(400);
        stage.setMinHeight(400);
        stage.setScene(scene);
        stage.show();

        Task<Void> uiUpdate = new Task<Void>()
        {
            @Override
            public Void call() throws Exception
            {
                while(!position.isFinal())
                {
                    Platform.runLater(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            for(int i = 0; i < tilewidth; i++) 
                            {
                                for(int j = 0; j < tileheight; j++) 
                                {                                    
                                    if(i == 0){}
                                    else 
                                    {
                                         char text = (position.getPosition())[i-1][j];
                                         btns[j][i].setText(String.valueOf(text));
                                    }
                                }
                            }
                        }
                    });
                    Thread.sleep(160);
                }
                Platform.runLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for(int i = 0; i < tilewidth; i++) 
                        {
                            for(int j = 0; j < tileheight; j++) 
                            {                                    
                                if(i == 0){}
                                else 
                                {
                                     char text = (position.getPosition())[i-1][j];
                                     btns[j][i].setText(String.valueOf(text));
                                }
                            }
                        }
                    }
                });
                return null;
            }
        };
        Thread updateThread = new Thread(uiUpdate);
        updateThread.setDaemon(true);
        updateThread.start();


        
        middlepane.setCenter(tiles);
        bottompane.setLeft(new Label("Play setting: " + play));
        
        mainpane.setBottom(bottompane);
        mainpane.setCenter(middlepane);

        Thread playThread = new Thread(new PlayWithHuman(position, true));
        playThread.setDaemon(true);
        playThread.start();        
        
    }

    /**
    * Basic method to update the Position. Uses a deep copy.
    *
    * @param _position The position to update to. 
    */ 
    public static void updatePosition(C3Position _position)
    {
        position = new C3Position(_position);
    }

    /**
    * Basic method to get the status of the buttons on the GUI.
    *
    * @return True is the button has been pressed for this turn false otherwise. 
    */ 
    public static boolean getStatus()
    {
        return buttonPressed;
    }   
    
    /**
    *  Basic method to reset the status of the button after the computers turn.
    * 
    */ 
    public static void setStatus()
    {
        buttonPressed = false;
    }  

    
    /**
    * Basic getter method to get the column that has been pressed most recently by 
    * the human player.
    *
    * @return The Most recent player move. 
    */ 
    public static int getCol()
    {
        return column;
    }  
}//end Connect3












