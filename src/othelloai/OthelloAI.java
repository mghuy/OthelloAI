
package othelloai;

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
        System.out.print("C Enter starting color: ");
        String startingColor = keyboard.nextLine();
        if (startingColor.equals("I B")) {
            me = 1;
            System.out.println("  R B");
        } else {
            me = -1;
            System.out.println("  R W");
        }
        
        Board game = new Board(me);
        System.out.println(game.toString());
        
        while(!game.isOver()) {
            if (me == 1) {
                move = game.generateRandMove(me);
            } else
                move = game.getMove(me);
            me *= -1;
            System.out.println(move);
            System.out.println(game.toString());
        }
    }
    
}
