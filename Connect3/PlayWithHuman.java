import java.lang.Exception;
/**
*
* PlayWithHuman.java
*
* Handles the backend processing of the Connect 3 game with a human.
* Currently it 
*
*
* @author Tyler Russell, Arthur Lunn
*/
public class PlayWithHuman implements Runnable{    

    private C3Position position = null;

    // Whether to display to the command line interface.
    private boolean displayCLI;

    /**
    * Main constructor.
    *
    * @param _position The initial position.
    */
    public PlayWithHuman(C3Position _position, boolean _displayCLI){
        position = new C3Position(_position);
        displayCLI = _displayCLI;
    }

    /**
    * Method to simulate the playing of a game of Stones with one human
    * and one automated player. The human will go first.
    */
    public void run()
    {   

        // Make a robot to decide what play is best
        GameEvaler robot = new GameEvaler();

        // Holder for the best options given a position
        Object[] robotChoice;

        // Holder for the best move given a position
        C3Move bestMove;

        // The human players choice of column
        int humanColChoice = 0;    

        // Display the board initially
        // if the command line output is enables
        if(displayCLI)
            position.getPositionStatus();
 
        // Continue to play until a final position is reached
        while(!position.isFinal())
        {   
            while(!Connect3.getStatus())
            {
                try{
                    Thread.sleep(60);                
                } catch (Exception e)
                {}
            }
            position = (C3Position) position.getResultPosition(new C3Move(Connect3.getCol()));

            // Update the position on the GUI
            Connect3.updatePosition(position);            
            
            // Display the position on command line
            // if the command line output is enables
            if(displayCLI)
                position.getPositionStatus();

            // Reverse the board.
            position = position.reverseBoard();       

            if(position.isFinal())
            {
                if (position.getScore()==0)
                    System.out.println("Tie Game.");
                else    
                    System.out.println("You Win.");
            }

            // As long as the game isn't over get the computer
            // players next move
            if(!position.isFinal())
            {
                robotChoice = robot.evaluatePosition(position, 0, true);
                bestMove = (C3Move) robotChoice[0];
                System.out.println("I believe my best move is column " + bestMove.getMove() + ".");
                
                position = (C3Position) robotChoice[1];

                //**********************************************************
                // This is where the GUI needs to be updated based on the new position.

                position = position.reverseBoard();       

                Connect3.updatePosition(position);


                if(position.isFinal())
                {
                    if (position.getScore()==0)
                        System.out.println("Tie Game.");
                    else    
                        System.out.println("You Lose.");
                }
                else
                {
                    Connect3.setStatus();
                }                    
            }
        }
    }
}