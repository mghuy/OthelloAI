package othelloai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author Leng
 */
public class Board {
    
    public Long myBB;
    public Long opponentBB;
    public Long occupiedBB;
    public Move move;
    public int color; //1 == I B and -1 == I W
    public Boolean isOver;
    public Scanner keyboard;
    public Random rand;
    public ArrayList<ArrayList> validMoves;
    public final ArrayList<Integer> N_EDGE = new ArrayList<>();
    public final ArrayList<Integer> E_EDGE = new ArrayList<>();
    public final ArrayList<Integer> S_EDGE = new ArrayList<>();
    public final ArrayList<Integer> W_EDGE = new ArrayList<>();
    
    private final int MAX_DEPTH = 4;
    private Board boardToEvaluate;
    
    public Board (int color) {
        keyboard = new Scanner(System.in);
        this.color = color;
        myBB = 0l;
        opponentBB = 0l;
        if (this.color == 1) {
            myBB |= 1l<<28 | 1l<<35;
            opponentBB |= 1l<<27 | 1l<<36;
        } else {
            myBB |= 1l<<27 | 1l<<36;
            opponentBB |= 1l<<28 | 1l<<35;
        }
        validMoves = new ArrayList<>();
        N_EDGE.addAll(Arrays.asList(0,1,2,3,4,5,6,7));
        E_EDGE.addAll(Arrays.asList(7,15,23,31,39,47,55,63));
        S_EDGE.addAll(Arrays.asList(56,57,58,59,60,61,62,63));
        W_EDGE.addAll(Arrays.asList(0,8,16,24,32,40,48,56));
        move = new Move();
    }
    
    public Board (Board oldBoard) {
        boardToEvaluate = oldBoard;
    }
    
    public boolean isOver() {
        checkValidMoves(1);
        if (validMoves.isEmpty()) {
            checkValidMoves(-1);
            if (validMoves.isEmpty()) {
                return true;
            }
        }
        return false;
    }
    
    private void applyMove(ArrayList<Integer> toToggle, int me) {
        if (me==1)
            myBB ^= 1l<<toToggle.get(0);
        else
            opponentBB ^= 1l<<toToggle.get(0);
        for (int i=1;i<toToggle.size();i++) {
            myBB ^= 1l<<toToggle.get(i);
            opponentBB ^= 1l<<toToggle.get(i);
        }
    }
    
    public String getMove(int me) {
        checkValidMoves(-1);
        ArrayList<Integer> moveSelected = validMoves.get(0);
        System.out.println("C Enter move: ");
        String moveString = keyboard.nextLine();
        boolean valid = true;
        //while(valid) {
            if (moveString.equals("W") || moveString.equals("B")) {
                return moveString;
            }
            int bit = move.moveToBit(moveString);
            int i;
            for(i=1;i<validMoves.size();i++) {
                if ((Integer)validMoves.get(i).get(0) == bit) {
                    moveSelected = validMoves.get(i);
                    break;
                }
            }
            /*if(i==validMoves.size()) {
                System.out.println("C Invalid Move.");
                System.out.println("C Enter move: ");
                moveString = keyboard.nextLine();
            } else
                break;*/
        //}
        applyMove(moveSelected,-1);
        return moveString;
    }    
    
    public String generateMove(int me) {
        checkValidMoves(1);
        if (validMoves.isEmpty()) {
            if (color == 1) return "B";
            else return "W";
        } else {
            ArrayList <Integer> largest = validMoves.get(0);
            applyMove(largest,1);

            String printMove = "";
            int bit = largest.get(0);
            if(color == 1){
                printMove += ("B ");
                printMove = printMove.concat(move.bitToMove(bit));
                return printMove;
            } else {
                printMove += ("W ");
                printMove = printMove.concat(move.bitToMove(bit));
                return printMove;
            }
        }
    }
    
    public String generateGreedy(int me) {
        checkValidMoves(1);
        if (validMoves.isEmpty()) {
            if (color == 1) return "B";
            else return "W";
        } else {
            ArrayList <Integer> largest = validMoves.get(0);
            int temp = validMoves.get(0).size();
            for(int i=1;i<validMoves.size();i++) {
                if (temp < validMoves.get(i).size())
                    largest = validMoves.get(i);
            }
            applyMove(largest,1);

            String printMove = "";
            int bit = largest.get(0);
            if(color == 1){
                printMove = printMove.concat("B ");
                printMove = printMove.concat(move.bitToMove(bit));
                return printMove;
            } else {
                printMove = printMove.concat("W ");
                printMove = printMove.concat(move.bitToMove(bit));
                return printMove;
            }
        }
    }
    
