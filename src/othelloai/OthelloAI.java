
package othelloai;

import java.util.Arrays;
import java.util.Scanner;

/**
 *
 * @author Leng
 */
public class OthelloAI {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Scanner keyboard = new Scanner(System.in);
        
        int me;
        String move;
        System.out.println("C Enter starting color: ");
        String startingColor = keyboard.nextLine();
        if (startingColor.equals("I B")) {
            me = 1;
            System.out.println("R B");
        } else {
            me = -1;
            System.out.println("R W");
        }
        
        Board game = new Board(me);
        System.out.println(game.toString());
        int [][] arr;
        
        while(!game.isOver(me)) {
            if (me == 1) {
                //arr = game.checkValidMoves(me);
                move = game.generateMove(me);
                System.out.println(move);
                //System.out.println(Arrays.deepToString(arr));
            } else {
                move = game.getMove(me);
                //arr = game.checkValidMoves(me);
                //System.out.println(Arrays.deepToString(arr));
            }
            me *= -1;
            //System.out.println("move);
            System.out.println(game.toString());
        }
        
        if(game.color == 1) {
            System.out.println("B");
        } else System.out.println("W");
        System.out.println("C Game is Finished");
    }
    
}
