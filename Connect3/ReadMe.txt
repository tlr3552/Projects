This framework is designed to be used in conjunction with classes designed to represent a game with the following features (adapted from "Winning Ways" by Berlekamp, Conway, and Guy)
    There are just two players, often called Left and Right.

    There are several, usually finitely many, positions, and often a particular starting position.

    There are clearly defined rules that specify the moves that either player can make from a given position.

    The two players Left and Right move alternately.

    There are specific rules that determine when the game ends.

    The rules ensure that play will always come to an end. This is called the ending condition. So there can be no games that can last forever.

    Both players know everything that is going on, i.e., there is complete information.

    There are no chance moves such as rolling dice or shuffling cards.

    The moving/winning rules for Left and Right may not be the same.

    At the end of the game there is a definite score. We will assume that the first player wants the largest possible score while the second player wants the smallest score. (You might want to think of zero as a tie score and that the second player wants to maximize the negative of the final score.)

The framework itself relies on the position interface and the Move interface. In order for the GameEvaler class to work with a given game the game must be defined in the context of a specific class that implements the position interface as well as the Move interface. In combination these two files can represent the back end of any game described above the data types for both interface are generalized as to provide maximum forward adaptability.

This specific program is set up to simulate the playing of the game take away, Stones, or Connect 3.

Take away is played by removing 1, 2, or 3 pennies from a stack of pennies until one player takes the last penny. The player who takes the last penny losses. In order to start a game of take away the following usage must be used:

    Usage: java TakeAway [play|auto] num_pennies

Stones is played by removing any number of stones from any single pile of stones.  In order to start a game of take away the following usage must be used:

    Usage: java Stones [play|auto] *Piles

Connect 3 is played by placing X's and O's in a grid until one player gets 3 consecutive symbols up down or sideways. In order to start a game of take away the following usage must be used:

    Usage: java Connect3 [play] <file name>

Both the play and auto options are optional. Only Take Away and Stones use the auto play. Only one option may be selected (you cannot play and auto). If play is selected the game will run with one human player and one computer player starting with the human player. If auto is selected the game will run with two computer players. If neither option is selected the game will simply return the optimal next move given the supplied position.

More information on the specific implementation of the Position interface can be found in the position file's documentation.