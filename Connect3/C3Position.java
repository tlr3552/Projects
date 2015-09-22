import java.util.Arrays;

/**
*
*  C3Position.java
*
* Position class for use with the GameEvaler class.
* This class is used to represent the Connect Three game that can then be evaluated
* using the GameEvaler class.
*
* @author Arthur Lunn
*/public class C3Position implements Position
{

    // The position value
    private char[][] position;
    private int lastMoveRow;
    private int lastMoveCol;
    private boolean isFinal;


    /**
    * Make the default constructor inaccesible.
    */
    private C3Position()
    {

    }

    /**
    * Standard constructor.
    *
    * @param _position The initial position.
    */
    public C3Position(char[][] _position)
    {
        int col, row;
        position = new char[_position.length][_position[0].length];
        for(col = 0; col< position.length; col++)
        {
            for( row = 0; row < position[col].length; row++)
            {
                position[col][row] = _position[col][row];
            }
        }
    }

    /**
    * Copy constructor with last move information.
    *
    * @param _position The initial position.
    * @param _lastMoveCol The column selected for the last move.
    * @param _lastMoveRow The row the last piece played is in.
    */
    public C3Position(C3Position _position, int _lastMoveCol, int _lastMoveRow)
    {
        this(_position.getPosition());
        lastMoveCol = _lastMoveCol;
        lastMoveRow = _lastMoveRow;
    }


    /**
    * Simple Copy constructor.
    *
    * @param _position The initial position.
    */
    public C3Position(C3Position _position)
    {
        this(_position.getPosition());
        lastMoveRow = _position.lastMoveRow;
        lastMoveCol = _position.lastMoveCol; 
    }

    /**
    * Basic method to get the value of this position.
    *
    * @return The value of the current position
    */
    public char[][] getPosition()
    {
        int col, row;
        char[][] _position = new char[position.length][position[0].length];
        for(col = 0; col< position.length; col++)
        {
            for( row = 0; row < position[col].length; row++)
            {
                _position[col][row] = position[col][row];
            }
        }
        return _position;
    }

    /**
    * Basic method to determine if this position is final.
    *
    * @return Whether or not this position is final
    */
    public boolean isFinal()
    {
        if(isFinal)
            return true;
        else
        {
            boolean threeInARow = this.check3(lastMoveCol, lastMoveRow);
            boolean boardFull = true;
            if( !threeInARow && boardFull)
            {
                for ( int col = 0; col < position[0].length && boardFull; col ++)
                {
                    char topChar = position[0][col];
                    if (topChar != 'X' && topChar != 'O')
                        boardFull = false;
                }
            }
            isFinal = boardFull || threeInARow;
            return isFinal;
        }
    }

    /**
    * Basic method to get the current score
    *
    * @return The current score.
    */
    public int getScore()
    {
        if (this.check3(lastMoveCol, lastMoveRow))
            return -1;
        else
            return 0;
    }

    /**
    * Basic method to get the worst possible score.
    *
    * @return The worst possible score
    */
    public int getWorstScore()
    {
        return -2;
    }

    /**
    * Basic method to get all availabel moves.
    *
    * @return All available moves
    */
    public Move[] getMoves()
    {
        Move[] moves = new Move[position[0].length];
        for(int i = 0; i<position[0].length; i++)
        {
            if(position[0][i] == '.')
            {
                moves[i] = new C3Move(i);
            }
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
        int row, col;
        char[][] _position = this.getPosition();
        
        col = (int) move.getMove();
        
        for( row = _position.length-1; _position[row][col] != '.' && row >0; row--)
        {
        }

        _position[row][col] = 'X';
        return new C3Position(new C3Position(_position), col, row);
    }

    /**
    * Basic method to reverse the board.
    *
    * @return The reverse board(if applicable).
    */
    public C3Position reverseBoard()
    {
        int row, col;
        char[][] _position = this.getPosition();

        for( col = 0; col < _position.length; col++)
        {
            for( row = 0; row < _position[col].length; row++)
            {
                if(_position[col][row] == 'X')
                    _position[col][row] = 'O';
                else if(_position[col][row] == 'O')
                    _position[col][row] = 'X';
            }
        }
        return new C3Position(new C3Position(_position), this.lastMoveCol, this.lastMoveRow);
    }

    /**
    * Basic equals method, evaluates the equality of a position based on the position array.
    *
    * @param the Object to compare this object to.
    * @return true is equals false if not.
    */
    public boolean equals(Object obj)
    {
        C3Position c3Position;
        if(obj instanceof C3Position)
        {
            c3Position = (C3Position)obj;
            return Arrays.deepEquals(this.position, c3Position.getPosition());
        }
        else
            return false;
    }

    private boolean check3(int col, int row)
    {
        int consecutiveXs = 0;
        int consecutiveOs = 0;
        boolean threeInARow = false;
        int i, j;


        for( i = row-2; i<row+3 && !threeInARow; i++)
        {   try
            {
                if(position[i][col] == 'O')
                {
                    consecutiveXs++;
                }
                else
                    consecutiveXs = 0;   
            } catch (Exception e)
            {
                consecutiveXs = 0;
            };     
            if(consecutiveXs > 2)
                threeInARow = true;
        }
        for( j = col-2; j<col+3  && !threeInARow; j++)
        {   try
            {
                if(position[row][j] == 'O')
                {
                    consecutiveXs++;
                }
                else
                    consecutiveXs = 0;   
            } catch (Exception e)
            {
                consecutiveXs = 0;
            };     
            if(consecutiveXs > 2)
                threeInARow = true;
        }
        for( j = col-2, i = row+2; i>row-3 && !threeInARow; j++, i--)
        {   try
            {
                if(position[i][j] == 'O')
                {
                    consecutiveXs++;
                }                
                else
                    consecutiveXs = 0;   
            } catch (Exception e)
            {
                consecutiveXs = 0;
            };     
            if(consecutiveXs > 2)
                threeInARow = true;
        }
        for( j = col-2, i = row-2; i<row+3 && !threeInARow; j++, i++)
        {   try
            {
                if(position[i][j] == 'O')
                {
                    consecutiveXs++;
                }                
                else
                    consecutiveXs = 0;   
            } catch (Exception e)
            {
                consecutiveXs = 0;
            };     
            if(consecutiveXs > 2)
                threeInARow = true;
        }
        return threeInARow;
    }


    /**
    * Basic method for printing out the status of the current position.
    * Shows connect 3 board and all played Xs and Os.
    */
    public void getPositionStatus()
    {
        System.out.println("The board is currently:");
        for(int col = 0; col<position.length; col++)
        {
            for(int row = 0; row < position[col].length; row++)
            {
                System.out.print(position[col][row]);
            }
            System.out.println();
        }
    }

}
