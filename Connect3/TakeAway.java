import java.util.Scanner;
/**
*
* TakeAway.java
*
* Driver class for the game of Take Away. This class is deisigned as a test of the
* GameEvaler class. It is used to play the game of take away against the computer.
*
* @author Arthur Lunn
*/
public class  TakeAway
{

    /**
    * The main method of the class. Carries out the playing of a game of takeaway
    * or evaluates the best play from a given position.
    *
    * @param args Used to specify the number of pennies to start with and the style of play.
    */
    public static void main(String[]args)
    {
        // The current position
        TAPosition position = null;

        // An instance of the evaluator to run game-play
        GameEvaler robot = new GameEvaler();

        // The best choice given a specific position on the board
        TAMove bestChoice;

        // Make sure that the correct number of CLAs were supplied
        if(args.length != 1 && args.length != 2)
        {
            System.err.println("Usage: java TakeAway [play|auto] num_pennies");
            System.exit(0);
        }

        // Run the most basic function of the program
        // Determine the best move given a position from the CLI
        else if(args.length ==1){
            position = setPosition(args[0]);
            bestChoice = (TAMove)robot.evaluatePosition(position, 0, false)[0];
            System.out.println("The best move would be to take " + bestChoice.getMove()
                               + " pennies.");
            System.exit(0);
        }

        // Run the game in human play mode
        if(args[0].toUpperCase().equals("PLAY"))
        {
            position = setPosition(args[1]);
            playWithHuman(position);
        }

        // Run the game in auto play mode (robot on robot)
        else if(args[0].toUpperCase().equals("AUTO"))
        {
            position = setPosition(args[1]);
            playAuto(position);
        }

        // If auto or play was not selected then the usage is invalid
        // This displays the appropriate message and exits gracefully.
        else
        {
            System.err.println("Usage: java TakeAway [play|auto] num_pennies");
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
    public static TAPosition setPosition(String _position) 
    { 
        // Place holder for the int value of the position
        int positionInt;

        // THe position to be returned
        TAPosition position = null;

        // Attempt to set the position catching any exceptions
        try 
        {
            positionInt = Integer.parseInt(_position);
            if(positionInt >0)
                position = new TAPosition(positionInt);
            else
                throw new Exception();
        }

        // If any exception is generated it is due to usage so display the
        // usage message
        catch(Exception e) 
        {
            System.err.println("Usage: java TakeAway [play|auto] num_pennies");
            System.exit(0);
        }
        return position;
    }

    /**
    * Method to simulate the playing of a game of take-away with one human
    * and one automated player. The human will go first.
    *
    * @param _position The initial position.
    * @return The resulting score.
    */
    public static int playWithHuman(Position _position)
    {   
        // Set the initil position
        TAPosition position = (TAPosition) _position;

        // Make a robot to decide what play is best
        GameEvaler robot = new GameEvaler();

        // Holder for the best options given a position
        Object[] robotChoice;

        TAMove bestMove;

        // The input from the human player
        int humanChoice;

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
                System.out.println("Your move. Choose 1, 2 or 3.");
                line = myScanner.nextLine();

                // See if the players move is valid 
                // and act accordingly
                try{
                    humanChoice = Integer.parseInt(line);
                    if (humanChoice>0 && humanChoice < 4)
                    {
                        position = (TAPosition) position.getResultPosition(new TAMove(humanChoice));
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
                               + position.getPosition()
                               + " left in the stack.\n");

            // As long as the game isn't over get the computer
            // players next move
            if(!position.isFinal())
            {
                robotChoice = robot.evaluatePosition(position, 0, false);
                bestMove = (TAMove) robotChoice[0];
                System.out.println("I believe my best move is "+ bestMove.getMove());
                
                position = (TAPosition) robotChoice[1];

                System.out.println("After my move there are " + position.getPosition()
                                     + " left in the stack.\n");                
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
        TAPosition position = (TAPosition) _position;

        // Make a game evaluator to make game choices
        GameEvaler robot = new GameEvaler();

        // Holder for the best choice given a specific position
        Object[] robotChoice;

        TAMove bestMove;

        // Initialize the robot id to 0 as robot 0 goes first
        int robotID = 0;

        // Keep playing until one player wins
        while(!position.isFinal())
        {

            robotChoice = robot.evaluatePosition(position, 0, false);
            bestMove = (TAMove) robotChoice[0];
            System.out.println("Robot " + robotID + " will take " + bestMove.getMove() + " pennies.");
            
            position = (TAPosition) robotChoice[1];

            System.out.println("After their move there are " + position.getPosition()
                                 + " left in the stack.\n");

            // Switch players
            robotID^=0x0001;
        }

        // Say who won
        System.out.println("Robot " + robotID + " is the winner.");

        return position.getScore();
    }
}