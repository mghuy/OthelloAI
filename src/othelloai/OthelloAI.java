package othelloai;

import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Leng Ghuy
 * @assignemt Othello AI
 * @class CSCI 312 - Intro to Artificial Intelligence
 * @version December 1, 2017
 * 
 * An artificial intelligence program that plays the game Othello
 */
public class OthelloAI {
    
    //------------ Global Variables ------------

    //time array.....each position represents a percentage of the remaining time to be used
    static double timeAllocation[] = {0.015, 0.015, 0.015, 0.015, 0.025, 0.025, 0.025, 0.025, 0.025, 0.025,
                                      0.048,  0.048, 0.048, 0.048, 0.048, 0.048, 0.050, 0.051, 0.052, 0.053,
                                      0.044,  0.045, 0.049, 0.049, 0.049, 0.051, 0.053, 0.055, 0.057, 0.059,
                                      0.060, 0.067, 0.071, 0.085, 0.093, 0.120, 0.155, 0.220, 0.230, 0.250,
                                      0.250, 0.270, 0.270, 0.265, 0.265, 0.235, 0.200, 0.180, 0.160, 0.130,
                                      0.100, 0.080, 0.080, 0.080, 0.080, 0.090, 0.090, 0.100, 0.100, 0.100,
                                      0.100, 0.100, 0.100, 0.105, 0.105, 0.105, 0.105, 0.105, 0.105, 0.105
                                    };

    public static int timeRemaining=600; // a variable to keep track of remaining time (start at 10 minutes)

    static Timer timer; //use this to start the interupt task
    public static boolean timeUP;  //boolean flag to check frequently for time up....this is set to true in the 
                                  //interrupt task
  
    //------------ Main Method ------------
    
    /**
     * @param args the command line arguments
     * 
     * Constructor method that initializes the loop to begin the game of Othello
     */
    public static void main(String[] args) {
        Scanner keyboard = new Scanner(System.in);
        
        int myTurn; //initialized to 1 if it's AI turn, and -1 if it's opponent's turn
        String move;
        
        System.out.println("C Enter starting color: ");
        String startingColor = keyboard.nextLine();
        
        //initializes the AI to a color based on user input
        if (startingColor.equals("I B")) {
            myTurn = 1;
            System.out.println("R B");
        } else {
            myTurn = -1;
            System.out.println("R W");
        }
        
        //initialize a new game with starting board position
        Board game = new Board(myTurn);
        System.out.println(game.toString());
        
        //loops until a game over condition is met
        while(!game.isOver(myTurn)) {
            System.out.println("C Move #" + game.moveNumber);
            if (myTurn == 1) {
                game.moveNumber++;
       
                //------- Timer - Dr.Mec -------
                timeUP = false;
                timer = new Timer();
                int timeForMove = (int) (timeAllocation[game.moveNumber]*(double)timeRemaining);
                timer.schedule(new InterruptTask(), timeForMove*1000);
                //-------------------------------
                
                move = game.generateMove();
                
                if(!timeUP)
                    timer.cancel();
                timeRemaining -= timeForMove;
                
                System.out.println(move);
            } else {
                game.moveNumber++;
                game.getMove();
            }
            
            //switches player's turn
            myTurn *= -1;
            System.out.println(game.toString());
        }
        
        //Print out final pass move
        if(game.color == 1) {
            System.out.println("B");
        } else System.out.println("W");
        System.out.println("C Game is Finished");
    }
    
    /**
     * Keeps track of run time and interrupts it when time limit for that move is
     *      reached
     */
    public static class InterruptTask extends TimerTask {
        @Override
        public void run() {
            System.out.println("C ****>timeup");
            timeUP = true;
            timer.cancel();
        }
    }
}
