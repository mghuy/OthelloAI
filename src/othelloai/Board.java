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
    public int moveNumber;
    public int color; //1 == I B and -1 == I W
    public Boolean isOver;
    public ArrayList<Move> validMoves;
    public final int [] index = {1,7,8,9};
    public final Long [] overflow = {
        0xFEFEFEFEFEFEFEFEl, //right
        0x7F7F7F7F7F7F7F00l, //downLeft
        0xFFFFFFFFFFFFFF00l, //down
        0xFEFEFEFEFEFEFE00l, //downRight
        0x7F7F7F7F7F7F7F7Fl, //left
        0x00FEFEFEFEFEFEFEl, //upRight
        0x00FFFFFFFFFFFFFFl, //up
        0x007F7F7F7F7F7F7Fl, //upLeft
    };
    public final Long [] overflow2 = {
        0x7F7F7F7F7F7F7F7Fl,    //left
        0xFEFEFEFEFEFEFE00l,    //downRight
        0xFFFFFFFFFFFFFF00l,
        0x7F7F7F7F7F7F7F00l,
        0xFEFEFEFEFEFEFEFEl,
        0x007F7F7F7F7F7F7Fl,
        0x00FFFFFFFFFFFFFFl,
        0x00FEFEFEFEFEFEFEl,
    };
    public Scanner keyboard;
    public Random rand;
    
    private final int MAX_DEPTH = 4;
    private Board boardToEvaluate;
    
    public Board (int color) {
        keyboard = new Scanner(System.in);
        moveNumber = -1;
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
        moveNumber++;
        if(me==-1) return false;
        else {
            checkValidMoves(1);
            if (validMoves.isEmpty()) {
                checkValidMoves(-1);
                if (validMoves.isEmpty()) return true;
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
        Long temp;
        int j=0,k;
        
        for (int i=0;i<4;i++) {
            temp = opp & overflow[j] & overflow2[j];
            Long t = temp & (me >>> index[i]);
            for(k=0;k<5;k++) {
                t |= temp & (t>>>index[i]);
            }
            totalMoves |= (empty & (t >>> index[i]));
            j++;
        }
        for(int i=0;i<4;i++) {
            temp = opp & overflow[j] & overflow2[j];
            Long t = temp & (me << index[i]);
            for(k=0;k<5;k++) {
                t |= temp & (t << index[i]);
            }
            totalMoves |= (empty & (t << index[i]));
            j++;
        }
        
        Move valid;
        Long toToggle;
        for (int bit=0;bit<64;bit++) {
            if(((totalMoves>>>bit)&1)==1) {
                toToggle = toToggle(bit,player);
                valid = new Move(bit, toToggle);
                validMoves.add(valid);
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
        Long bitBB = 1l<<bit; //System.out.println(Long.toBinaryString(bitBB));
        Long toToggle = 0l;
        Long temp;
        Long checkDir;
        int j = 0;
        
        for (int i=0;i<4;i++) {
            temp = opp & overflow[j] & overflow2[j];
            Long t = temp & (bitBB>>>index[i]);
            checkDir = 0l;
            for(int k=0;k<6;k++) {
                if (t!=0l) {
                    checkDir |= t; 
                    if((t>>>index[i] & me) != 0l) {
                        toToggle |= checkDir;
                        break;
                    }
                }
                t = temp & (t>>>index[i]);
            }
            j++;
        }
        for(int i=0;i<4;i++) {
            temp = opp & overflow[j] & overflow2[j];
            //bitBB &= overflow[j];
            Long t = temp & (bitBB << index[i]);
            checkDir = 0l;
            for(int k=0;k<6;k++) {
                //System.out.println("Found enemy");
                if(t!=0l) {
                    checkDir |= t;
                    if((t<<index[i]&me)!=0l) {
                        toToggle|=checkDir;
                        break;
                    }
                }
                t = temp & (t << index[i]);
            }
            j++;
        }
        
        /*System.out.print("C " + bit + " : [ ");
        for(int i=0;i<64;i++) {
            if(((toToggle>>>i)&1)==1) {
                System.out.print(i + ", ");
            }
        }
        System.out.println("]");*/
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
        Move attempt;
        Move check = new Move();
        if(!validMoves.isEmpty()) 
            check = validMoves.get(0);
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
                    check = validMoves.get(i); break;
                }
            }
            if(i == validMoves.size()) {
                System.out.println("C Invalid Move.");
                System.out.println("C Enter move: ");
                moveString = keyboard.nextLine();
            } else
                valid = false;
        }
        applyMove(-1,check);
        return moveString;
    }    
    
    @Override
    public String toString() {
        int i;
        int bit = 0;
        String board = "C ";
        for (i=0;i<16;i++) {
            board += ("**");
        }
        board += ("\nC     ");
        
        for (char ch='a';ch<='h';ch++) {
            board += ("|" + ch + "|");
        } board += ("\nC     ");
        for (i=0;i<12;i++) {
            board += ("==");
        } board += ("\n");
        for (i=1;i<=8;i++) {
            board += ("C |" + i + "| ");
            for (int j=1;j<=8;j++) {
                if(color == 1) {
                    if (((myBB>>>bit) & 1) != 0) {
                        board += (" B ");
                    } else if (((opponentBB>>>bit) & 1) != 0) {
                        board += (" W ");
                    } else
                        board += (" - ");
                } else {
                    if (((myBB>>>bit) & 1) != 0) {
                        board += (" W ");
                    } else if (((opponentBB>>>bit) & 1) != 0) {
                        board += (" B ");
                    } else
                        board += (" - ");
                }
                bit++;
            }
            board += ("\n");
        } board += ("C     ");
        for (i=0;i<12;i++) {
            board += ("==");
        }  board += "\n";
        if (color==1) {
            board += ("C     Black: " + Integer.toString(Long.bitCount(myBB)) + " ---- ");
            board += ("White: " + Integer.toString(Long.bitCount(opponentBB)));
        } else {
            board += ("C     Black: " + Integer.toString(Long.bitCount(opponentBB)) + " ---- ");
            board += ("White: " + Integer.toString(Long.bitCount(myBB)));
           
        } board += ("\nC ");
        for (i=0;i<16;i++) {
            board = board.concat("**");
        } 
        
        
        return board;
    }
}
