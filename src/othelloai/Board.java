package othelloai;

import java.util.ArrayList;
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
    public ArrayList<Move> validMoves;
    public final int [] index = {1,7,8,9};
    
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
    }
    
    public Board (Board oldBoard) {
        boardToEvaluate = oldBoard;
    }
    
    public boolean isOver(int me) {
        if(me==-1) {
            return false;
        } else {
            checkValidMoves(1);
            if (validMoves.isEmpty()) {
                checkValidMoves(-1);
                if (validMoves.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void applyMove(int player, Move valid) {
        if(player==1) myBB ^= 1l<<valid.bitIndex;
        else opponentBB ^= 1l<<valid.bitIndex;
        myBB ^= valid.toggle;
        opponentBB ^= valid.toggle;
    }
    
    public void checkValidMoves(int player) {
        validMoves.clear();
        Long empty = ~(myBB | opponentBB);
        Long me, opp;
        if(player == 1) {
            me = myBB;
            opp = opponentBB;
        } else {
            me = opponentBB;
            opp = myBB;
        }
        Long totalMoves = 0l;
        
        for (int i=0;i<4;i++) {
            
            Long t = opp & (me << index[i]);
            t |= opp & (t << index[i]);
            t |= opp & (t << index[i]);
            t |= opp & (t << index[i]);
            t |= opp & (t << index[i]);
            t |= opp & (t << index[i]);
            totalMoves |= (empty & (t << index[i]));
        }
        for(int i=0;i<4;i++) {
            Long t = opp & (me >> index[i]);
            t |= opp & (t >> index[i]);
            t |= opp & (t >> index[i]);
            t |= opp & (t >> index[i]);
            t |= opp & (t >> index[i]);
            t |= opp & (t >> index[i]);
            totalMoves |= (empty & (t >> index[i]));
        }
        
        Move valid;
        Long toToggle=0l;
        for (int bit=0;bit<64;bit++) {
            if(((totalMoves>>>bit)&1)==1) {
                toToggle = toToggle(bit,player);
                valid = new Move(bit, toToggle);
                validMoves.add(valid);
                //System.out.println(bit);
            }
        }
        
        Collections.shuffle(validMoves);
    }
    
    public Long toToggle(int bit, int player) {
        Long me, opp;
        if(player == 1) {
            me = myBB;
            opp = opponentBB;
        } else {
            me = opponentBB;
            opp = myBB;
        }
        Long bitBB = 1l<<bit;
        Long toToggle = 0l;
        for (int i=0;i<4;i++) {
            Long t = opp & (bitBB << index[i]);
            t |= opp & (t << index[i]);
            t |= opp & (t << index[i]);
            t |= opp & (t << index[i]);
            t |= opp & (t << index[i]);
            t |= opp & (t << index[i]);
           
            if(Long.bitCount(me&t<<index[i])>0) {
                toToggle|= t;
            }
        }
        for(int i=0;i<4;i++) {
            Long t = opp & (bitBB >> index[i]);
            t |= opp & (t >> index[i]);
            t |= opp & (t >> index[i]);
            t |= opp & (t >> index[i]);
            t |= opp & (t >> index[i]);
            t |= opp & (t >> index[i]);
            if(Long.bitCount(me&t>>index[i])>0) {
                toToggle|= t;
            }
        }
        //toToggle|=bitBB;
        return toToggle;
    }
    public String generateMove(int player) {
        checkValidMoves(1);
        if (validMoves.isEmpty()) {
            if (color == 1) return "B";
            else return "W";
        } else {
            applyMove(player,validMoves.get(0));

            String printMove = "";
            if(color == 1){
                printMove += ("B ");
                printMove += (validMoves.get(0).toString());
                return printMove;
            } else {
                printMove += ("W ");
                printMove += (validMoves.get(0).toString());
                return printMove;
            }
        }
    }
    
    public String getMove(int me) {
        checkValidMoves(-1);
        Move attempt = new Move();
        Move check = new Move();
        if(validMoves.isEmpty()) {
            
        } else check = validMoves.get(0);
        System.out.println("C Enter move: ");
        String moveString = keyboard.nextLine();
        boolean valid = true;
        while(valid) {
            if (moveString.equals("W") || moveString.equals("B")) {
                return moveString;
            }
            else attempt = new Move(moveString);
            int i;
            for(i=0;i<validMoves.size();i++) {
                if (attempt.bitIndex == validMoves.get(i).bitIndex) {
                    check = validMoves.get(i);
                    break;
                }
            }
            if(i==validMoves.size()) {
                System.out.println("C Invalid Move.");
                System.out.println("C Enter move: ");
                moveString = keyboard.nextLine();
            } else
                break;
        }
        applyMove(-1,check);
        return moveString;
    }    
    
    
    /*private void applyMove(ArrayList<Integer> toToggle, int me) {
        if (me==1)
            myBB ^= 1l<<toToggle.get(0);
        else
            opponentBB ^= 1l<<toToggle.get(0);
        for (int i=1;i<toToggle.size();i++) {
            myBB ^= 1l<<toToggle.get(i);
            opponentBB ^= 1l<<toToggle.get(i);
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
    }*/
    
    
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
