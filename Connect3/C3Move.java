/**
* Basic class for representing a move in the connect 3 game.
* 
* @author Arthur Lunn
*/
public class C3Move implements Move
{
    // The value of this move
    private int move;

    /**
    * Basic constructor for the TAMove class.
    *
    * @param the value to set this move to.
    */
    public C3Move(int _move)
    {
        move = _move;
    }

    /**
    * Basic getter. Returns the value of this move.
    *
    * @return the value of this move.
    */
    public Integer getMove()
    {
        return move;
    }
}