    private void checkValidMoves(int me) {
        validMoves.clear();
        occupiedBB = myBB | opponentBB;
        int bit;
        for(bit=0;bit<64;bit++) {
            if (me==1) {
                if(((occupiedBB>>>bit)&1)==0) {
                    findNearestNeighbor(me,bit);
                }
            } else {
                if(((occupiedBB>>>bit)&1)==0) {
                    findNearestNeighbor(me,bit);
                }
            }
        }
        Collections.shuffle(validMoves);
    }
    
    private void findNearestNeighbor(int me, int bit) {
        ArrayList<Integer> toToggle = new ArrayList<>();
        toToggle.add(bit);
        if(me==1) {
            if (N_EDGE.indexOf(bit)==-1) 
                if(((opponentBB>>>(bit-8))&1) !=0) 
                    toToggle = north(toToggle,me,bit);
            if (N_EDGE.indexOf(bit)==-1 && E_EDGE.indexOf(bit)==-1) 
                if(((opponentBB>>>(bit-7))&1) !=0) 
                    toToggle = northEast(toToggle,me,bit);
            if (E_EDGE.indexOf(bit)==-1) 
                if(((opponentBB>>>(bit+1))&1) !=0) 
                    toToggle = east(toToggle,me,bit);
            if (S_EDGE.indexOf(bit)==-1 && E_EDGE.indexOf(bit)==-1) 
                if(((opponentBB>>>(bit+9))&1) !=0) 
                    toToggle = southEast(toToggle,me,bit);
            if (S_EDGE.indexOf(bit)==-1) 
                if(((opponentBB>>>(bit+8))&1) !=0) 
                    toToggle = south(toToggle,me,bit);
            if (S_EDGE.indexOf(bit)==-1 && W_EDGE.indexOf(bit)==-1) 
                if(((opponentBB>>>(bit+7))&1) !=0) 
                    toToggle = southWest(toToggle,me,bit);
            if (W_EDGE.indexOf(bit)==-1) 
                if(((opponentBB>>>(bit-1))&1) !=0) 
                    toToggle = west(toToggle,me,bit);
            if (N_EDGE.indexOf(bit)==-1 && W_EDGE.indexOf(bit)==-1) 
                if(((opponentBB>>>(bit-9))&1) !=0) 
                    toToggle = northWest(toToggle,me,bit);
        } else {
            if (N_EDGE.indexOf(bit)==-1) 
                if(((myBB>>>(bit-8))&1) !=0) 
                    toToggle = north(toToggle,me,bit);
            if (N_EDGE.indexOf(bit)==-1 && E_EDGE.indexOf(bit)==-1) 
                if(((myBB>>>(bit-7))&1) !=0) 
                    toToggle = northEast(toToggle,me,bit);
            if (E_EDGE.indexOf(bit)==-1) 
                if(((myBB>>>(bit+1))&1) !=0) 
                    toToggle = east(toToggle,me,bit);
            if (S_EDGE.indexOf(bit)==-1 && E_EDGE.indexOf(bit)==-1) 
                if(((myBB>>>(bit+9))&1) !=0) 
                    toToggle = southEast(toToggle,me,bit);
            if (S_EDGE.indexOf(bit)==-1) 
                if(((myBB>>>(bit+8))&1) !=0) 
                    toToggle = south(toToggle,me,bit);
            if (S_EDGE.indexOf(bit)==-1 && W_EDGE.indexOf(bit)==-1) 
                if(((myBB>>>(bit+7))&1) !=0) 
                    toToggle = southWest(toToggle,me,bit);
            if (W_EDGE.indexOf(bit)==-1) 
                if(((myBB>>>(bit-1))&1) !=0) 
                    toToggle = west(toToggle,me,bit);
            if (N_EDGE.indexOf(bit)==-1 && W_EDGE.indexOf(bit)==-1) 
                if(((myBB>>>(bit-9))&1) !=0) 
                    toToggle = northWest(toToggle,me,bit);
        }
        if(toToggle.size()!= 1) {
            validMoves.add(toToggle);
        }
    }
    
    private ArrayList<Integer> north(ArrayList<Integer> toToggle, int player, int bit) {
        int count=0;
        if (player==1) {
            bit -= 8;
            while(!N_EDGE.contains(bit)) {
                if (((opponentBB>>>bit)&1)!=0) {
                    toToggle.add(bit);
                    count++;
                }
                else break;
                bit -= 8;
            }
            if(((myBB>>>bit)&1)!=1 && toToggle.size()>1) {
                toToggle.subList(toToggle.size()-count,toToggle.size()).clear();
            }
        }
        else {
            bit -= 8;
            while(!N_EDGE.contains(bit)) {
                if (((myBB>>>bit)&1)!=0) {
                    toToggle.add(bit);
                    count++;
                }
                else break;
                bit -= 8;
            }
            if(((opponentBB>>>bit)&1)!=1 && toToggle.size()>1) {
                toToggle.subList(toToggle.size()-count,toToggle.size()).clear();
            }
        }
        return toToggle;
    }
    
