
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
        
        while(!game.isOver()) {
            if (me == 1) {
                move = game.generateMove(me);
                System.out.println(move);
            } else
                move = game.getMove(me);
            me *= -1;
            System.out.println("C " + move);
            System.out.println(game.toString());
        }
        System.out.println("C Game is Finished");
    }
    
}
