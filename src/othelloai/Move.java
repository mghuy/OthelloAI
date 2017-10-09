package othelloai;

import java.util.Scanner;

/**
 *
 * @author Leng 
 */
public class Move {
    
    public String moveString;
    public int bitIndex;
    public Long toggle;

    public Move () {
        
    }
    /**
     *
     * @param moveString
     */
    public Move(String moveString) {
        this.moveString = moveString;
        moveToBit(moveString);
    }
    
    public Move(int valid, Long toToggle) {
        bitIndex = valid;
        toggle = toToggle;
    }
    
    public void moveToBit(String move) {
        Scanner readMove = new Scanner(move);
        int bit;
        String color = readMove.next();
        String col = readMove.next();
        int row = readMove.nextInt();
        bit = col.toCharArray()[0]-97;
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
    
    @Override
    public String toString(){
        int count = 0;
        String move;
        while(bitIndex>=8) {
            bitIndex -= 8;
            count++;
        }
        move = Character.toString(Character.toChars(bitIndex+97)[0]);
        move += (" " + Integer.toString(count+1));
        
        return move;
    }
}
