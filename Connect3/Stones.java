import java.util.Scanner;
/**
*
* Stones.java
*
* Driver class for the game of Stones. This class is deisigned as a test of the
* GameEvaler class. It is used to play the game of stones against the computer.
*
* @author Arthur Lunn
*/
public class  Stones
{

    /**
    * The main method of the class. Carries out the playing of a game of Stones
    * or evaluates the best play from a given position.
    *
    * @param args Used to specify the number of stones to start with and the style of play.
    */
    public static void main(String[]args)
    {
        // The current position
        SPosition position = null;

        Integer[] piles;

        // An instance of the evaluator to run game-play
        GameEvaler robot = new GameEvaler();

        // The best choice given a specific position on the board
        SMove bestChoice;

        // Make sure that the correct number of CLAs were supplied
        if(args.length == 0)
        {
            System.err.println("Usage: java Stones [play|auto] *num_stones");
            System.exit(0);
        }



        // Run the game in human play mode
        if(args[0].toUpperCase().equals("PLAY"))
        {
            position = setPosition(1, args);
            playWithHuman(position);
        }

        // Run the game in auto play mode (robot on robot)
        else if(args[0].toUpperCase().equals("AUTO"))
        {
            position = setPosition(1, args);
            playAuto(position);
        }
        
        // Run the most basic function of the program
        // Determine the best move given a position from the CLI
        else if(args.length > 0 ){

            position = setPosition(0, args);

            bestChoice = (SMove)robot.evaluatePosition(position, 0, false)[0];

            System.out.println("The best move would be to take " + bestChoice.getStone()
                               + " stones from pile "+ bestChoice.getPile() +".");
            System.exit(0);
        }

        // If auto or play was not selected then the usage is invalid
        // This displays the appropriate message and exits gracefully.
        else
        {
            System.err.println("Usage: java Stones [play|auto] *num_stones");
            System.exit(0);     
        }

    }

    /**
    * Basic helper method for setting the position to a position object given a.
    * position string.
    *
    * @param _position The initial position in string form.
    * @return The position object set from the paramater.
    */
    public static SPosition setPosition(int j, String[] _position) 
    { 
        // Place holder for the int values of the position
        Integer[] positionInt = new Integer[_position.length-j];

        // THe position to be returned
        SPosition position = null;

        // Attempt to set the position catching any exceptions
        try 
        {

            for(int i = 0; i<(_position.length-j); i++)
            {
                positionInt[i] = Integer.parseInt(_position[i+j]);
            }
            if(positionInt != null)
            {
                position = new SPosition(positionInt);
            }
            else
                throw new Exception();
        }

        // If any exception is generated it is due to usage so display the
        // usage message
        catch(Exception e) 
        {
            System.err.println("Usage: java Stones [play|auto] *num_stones");
            System.exit(0);
        }
        return position;
    }

    /**
    * Method to simulate the playing of a game of Stones with one human
    * and one automated player. The human will go first.
    *
    * @param _position The initial position.
    * @return The resulting score.
    */
    public static int playWithHuman(Position _position)
    {   
        // Set the initil position
        SPosition position = (SPosition) _position;

        // Make a robot to decide what play is best
        GameEvaler robot = new GameEvaler();

        // Holder for the best options given a position
        Object[] robotChoice;

        // Holder for the best move given a position
        SMove bestMove;

        // The human players choice of pile
        int humanPileChoice = 0;

        // The human players choice of stones
        int humanStoneChoice = 0;
 
        //Show the initial status of the "board"
        position.getPositionStatus();    
 
        // Continue to play until a final position is reached
        while(!position.isFinal())
        {   
            // The players input
            String line;

            // Flag to determine if the player has selected a valid move
            boolean validMove = false;

            // Scanner to receive player input
            Scanner myScanner = new Scanner(System.in);            

            // Ask the player for a move until a valid move is entered
            while(!validMove)
            {
                // Prompt player for input
                System.out.println("Your move. Choose pile " + position.getPileChoices() + ".");
                line = myScanner.nextLine();

                // See if the players move is valid 
                // and act accordingly
                try{
                    humanPileChoice = Integer.parseInt(line)-1;
                    System.out.println("Choose # of stones from 1 - " + position.getPile(humanPileChoice) + ".");
                    line = myScanner.nextLine();
                    humanStoneChoice = Integer.parseInt(line);
                    if(humanStoneChoice > 0 && humanStoneChoice < position.getPile(humanPileChoice)+1)
                    {
                        position =(SPosition) position.getResultPosition(new SMove(humanPileChoice, humanStoneChoice));
                        validMove = true;
                    }
                    else
                        throw new Exception();
                }
                catch(Exception e)
                {
                    System.out.println("I'm sorry, that's not a valid move");
                }
            }

            // Reset the flag
            validMove = false;

            System.out.println("After your move there are " 
                               + position.getPile(humanPileChoice)
                               + " left in the stack " + (humanPileChoice + 1) + ".\n");

            if(position.isFinal())
                System.out.println("You Lose.");

            // As long as the game isn't over get the computer
            // players next move
            if(!position.isFinal())
            {
                robotChoice = robot.evaluatePosition(position, 0, false);
                bestMove = (SMove) robotChoice[0];
                System.out.println("I believe my best move is " + bestMove.getStone()
                               + " stones from pile "+ (bestMove.getPile() + 1) +".");
                
                position = (SPosition) robotChoice[1];

                position.getPositionStatus();                
                if(position.isFinal())
                    System.out.println("You Win.");
            }
        }
        return position.getScore();
    }

    /**
    * Method for auto play. Will simulate a game between two computer players.
    *
    * @_position The initial position of the game.
    * @return The score of the game.
    */
    public static int playAuto(Position _position)
    {   
        // Current position of the game
        SPosition position = (SPosition) _position;

        // Make a game evaluator to make game choices
        GameEvaler robot = new GameEvaler();

        // Holder for the best choice given a specific position
        Object[] robotChoice;

        SMove bestMove;

        // Initialize the robot id to 0 as robot 0 goes first
        int robotID = 0;

        // Show the intial board status
        position.getPositionStatus();                


        // Keep playing until one player wins
        while(!position.isFinal())
        {

            robotChoice = robot.evaluatePosition(position, 0, false);
            bestMove = (SMove) robotChoice[0];

            System.out.println("\nRobot " + robotID + " will take " + bestMove.getStone() + " stones from pile " + bestMove.getPile() + ".");
            
            position = (SPosition) robotChoice[1];

            System.out.print("\nAfter their move ");

            position.getPositionStatus();                


            // Switch players
            robotID^=0x0001;
        }

        // Say who won
        System.out.println("Robot " + robotID + " is the winner.");

        return position.getScore();
    }
}