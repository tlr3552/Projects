
/**
* 
*
* @author Arthur Lunn
*/
public interface Position
{

    /**
    * Basic method to determine if this position is final.
    *
    * @return Whether or not this position is final
    */
    public boolean isFinal();

    /**
    * Basic method to get the current score
    *
    * @return The current score.
    */
    public int getScore();

    /**
    * Basic method to get the worst possible score.
    *
    * @return The worst possible score
    */
    public int getWorstScore();

    /**
    * Basic method to get the value of this position.
    *
    * @return The value of the current position
    */
    public Object getPosition();

    /**
    * Basic method to get the position resulting from this move
    *
    * @param 
    * @return the position that results from this move.
    */
    public Position getResultPosition(Move move);

    /**
    * Basic method to get all legal moves from this position.
    *
    * @return All legal moves.
    */
    public Move[] getMoves();

    /**
    * Basic method to reverse the board.
    *
    * @return The reverse board(if applicable).
    */
    public Position reverseBoard();


}