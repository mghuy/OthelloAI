package othelloai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author Leng Ghuy
 * @assignemt Othello AI
 * @class CSCI 312 - Intro to Artificial Intelligence
 * @version December 1, 2017
 * 
 * A class that stores information to represent every possible instance of an
 *      Othello board state
 */
public class Board {

    //------------ Global Variables ------------
    
    public Long myBB;
    public Long opponentBB;
    public Long occupiedBB;
    public int moveNumber;
    public int color; //1 == I B and -1 == I W
    public Boolean isOver;
    public final int[] index = {1, 7, 8, 9};
    public static final Long[] overflow = {
        0xFEFEFEFEFEFEFEFEl, //right
        0x7F7F7F7F7F7F7F00l, //downLeft
        0xFFFFFFFFFFFFFF00l, //down
        0xFEFEFEFEFEFEFE00l, //downRight
        0x7F7F7F7F7F7F7F7Fl, //left
        0x00FEFEFEFEFEFEFEl, //upRight
        0x00FFFFFFFFFFFFFFl, //up
        0x007F7F7F7F7F7F7Fl, //upLeft
    };
    public static final Long[] overflow2 = {
        0x7F7F7F7F7F7F7F7Fl, //left
        0xFEFEFEFEFEFEFE00l, //downRight
        0xFFFFFFFFFFFFFF00l,
        0x7F7F7F7F7F7F7F00l,
        0xFEFEFEFEFEFEFEFEl,
        0x007F7F7F7F7F7F7Fl,
        0x00FFFFFFFFFFFFFFl,
        0x00FEFEFEFEFEFEFEl,};
    public Scanner keyboard;
    public Random rand;

    //------------ Constructor ------------
    
    /**
     * @param color the color in which the AI will be playing as
     * 
     * Properly initializes all instance variables and set bitboard set bits correctly
     *      in accordance to color initialized
     */
    public Board(int color) {
        keyboard = new Scanner(System.in);
        moveNumber = 0;
        this.color = color;
        myBB = 0l;
        opponentBB = 0l;
        if (this.color == 1) {
            myBB |= 1l << 28 | 1l << 35;
            opponentBB |= 1l << 27 | 1l << 36;

        } else {
            myBB |= 1l << 27 | 1l << 36;
            opponentBB |= 1l << 28 | 1l << 35;
        }
    }

    /**
     * @param oldBoard an old board object to be copied
     * 
     * constructs a new board object based on location of pieces and values from
     *      an old board passed in
     */
    public Board(Board oldBoard) {
        myBB = oldBoard.myBB;
        opponentBB = oldBoard.opponentBB;
        moveNumber = oldBoard.moveNumber;
        color = oldBoard.color;
    }
    
    //------------ Class Methods ------------