    private ArrayList<Integer> northEast(ArrayList<Integer> toToggle, int player, int bit) {
        int count = 0;
        if (player==1) {
            bit -= 7;
            while(!N_EDGE.contains(bit) && !E_EDGE.contains(bit)) {
                if (((opponentBB>>>bit)&1)!=0) {
                    toToggle.add(bit);
                    count++;
                }
                else break;
                bit -= 7;
            }
            if(((myBB>>>bit)&1)!=1 && toToggle.size()>1) {
                toToggle.subList(toToggle.size()-count,toToggle.size()).clear();
            }
        }
        if (player == -1) {
            bit -= 7;
            while(!N_EDGE.contains(bit) && !E_EDGE.contains(bit)) {
                if (((myBB>>>bit)&1)!=0) {
                    toToggle.add(bit);
                    count++;
                }
                else break;
                bit -= 7;
            }
            if(((opponentBB>>>bit)&1)!=1 && toToggle.size()>1) {
                toToggle.subList(toToggle.size()-count,toToggle.size()).clear();
            }
        }
        return toToggle;
    }
    
    private ArrayList<Integer> east(ArrayList<Integer> toToggle, int player, int bit) {
        int count = 0;
        if (player==1) {
            bit++;
            while(!E_EDGE.contains(bit)) {
                if (((opponentBB>>>bit)&1)!=0) {
                    toToggle.add(bit);
                    count++;
                }
                else break;
                bit++;
            }
            if(((myBB>>>bit)&1)!=1 && toToggle.size()>1) {
                toToggle.subList(toToggle.size()-count,toToggle.size()).clear();
            }
        }
        if (player == -1) {
            bit++;
            while(!E_EDGE.contains(bit)) {
                if (((opponentBB>>>bit)&1)!=0) break;
                if (((myBB>>>bit)&1)!=0) {
                    toToggle.add(bit);
                    count++;
                }
                else break;
                bit++;
            }
            if(((opponentBB>>>bit)&1)!=1 && toToggle.size()>1) {
                toToggle.subList(toToggle.size()-count,toToggle.size()).clear();
            }
        }
        return toToggle;
    }
    
    private ArrayList<Integer> southEast(ArrayList<Integer> toToggle, int player, int bit) {
        int count = 0;
        if (player==1) {
            bit += 9;
            while(!S_EDGE.contains(bit) && !E_EDGE.contains(bit)) {
                if (((opponentBB>>>bit)&1)!=0) {
                    toToggle.add(bit);
                    count++;
                }
                else break;
                bit += 9;
            }
            if(((myBB>>>bit)&1)!=1 && toToggle.size()>1) {
                toToggle.subList(toToggle.size()-count,toToggle.size()).clear();
            }
        }
        if (player == -1) {
            bit += 9;
            while(!S_EDGE.contains(bit) && !E_EDGE.contains(bit)) {
                if (((myBB>>>bit)&1)!=0) {
                    toToggle.add(bit);
                    count++;
                }
                else break;
                bit += 9;
            }
            if(((opponentBB>>>bit)&1)!=1 && toToggle.size()>1) {
                toToggle.subList(toToggle.size()-count,toToggle.size()).clear();
            }
        }
        return toToggle;
    }
    
    private ArrayList<Integer> south(ArrayList<Integer> toToggle, int player, int bit) {
        int count = 0;
        if (player==1) {
            bit += 8;
            while(!S_EDGE.contains(bit)) {
                if (((opponentBB>>>bit)&1)!=0) {
                    toToggle.add(bit);
                    count++;
                }
                else break;
                bit += 8;
            }
            if(((myBB>>>bit)&1)!=1 && toToggle.size()>1) {
                toToggle.subList(toToggle.size()-count,toToggle.size()).clear();
            }
        }
        if (player == -1) {
            bit += 8;
            while(!S_EDGE.contains(bit)) {
                if (((myBB>>>bit)&1)!=0) {
                    toToggle.add(bit);
                    count++;
                }
                else break;
                bit += 8;
            }
            if(((opponentBB>>>bit)&1)!=1 && toToggle.size()>1) {
                toToggle.subList(toToggle.size()-count,toToggle.size()).clear();
            }
        }
        return toToggle;
    }
    
