/**
*
* SMove.java
*
* Basic class for representing a move in the Stones game.
* 
* @author Arthur Lunn
*/
public class SMove implements Move
{
    // The value of this move given as pile[0] and number of stones to take[1]
    private Integer[] move = new Integer[2];


    /**
    * Basic constructor for the TAMove class.
    *
    * @param pile The pile to take stones from.
    * @param stones the number of stones to take from the given pile.
    */
    public SMove(int pile, int stones)
    {
        move[0] = pile;
        move[1] = stones;
    }


    /**
    * Basic getter to return the pile number this move applies to.
    * @param the pile number this move applies to.
    */
    public int getPile()
    {
        return move[0];
    }


    /**
    * Basic getter to return the number of stones to be removed.
    * @param the number of stones to be removed
    */
    public int getStone()
    {
        return move[1];
    }


    /**
    * Basic getter. Returns the value of this move.
    *
    * @return the value of this move in form pile[0] number of stones[1].
    */
    public Integer[] getMove()
    {
        Integer[] _move = new Integer[2];
        System.arraycopy(move, 0 , _move, 0, move.length);
        return _move;
    }
}