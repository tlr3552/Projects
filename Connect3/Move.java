/**
*
* Move.java
* 
* Basic interface for representing moves for a given game.
* This class is designed to be used in conjunction with the Position interface 
* and GameEvaler class to represent a complete game.
* @author Arthur Lunn
*/
public interface Move
{
    /**
    * Basic getter to return the value of the given move.
    * @return The value of this move.
    */
    public Object getMove();
}