    private ArrayList<Integer> southWest(ArrayList<Integer> toToggle, int player, int bit) {
        int count = 0;
        if (player==1) {
            bit += 7;
            while(!S_EDGE.contains(bit) && !W_EDGE.contains(bit)) {
                if (((opponentBB>>>bit)&1)!=0) {
                    toToggle.add(bit);
                    count++;
                }
                else break;
                bit += 7;
            }
            if(((myBB>>>bit)&1)!=1 && toToggle.size()>1) {
                toToggle.subList(toToggle.size()-count,toToggle.size()).clear();
            }
        }
        if (player == -1) {
            bit += 7;
            while(!S_EDGE.contains(bit) && !W_EDGE.contains(bit)) {
                if (((myBB>>>bit)&1)!=0) {
                    toToggle.add(bit);
                    count++;
                }
                else break;
                bit += 7;
            }
            if(((opponentBB>>>bit)&1)!=1 && toToggle.size()>1) {
                toToggle.subList(toToggle.size()-count,toToggle.size()).clear();
            }
        }
        return toToggle;
    }
    
    private ArrayList<Integer> west(ArrayList<Integer> toToggle, int player, int bit) {
        int count = 0;
        if (player==1) {
            bit--;
            while(!W_EDGE.contains(bit)) {
                if (((opponentBB>>>bit)&1)!=0) {
                    toToggle.add(bit);
                    count++;
                }
                else break;
                bit--;
            }
            if(((myBB>>>bit)&1)!=1 && toToggle.size()>1) {
                toToggle.subList(toToggle.size()-count,toToggle.size()).clear();
            }
        }
        if (player == -1) {
            bit--;
            while(!W_EDGE.contains(bit)) {
                if (((myBB>>>bit)&1)!=0) {
                    toToggle.add(bit);
                    count++;
                }
                else break;
                bit--;
            }
            if(((opponentBB>>>bit)&1)!=1 && toToggle.size()>1) {
                toToggle.subList(toToggle.size()-count,toToggle.size()).clear();
            }
        }
        return toToggle;
    }
    
    private ArrayList<Integer> northWest(ArrayList<Integer> toToggle, int player, int bit) {
        int count = 0;
        if (player==1) {
            bit -= 9;
            while(!N_EDGE.contains(bit) && !W_EDGE.contains(bit)) {
                if (((opponentBB>>>bit)&1)!=0) {
                    toToggle.add(bit);
                    count++;
                }
                else break;
                bit -= 9;
            }
            if(((myBB>>>bit)&1)!=1 && toToggle.size()>1) {
                toToggle.subList(toToggle.size()-count,toToggle.size()).clear();
            }
        }
        if (player == -1) {
            bit -= 9;
            while(!S_EDGE.contains(bit) && !W_EDGE.contains(bit)) {
                if (((myBB>>>bit)&1)!=0) {
                    toToggle.add(bit);
                    count++;
                }
                else break;
                bit -= 9;
            }
            if(((opponentBB>>>bit)&1)!=1 && toToggle.size()>1) {
                toToggle.subList(toToggle.size()-count,toToggle.size()).clear();
            }
        }
        return toToggle;
    }
    
    
    @Override
    public String toString() {
        int i;
        int bit = 0;
        String board = "C ";
        for (i=0;i<16;i++) {
            board = board.concat("**");
        }
        board = board.concat("\nC     ");
        
        for (char ch='a';ch<='h';ch++) {
            board = board.concat("|" + ch + "|");
        } board = board.concat("\nC     ");
        for (i=0;i<12;i++) {
            board = board.concat("==");
        } board = board.concat("\n");
        for (i=1;i<=8;i++) {
            board = board.concat("C |"+i+"| ");
            for (int j=1;j<=8;j++) {
                if(color==1) {
                    if (((myBB >>> bit) & 1) != 0) {
                        board = board.concat(" B ");
                    } else if (((opponentBB >>> bit) & 1) != 0) {
                        board = board.concat(" W ");
                    } else
                        board = board.concat(" - ");
                } else {
                    if (((myBB >>> bit) & 1) != 0) {
                        board = board.concat(" W ");
                    } else if (((opponentBB >>> bit) & 1) != 0) {
                        board = board.concat(" B ");
                    } else
                        board = board.concat(" - ");
                }
                bit++;
            }
            board = board.concat("\n");
        } board = board.concat("C     ");
        for (i=0;i<12;i++) {
            board = board.concat("==");
        }  board += "\n";
        if (color==1) {
            board = board.concat("C     Black: " + Integer.toString(Long.bitCount(myBB)) + " ---- ");
            board = board.concat("White: " + Integer.toString(Long.bitCount(opponentBB)));
        } else {
            board = board.concat("C     Black: " + Integer.toString(Long.bitCount(opponentBB)) + " ---- ");
            board = board.concat("White: " + Integer.toString(Long.bitCount(myBB)));
           
        } board = board.concat("\nC ");
        for (i=0;i<16;i++) {
            board = board.concat("**");
        } board += "\nC";
        
        
        return board;
    }
}
