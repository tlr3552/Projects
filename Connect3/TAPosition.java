/**
* Position class for use with the GameEvaler class.
* This interface is used to represent the Take away game that can then be evaluated
* using the GameEvaler class.
*
* @author Arthur Lunn
*/public class TAPosition implements Position
{

    // The position value
    private Integer position;

    /**
    * Make the default constructor inaccesible.
    */
    private TAPosition()
    {

    }

    /**
    * Standard constructor.
    *
    * @param _position The initial position.
    */
    public TAPosition(Integer _position)
    {
        position = _position;
    }

    /**
    * Basic method to get the value of this position.
    *
    * @return The value of the current position
    */
    public Integer getPosition()
    {
        return position;
    }

    /**
    * Basic method to determine if this position is final.
    *
    * @return Whether or not this position is final
    */
    public boolean isFinal()
    {
        return (position==0);
    }

    /**
    * Basic method to get the current score
    *
    * @return The current score.
    */
    public int getScore()
    {
        int score;
        if (this.isFinal())
            score = 1;
        else
            score = 0;
        return score;
    }

    /**
    * Basic method to get the worst possible score.
    *
    * @return The worst possible score
    */
    public int getWorstScore()
    {
        return -1;
    }

    public Move[] getMoves()
    {
     
        Move[] moves = new Move[3];

        int resultantPosition;

        for(int i = 1; i < 4; i++){
            resultantPosition = position - i;
            if (resultantPosition > -1)
                moves[i-1] =new TAMove(i);
            else
                moves[i-1] = null;

        }
        return moves;
    }

    /**
    * Basic method to get the position resulting from this move
    *
    * @param 
    * @return the position that results from this move.
    */
    public Position getResultPosition(Move move)
    {
        return new TAPosition(position-(int)move.getMove());
    }

    /**
    * Basic method to reverse the board.
    *
    * @return The reverse board(if applicable).
    */
    public Position reverseBoard()
    {
        return this;
    }

    /**
    * Basic equals method, evaluates the equality of a position based on the position array.
    *
    * @param the Object to compare this object to.
    * @return true is equals false if not.
    */
    public boolean equals(Object obj)
    {
        TAPosition taPosition;
        if(obj instanceof TAPosition)
        {
            taPosition = (TAPosition)obj;
            return position.equals(taPosition.getPosition());
        }
        else
            return false;
    }

}