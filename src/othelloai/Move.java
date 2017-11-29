package othelloai;

import java.util.Scanner;

/**
 * 
 * @author Leng Ghuy
 * @assignemt Othello AI
 * @class CSCI 312 - Intro to Artificial Intelligence
 * @version December 1, 2017
 * 
 * The class that holds information pertaining to each individual move
 */
public class Move implements Comparable<Move> {
    
    //------------ Global Variables ------------
    
    public String moveString;   //literal move string in format "Color colLettter rowNum"
    public int bitIndex;    //the index corresponding to the bit on the bitboard
    public Long toggle;     //a bitboard with set bits at locations where discs need to toggle
    public Double value;    //the  value of a move determined by the evuation function
    
    //the weights of each location on the board
    public final int [] weight = {150,-40,10,05,05,10,-40,150,
                                  -40,-35,-1,-3,-3,-1,-35,-40,
                                  010,-01,05,01,01,05,-01,010,
                                  005,-03,01,00,00,01,-03,005,
                                  005,-03,01,00,00,01,-03,005,
                                  010,-01,05,01,01,05,-01,010,
                                  -40,-35,-1,-3,-3,-1,-35,-40,
                                  150,-40,10,05,05,10,-40,150};
    

    //------------ Constructor ------------
    
    /**
     * Creates an empty move that represents a pass
     */
    public Move () {
        toggle = null;
    }
    
    /**
     * @param moveString the literal move string to be converted in accordance to
     *      board representation
     * 
     * Create a move object based on user's input
     */
    public Move(String moveString) {
        this.moveString = moveString;
        
        //convert the string to correct corresponding bit format
        moveToBit(moveString);
    }
    
    /**
     * @param valid the bitIndex of the valid move to be made
     * @param toToggle a bitboard representing locations of discs to be flipped
     * 
     * Create a move object based on a possible valid move and it's corresponding
     *      discs to toggle
     */
    public Move(int valid, Long toToggle) {
        bitIndex = valid;
        toggle = toToggle;
    }
    
    //------------ Class Methods ------------
    
    /**
     * @param move the formated input string to be converted to correct index
     */
    public void moveToBit(String move) {
        //reading string one character at a time
        Scanner readMove = new Scanner(move);
        int bit;
        String color = readMove.next();
        String col = readMove.next();
        int row = readMove.nextInt();
        
        bit = col.toCharArray()[0]-97; //the char value will tell me which column number
        
        //depends on the row, increase bit by 8 for correct index
        switch(row) {
            case 1 : break;
            case 2 : bit+=8; break;
            case 3 : bit+=16; break;
            case 4 : bit+=24; break;
            case 5 : bit+=32; break;
            case 6 : bit+=40; break;
            case 7 : bit+=48; break;
            case 8 : bit+=56; break;
        }
        bitIndex = bit;
    }
    
    /**
     * prints each move in the desired format
     */
    @Override
    public String toString(){
        int count = 0;
        String move;
        int tempIndex = bitIndex;
        
        while(tempIndex>=8) {   //subtract until you get to top row
            tempIndex -= 8;
            count++;    //count will tell me what row it was first on
        }
        //convert the bitValue to the correct corresponding char
        move = Character.toString(Character.toChars(tempIndex+97)[0]); 
        move += (" " + Integer.toString(count+1));
        return move;
    }
    
    /**
     * @param bit the index location to be examined
     * @return the value/weight of that location on the board
     */
    public int locationWeight(int bit) {
        return weight[bit];
    }

    /**
     * @param move a Move object 
     * @return the value to be sorted by
     * 
     * Comparison method that sorts based on the weights of the move in accordance
     *      to its location on the board in descending order
     */
    @Override
    public int compareTo(Move move) {
        int compare = move.locationWeight(move.bitIndex);
        return compare-locationWeight(this.bitIndex);
    }
}