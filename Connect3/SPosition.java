import java.util.ArrayList;
import java.util.Arrays;

/**
* Position class for use with the GameEvaler class.
* This interface is used to represent the Stones game that can then be evaluated
* using the GameEvaler class.
*
* @author Arthur Lunn
*/public class SPosition implements Position
{

    // The position value
    private Integer[] position = null;

    /**
    * Make the default constructor inaccesible.
    */
    private SPosition()
    {

    }

    /**
    * Standard constructor.
    *
    * @param _position The initial position.
    */
    public SPosition(Integer[] _position)
    {
        position = new Integer[_position.length];
        System.arraycopy(_position, 0, position, 0, _position.length);
    }

    /**
    * Basic method to get the value of this position.
    *
    * @return The value of the current position
    */
    public Integer[] getPosition()
    {
        Integer[] _position = null;
        if(position!=null)
        {
            _position = new Integer[position.length];
            System.arraycopy(position, 0, _position, 0, position.length);
        }
        return _position;
    }

    /**
    * Basic method to get the stones in a given pile.
    *
    * @return The number of stones in a given pile i.
    */
    public int getPile(int i)
    {
        return position[i];
    }

    /**
    * Basic method to determine if this position is final.
    *
    * @return Whether or not this position is final
    */
    public boolean isFinal()
    {
        boolean _return = true;
        for(int i: position)
        {
            if(i!=0)
                _return = false;
        }
        return _return;
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


    /**
    * Basic method to get all availabel moves.
    *
    * @return All available moves
    */
    public Move[] getMoves()
    {
     
        ArrayList<SMove> moves = new ArrayList<SMove>();

        for(int i=0; i < position.length; i++)
        {
            for(int j = 1; j < position[i]+1; j++)
                moves.add(new SMove(i,j));
        }

        return moves.toArray(new Move[moves.size()]);
    }

    /**
    * Basic method to get the position resulting from this move
    *
    * @param move the move that is being made
    * @return the position that results from this move.
    */
    public Position getResultPosition(Move move)
    {
        //convert the move to an SMove
        SMove sMove = (SMove) move;
        
        //Get the specifics of the SMove
        Integer[] sMoveArray = sMove.getMove();

        //get a copy of the position to manipulate as the return
        Integer[] _position = new Integer[position.length];
        System.arraycopy(position, 0, _position, 0, position.length);

        _position[sMoveArray[0]]-=sMoveArray[1];

        return new SPosition(_position);
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
    * Basic method to get all the pile choices in String form.
    *
    * @return A list of all pile choices with comma formating.
    */
    public String getPileChoices()
    {
        // return string
        String _return = "";

        // index for the last true pile choice
        int j = position.length;

        boolean multipleChoices = false;

        // ensure the "last" value is not 0
        while(position[j-1]==0)
            j--;

        // Find all valid piles
        for(int i=0; i<j-1; i++)
        {
            if(position[i]!=0)
            {
                _return += (i+1)+", ";
                multipleChoices = true;
            }
        }
        if(multipleChoices)
            _return += "or " + (j);
        else
            _return += ""+ j;
        return _return;
    }


    /**
    * Basic method for printing out the status of the current position.
    * Shows the piles and the number of stones in each pile.
    */
    public void getPositionStatus()
    {
        System.out.println("The piles are currently:");
        for(int i = 0; i<position.length; i++)
        {
            System.out.println("Pile " + (i+1) + " has " + position[i] + " stones remaing.");
        }
    }

    /**
    * Basic equals method, evaluates the equality of a position based on the position array.
    *
    * @param the Object to compare this object to.
    * @return true is equals false if not.
    */
    public boolean equals(Object obj)
    {
        Integer[] _position = null;
        SPosition sPosition;
        if(obj instanceof SPosition)
        {
            sPosition = (SPosition) obj;
            _position = (Integer[]) sPosition.getPosition();
            return Arrays.equals(position, _position);
        }
        else
            return false;
    }

}