    /**
     * @param me which player's turn -- 1 == AI turn and -1 == opponent
     * @return true if the game is over, or false otherwise
     * 
     * Checks the current game state to see if either player still has a move
     *      if no player has a move, the game is over the method returns true
     */
    public boolean isOver(int me) {
        if (me == -1) {
            return false;
        } else {
            ArrayList<Move> moves = checkValidMoves(1);
            if (moves.isEmpty()) {
                moves = checkValidMoves(-1);
                if (moves.isEmpty()) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * @param player the player who is making the move
     * @param valid the valid move to be made
     * 
     * Update the board in accordance to the indicated valid move to be made
     */
    public void applyMove(int player, Move valid) {
        if (valid.toggle != null) {
            if (player == 1) {
                myBB ^= 1l << valid.bitIndex;
            } else {
                opponentBB ^= 1l << valid.bitIndex;
            }
            myBB ^= valid.toggle;
            opponentBB ^= valid.toggle;
        }
    }

    /**
     * @param player the player who's possible valid moves we are search for
     * @return an arraylist of possible valid moves
     * 
     * Checks the board for all possible moves and discs to flip for each move.
     *      Return an arraylist of move objects with each having a corresponding
     *      bitboard of discs that would be flipped if the move was made
     */
    public ArrayList<Move> checkValidMoves(int player) {
        ArrayList<Move> moves = new ArrayList<>();

        Long empty = ~(myBB | opponentBB);
        Long me, opp;
        if (player == 1) {
            me = myBB;
            opp = opponentBB;
        } else {
            me = opponentBB;
            opp = myBB;
        }
        Long totalMoves = 0l;
        Long temp;
        int j = 0, k;

        for (int i = 0; i < 4; i++) {
            temp = opp & overflow[j] & overflow2[j];    //border buffer
            Long t = temp & (me >>> index[i]);
            for (k = 0; k < 5; k++) {
                t |= temp & (t >>> index[i]);
            }
            totalMoves |= (empty & (t >>> index[i]));   //if the final shift indicate an empty, its valid
            j++;
        }
        for (int i = 0; i < 4; i++) {
            temp = opp & overflow[j] & overflow2[j];    //border buffer
            Long t = temp & (me << index[i]);
            for (k = 0; k < 5; k++) {
                t |= temp & (t << index[i]);
            }
            totalMoves |= (empty & (t << index[i])); //if the final shift indicate an empty, its valid
            j++;
        }

        //locate the numeric bit index of each valid moves given the bitboard
        Move valid;
        Long toToggle;
        for (int bit = 0; bit < 64; bit++) {
            if (((totalMoves >>> bit) & 1) == 1) {
                toToggle = toToggle(bit, player);
                valid = new Move(bit, toToggle);
                moves.add(valid);
            }
        }
        
        //sorts the arrayList in descending order dependent upon location weights for searching
        Collections.sort(moves);
        
        return moves;
    }

    /**
     * @param bit the valid move index to be examined
     * @param player the player who's making the move
     * @return a long bitboard of location to flip discs
     * 
     * For the given move passed in, return the location of where discs need to flip
     *      in accordance to the move made
     */
    public Long toToggle(int bit, int player) {
        Long me, opp;
        if (player == 1) {
            me = myBB;
            opp = opponentBB;
        } else {
            me = opponentBB;
            opp = myBB;
        }
        Long bitBB = 1l << bit;
        Long toToggle = 0l;
        Long temp;
        Long checkDir;
        int j = 0;

        for (int i = 0; i < 4; i++) {
            temp = opp & overflow[j] & overflow2[j];    //border buffer
            Long t = temp & (bitBB >>> index[i]);
            checkDir = 0l;
            for (int k = 0; k < 6; k++) {
                if (t != 0l) {
                    checkDir |= t;
                    //a check that breaks the loop when the same player making the move
                    //piece is found indicating a strattle
                    if ((t >>> index[i] & me) != 0l) {
                        toToggle |= checkDir;
                        break;  
                    }
                }
                t = temp & (t >>> index[i]);
            }
            j++;
        }
        for (int i = 0; i < 4; i++) {
            temp = opp & overflow[j] & overflow2[j];    //border buffer
            Long t = temp & (bitBB << index[i]);
            checkDir = 0l;
            for (int k = 0; k < 6; k++) {
                if (t != 0l) {
                    checkDir |= t;
                    //a check that breaks the loop when the same player making the move
                    //piece is found indicating a strattle
                    if ((t << index[i] & me) != 0l) {
                        toToggle |= checkDir;
                        break;
                    }
                }
                t = temp & (t << index[i]);
            }
            j++;
        }
        return toToggle;
    }

    /**
     * @return the properly formated move string to be presented to the opponent
     * 
     * generate a valid move to make for the AI using alpha-beta pruning with
     *      iterative deepening
     */
    public String generateMove() {
        ArrayList<Move> moves = checkValidMoves(1);
        if (moves.isEmpty()) {
            if (color == 1) return "B";
            else return "W";
        } else {
            Move moveToMake;
            if (moveNumber == 1) moveToMake = moves.get(0);
            else moveToMake = iterativeDeep();
            
            if (moveToMake != null) {
                applyMove(1, moveToMake);
                String printMove = "";
                if (color == 1) {
                    printMove += ("B ");
                    printMove += (moveToMake.toString());
                    return printMove;
                } else {
                    printMove += ("W ");
                    printMove += (moveToMake.toString());
                    return printMove;
                }
            } else
                if (color == 1) return "B";
                else return "W";
        }
    }

    /**
     * @return the best move on a fully completed search tree
     * 
     * While there's still still time left for the move, search another layer deep
     *      with pruning for specific range based on previous depth
     */
    public Move iterativeDeep() {
        double alpha = Double.NEGATIVE_INFINITY;
        double beta = Double.POSITIVE_INFINITY;
        double inc = 250;
        double EPSILON = 0.001;
        
        //fully complete search at depth two first
        Move moveToMake = alphaBeta(this, 0, 1, alpha, beta, 2);
        System.out.printf("C --- Completed Search at Depth = %d --- Value = %1.3f\n", 2,moveToMake.value);
        Move newMove;
        int maxDepth = 4;
        
        //while there's still keep search deeper, but only up until the depth of moves
        //left before end game
        while (!OthelloAI.timeUP && 65-moveNumber>=maxDepth) {
            
            newMove = alphaBeta(this, 0, 1, alpha, beta, maxDepth);

            //pruning algorithm
            if (newMove != null) {
                if (Math.abs(newMove.value - alpha) < EPSILON) {
                    System.out.printf("C --- alphaBeta failed Low. Searching again on depth = %d\n", maxDepth);
                    alpha = newMove.value - 2 * inc;
                    beta = newMove.value;

                } else if (Math.abs(beta - newMove.value) < EPSILON) {
                    System.out.printf("C --- alphaBeta failed high. Searching again on depth = %d\n", maxDepth);
                    alpha = newMove.value;
                    beta = newMove.value + 2 * inc;

                } else {
                    System.out.printf("C --- Completed Search at Depth = %d --- Value = %1.3f\n", maxDepth,newMove.value);
                    alpha = newMove.value - inc;
                    beta = newMove.value + inc;
                    maxDepth += 2;
                    moveToMake = newMove;
                }
            }
        }
        return moveToMake;
    }

    /**
     * @return the literal move string made by the opponent
     * 
     * Takes the user input and creates a move object to be compared to a list
     *      of possible valid moves. If there's a match, apply that move to the
     *      current board state and switch players. If there's not a match, prompt
     *      the opponent for another input until a valid move is received
     */
    public String getMove() {
        ArrayList<Move> moves = checkValidMoves(-1);
        Move attempt;
        Move check = new Move();
        //if there are possible valid moves automatically assign the one to be
        //checked against to the first
        if (!moves.isEmpty()) 
            check = moves.get(0);
        
        System.out.println("C Enter move: ");
        String moveString = keyboard.nextLine();
        boolean valid = true;
        while (valid) {
            if (moveString.equals("W") || moveString.equals("B")) {
                return moveString;
            } else {
                attempt = new Move(moveString);
            }
            int i;
            for (i = 0; i < moves.size(); i++) {
                if (attempt.bitIndex == moves.get(i).bitIndex) {
                    check = moves.get(i);
                    break;  //break once a match is found and call apply move on the matched move
                }
            }
            //if the break doesn't happen (meaning no match was found) prompt the
            //oppoent again
            if (i == moves.size()) {
                System.out.println("C Invalid Move. Enter Move: ");
                moveString = keyboard.nextLine();
            } else {
                valid = false;
            }
        }
        applyMove(-1, check);
        return moveString;
    }

    /**
     * @param currentBoard the current board in the game tree
     * @param ply current level in the game tree (0 is the top level)
     * @param player max or min player (1 == AI and -1 == opponent)
     * @param alpha lowest value that max parent will allow
     * @param beta highest value that min parent will allow
     * @param maxDepth depth where the leaf nodes occur
     * @return the best possible move for the current board
     * 
     * a recursive alpha-beta search for the best possible move at the current board
     */
    public Move alphaBeta(Board currentBoard, int ply, int player, double alpha, double beta, int maxDepth) {
        if (ply >= maxDepth) {
            Move returnMove = new Move();
            returnMove.value = currentBoard.evaluate(currentBoard);
            return returnMove;
        } else {
            ArrayList<Move> moves = currentBoard.checkValidMoves(player);
            if (moves.isEmpty()) 
                moves.add(new Move()); //adding a pass move      
            Move bestMove = moves.get(0);
            for (Move moveToMake : moves) {
                Board newBoard = new Board(currentBoard);
                newBoard.applyMove(player, moveToMake);

                Move tempMove = alphaBeta(newBoard, ply + 1, -player, -beta, -alpha, maxDepth);
                
                //if time runs up while exploring an incomplete tree return null
                //and default to the move with a completed tree search
                if (OthelloAI.timeUP && maxDepth > 2) 
                    return null;
                
                moveToMake.value = -tempMove.value;
                /*if (ply == 0) {
                    System.out.printf("ply = %d, move = %s, value = %1.3f, depth = %d\n", ply, moveToMake, moveToMake.value, maxDepth);
                }*/
                if (moveToMake.value > alpha) {

                    bestMove = moveToMake;
                    alpha = moveToMake.value;
                    if (alpha > beta) {
                        return bestMove;
                    }
                }
            }
            return bestMove;
        }
    }
    
    /**
     * @param currentBoard the board to evaluate
     * @return the value of the board
     */
    public double evaluate(Board currentBoard) {
        Evaluate eval = new Evaluate(currentBoard);
        return eval.evaluate();
    }

    /**
     * @return the board printed in a nicely formated string
     */
    @Override
    public String toString() {
        int i;
        int bit = 0;
        String board = "C ";
        
        for (i = 0; i < 16; i++)
            board += ("**");
        board += ("\nC     ");

        for (char ch = 'a'; ch <= 'h'; ch++)
            board += ("|" + ch + "|");
        board += ("\nC     ");
        
        for (i = 0; i < 12; i++)
            board += ("==");
        board += ("\n");
        
        for (i = 1; i <= 8; i++) {
            board += ("C |" + i + "| ");
            for (int j = 1; j <= 8; j++) {
                if (color == 1) {
                    if (((myBB >>> bit) & 1) != 0) 
                        board += (" B ");
                    else if (((opponentBB >>> bit) & 1) != 0) 
                        board += (" W ");
                    else 
                        board += (" - ");
                } else {
                    if (((myBB >>> bit) & 1) != 0) 
                        board += (" W ");
                    else if (((opponentBB >>> bit) & 1) != 0)
                        board += (" B ");
                    else
                        board += (" - ");
                }
                bit++;
            }
            board += ("\n");
        }
        
        board += ("C     ");
        for (i = 0; i < 12; i++)
            board += ("==");
        board += "\n";
        
        if (color == 1) {
            board += ("C     Black: " + Integer.toString(Long.bitCount(myBB)) + " ---- ");
            board += ("White: " + Integer.toString(Long.bitCount(opponentBB)));
        } else {
            board += ("C     Black: " + Integer.toString(Long.bitCount(opponentBB)) + " ---- ");
            board += ("White: " + Integer.toString(Long.bitCount(myBB)));
        }
        board += ("\nC ");
        
        for (i = 0; i < 16; i++) 
            board = board.concat("**");
 
        return board;
    }
}