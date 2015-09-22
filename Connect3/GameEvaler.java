import java.util.Hashtable;

/**
*
* GameEvaler.java
*
* Abstract framework used to solve simple games with perfect information.
*
* @author Arthur Lunn
*/
public class GameEvaler
{
    // This is the depth to which the solver will go; adjust this if
    // the solver is taking to long or if it's not solving correctly.
    static final int DEPTH_THRESHOLD = 10;
    
    private Hashtable<Position,Object[]>  memoizer = new Hashtable<Position, Object[]>();

    /**
    * Method to evaluate the best move given a specific position in
    * a game. Generalized algorithm will work with any game.
    *
    * @param position The initial position
    * @param depth the current depth of the recursion.
    * @param useDepth Determines if the depth function should be used.
    * @return returns best move(0), best position(1) and best score(2) in an array.
    */
    public Object[] evaluatePosition(Position position, int depth, boolean _useDepth)
    {


        boolean useDepth = _useDepth;


        //index for for loop checking move validity
        int i;
        // Check to see if we've encountered this position before.
        if(memoizer.get(position)!=null)
        {
            return memoizer.get(position);
        }
        // A return array holding the
        // best move(0), best position(1) and best score(2)
        Object[] returns = new Object[3];

        // set the initial score to the worse score possible
        returns[2] = position.getWorstScore();

        // A list of all possible moves
        Move[] moves;

        // The return, a list o results from a recursive call to this method
        // This lets results to be "passed up" through calls
        Object[] evaluatedResults;

        // The "next" position given a specific move
        Position nextPosition;

        // If this position is final return the score
        if(position.isFinal())
        {
            returns[2] = position.getScore();
            return returns;
        }
        // Otherwise evaluate all possible moves
        else if((depth++ > DEPTH_THRESHOLD) && useDepth)
        {
            returns[2] = 0;
            return returns;
        }
        else
        {
            
            moves = position.getMoves();


            // Set the best move to an arbitrary move
            // This ensure that if all moves are equally bad the program
            // still returns move.
            for(i = 0; i < moves.length && moves[i] == null; i++);
            if(moves[i] == null)
            {
                returns[2] = 0;
                return returns;
            }
            else
            {
                returns[0] = moves[i];
                returns[1] = position.getResultPosition((Move)returns[0]);
            }

            for(Move move : moves)
            {
                if(move != null)
                {
                    nextPosition = position.getResultPosition(move);
                    evaluatedResults = evaluatePosition(nextPosition.reverseBoard(), depth, useDepth);
                    evaluatedResults[2] = -(int)evaluatedResults[2];
                    if((int)evaluatedResults[2] > (int)returns[2] )
                    {
                        returns[0] = move;
                        returns[1] = nextPosition; // This needs to be reversed to maintain correct order
                        returns[2] = evaluatedResults[2];
                    }
                }    
            }
        }
        // Remember this result
        memoizer.put(position, returns);
        return returns;
    }
}