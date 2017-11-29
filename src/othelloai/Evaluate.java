package othelloai;

/**
 *
 * @author Leng Ghuy
 * @assignemt Othello AI
 * @class CSCI 312 - Intro to Artificial Intelligence
 * @version December 1, 2017
 * 
 * A class containing all the methods of each evaluation feature that determines 
 *      the "goodness" of the board that is given
 */
public class Evaluate {
    
    //------------ Global Variables ------------

    public Board board;
    public double greedy;
    public double cornerCaptured;
    public double avoidX;
    public double mobility;
    public double potentialMobility;
    public double secured;
    public double wedgeAlert;
    
    public Long[] wedge = {//north wedge
                           0x000000000000000Al,0x0000000000000014l,
                           0x0000000000000028l,0x0000000000000050l,
                           //west wedge
                           0x0000000001000100l,0x0000000100010000l,
                           0x0000010001000000l,0x0001000100000000l,
                           //east wedge
                           0x0000000080008000l,0x0000008000800000l,
                           0x0000800080000000l,0x0080008000000000l,
                           //south wedge
                           0x0A00000000000000l,0x1400000000000000l,
                           0x2800000000000000l,0x5000000000000000l,
                          };
    public Long [] stonerWarning = {0x0000000000000005l,0x00000000000000A0l,
                                    0x0000000000010001l,0x0100010000000000l,
                                    0x0000000000800080l,0x8000800000000000l,
                                    0x0500000000000000l,0xA000000000000000l
    };
    public Long [] stoner = {0x0000000000000078l,0x000000000000001El,
                             0x0001010101000000l,0x0000000101010100l,
                             0x0080808080000000l,0x0000008080808000l,
                             0x7800000000000000l,0x1E00000000000000l
    };
    
    public Long[] corner = {0x0000000000000001l, //upper-left corner
                            0x0000000000000080l, //upper-right corner
                            0x0100000000000000l, //lower-left corner
                            0x8000000000000000l, //lower-right corner
                           };
    public Long[] xCorner = {0x0000000000000200l, //upper-left Xsquare
                             0x0000000000004000l, //upper-right Xsquare
                             0x0002000000000000l, //lower-left Xsquare
                             0x0040000000000000l, //lower-right Xsquare
                            };
    public Long[] fourCorner = {0x0000000000040000l, //upper-left four-square corner
                                0x0000000000200000l, //upper-right four-square corner
                                0x0000040000000000l, //lower-left four-square corner
                                0x0000200000000000l, //lower-right four-square corner
                               };

    public Long[] center = {0x0000000008000000l, //d-4
                            0x0000000010000000l, //e-4
                            0x0000000800000000l, //d-5
                            0x0000001000000000l, //e-5
                           };

    // --- Northern Secured ---
    public Long[] securedN = {0x0000000000000002l,
                              0x0000000000000004l,
                              0x0000000000000008l,
                              0x0000000000000010l,
                              0x0000000000000020l,
                              0x0000000000000040l};
    public Long[] inSecuredN = {0x0000000000000400l,
                                0x0000000000000800l,
                                0x0000000000001000l,
                                0x0000000000002000l};
    public Long[] innerSecuredN = {0x0000000000080000l,
                                   0x0000000000100000l};

    //--- Western Secured ---
    public Long[] securedW = {0x0000000000000100l,
                              0x0000000000010000l,
                              0x0000000001000000l,
                              0x0000000100000000l,
                              0x0000010000000000l,
                              0x0001000000000000l};
    public Long[] inSecuredW = {0x0000000000020000l,
                                0x0000000002000000l,
                                0x0000000200000000l,
                                0x0000020000000000l};
    public Long[] innerSecuredW = {0x0000000004000000l,0x0000000400000000l};

    //--- Eastern Secured ---
    public Long[] securedE = {0x0000000000008000l,
                              0x0000000000800000l,
                              0x0000000080000000l,
                              0x0000008000000000l,
                              0x0000800000000000l,
                              0x0080000000000000l};
    public Long[] inSecuredE = {0x0000000000400000l,
                                0x0000000040000000l,
                                0x0000004000000000l,
                                0x0000400000000000l};
    public Long[] innerSecuredE = {0x0000000020000000l, 0x0000002000000000l};

    //--- Southern Secured ---
    public Long[] securedS = {0x0200000000000000l,
                              0x0400000000000000l,
                              0x0800000000000000l,
                              0x0100000000000000l,
                              0x0200000000000000l,
                              0x0400000000000000l};
    public Long[] inSecuredS = {0x0004000000000000l,
                                0x0008000000000000l,
                                0x0010000000000000l,
                                0x0020000000000000l};
    public Long[] innerSecuredS = {0x0000080000000000l, 0x0000100000000000l};

    //------------ Constructor ------------
    
    /**
     * @param board the board state to evaluate
     *
     * Constructor method to initializes the board to evaluate
     */
    public Evaluate(Board board) {
        this.board = board;
    }//constructor

    //------------ Class Methods ------------
    
    /**
     * @return the value for the move
     * 
     * Evaluation function for the agent
     */
    public Double evaluate() {
        
        double doubleValue = 0;

        //--- Check end-game status --- also implements mobility feature
        int myCount = board.checkValidMoves(1).size();
        int oppCount = board.checkValidMoves(-1).size();
        int total = myCount + oppCount;
        mobility = 0;
        if (total != 0) {
            mobility = (myCount - oppCount) / (double) total;
        }
        greedy = greedy();

        if (total == 0) {
            double result = greedy * 1000;
            return result;
        }
        //---- *************************************** ----

        //--- Evaluate ---
        cornerCaptured = cornerCaptured();
        wedgeAlert = wedge();
        
        if (board.moveNumber < 47) {
            avoidX = avoidX();
            potentialMobility = potentialMobility();
            secured = cornerCaptured!=0 ? securedDisk() : 0;

            doubleValue = 5*greedy + 65*cornerCaptured + 30*avoidX + 55*mobility + 
                        30*potentialMobility + 85*secured + 30*wedgeAlert;
        } 
        else 
            doubleValue = 45*greedy + 60*cornerCaptured + 135*mobility + 60*wedgeAlert;
        
        return doubleValue;
    }//evaluate

    /**
     * @return the normalized value of my net discs vs. opp net discs
     */
    public double greedy() {
        int myCount = Long.bitCount(board.myBB);
        int oppCount = (Long.bitCount(board.opponentBB));
        return (myCount - oppCount) / ((double) (myCount + oppCount));
    }//greedy

    /**
     * @return the normalized value of total corners captured of my discs vs. opp discs
     */
    public double cornerCaptured() {
        Long corners = 0x8100000000000081l;
        int myCount = Long.bitCount(board.myBB & corners);
        int oppCount = Long.bitCount(board.opponentBB & corners);
        return (myCount - oppCount) / 4.0;
    }

    /**
     * @return the normalized value of discs in x-squares of opp discs vs. mine
     */
    public double avoidX() {
        Long xSquares = 0x0042000000004200l;
        int myCount = Long.bitCount(board.myBB & xSquares);
        int oppCount = Long.bitCount(board.opponentBB & xSquares);
        return (oppCount - myCount) / 4.0;
    }

    /**
     * @return the normalized value of my frontier discs vs. my opp frontier discs 
     */
    public double potentialMobility() {
        int myCount = 0;
        int oppCount = 0;
        Long empty = ~(board.myBB | board.opponentBB);
        Long temp = 0l;
        int j = 0;

        //shift once in every direction and incrememt count for every shift that
        //lands on an empty square
        for (int i = 0; i < 4; i++) {
            temp = empty & board.overflow[j];   //boreder buffer
            Long myFrontier = temp & (board.myBB >>> board.index[i]);
            myCount += Long.bitCount(empty & myFrontier);
            Long oppFrontier = temp & (board.opponentBB >>> board.index[i]);
            oppCount += Long.bitCount(empty & oppFrontier);
            j++;
        }
        for (int i = 0; i < 4; i++) {
            temp = empty & board.overflow[j];   //border buffer
            Long myFrontier = temp & (board.myBB << board.index[i]);
            myCount += Long.bitCount(empty & myFrontier);
            Long oppFrontier = temp & (board.opponentBB << board.index[i]);
            oppCount += Long.bitCount(empty & oppFrontier);
            j++;
        }

        double total = myCount + oppCount;
        if (total != 0) {
            return (oppCount - myCount) / total;
        }
        return 0.0;
    }
    
    /**
     * @return the normalized value of my secured discs vs opp secured discs
     */
    public double securedDisk() {
        int myCount = mySecuredDisk();
        int oppCount = oppSecuredDisk();
        double total=myCount+oppCount;
        if(total!=0) {
            return (myCount-oppCount)/total;
        }
        return 0.0;
    }

    /**
     * @return total count of my secured discs
     */
    public int mySecuredDisk() {
        int myCount = 0;
        int edgeN=0, edgeNinv, inEdgeN=0, inEdgeNinv, innerEdgeN=0, innerEdgeNinv;
        int edgeE=0, edgeEinv, inEdgeE=0, inEdgeEinv, innerEdgeE=0, innerEdgeEinv;
        int edgeW=0, edgeWinv, inEdgeW=0, inEdgeWinv, innerEdgeW=0, innerEdgeWinv;
        int edgeS=0, edgeSinv, inEdgeS=0, inEdgeSinv, innerEdgeS=0, innerEdgeSinv;
 
        //--- Check MY-BitBoard from upper-left corner
        if ((board.myBB & corner[0]) != 0) {
            myCount++;
            for (edgeN=0; edgeN<securedN.length; edgeN++) {
                if ((board.myBB & securedN[edgeN]) == 0) break; 
                else myCount++;
            }//checks northern border
            for (edgeW=0; edgeW<securedW.length; edgeW++) {
                if ((board.myBB & securedW[edgeW]) == 0) break;
                else myCount++;
            }
            if(edgeN>=2 && edgeW>=2) {  //check from b-2
                if((board.myBB & xCorner[0]) != 0) {
                    myCount++;
                    for(inEdgeN=0; inEdgeN<edgeN-2; inEdgeN++) {
                        if((board.myBB&inSecuredN[inEdgeN]) == 0) break;
                        else myCount++;
                    }
                    for(inEdgeW=0; inEdgeW<edgeW-2; inEdgeW++) {
                        if((board.myBB&inSecuredW[inEdgeW]) == 0) break;
                        else myCount++;
                    }
                    if(inEdgeN>=2 && inEdgeW>=2) {
                        if((board.myBB & fourCorner[0])!= 0) {  //check from c-3
                            myCount++;
                            for(innerEdgeN=0;innerEdgeN<inEdgeN-2;innerEdgeN++) {
                                if((board.myBB&innerSecuredN[innerEdgeN]) == 0) break;
                                else myCount++;
                            }
                            for(innerEdgeW=0;innerEdgeW<inEdgeW-2;innerEdgeW++) {
                                if((board.myBB&innerSecuredW[innerEdgeW]) == 0) break;
                                else myCount++;
                            }
                            if(innerEdgeW==2 && innerEdgeW==2)
                                if((board.myBB & center[0]) != 0) myCount++;
                        }
                    }
                }
            }
        }//checking from a-1 corner
        
        //--- Check from upper-right corner
        if((board.myBB & corner[1]) != 0) {
            myCount++;
            for(edgeNinv=securedN.length-1;edgeNinv>=edgeN;edgeNinv--) {
                if((board.myBB&securedN[edgeNinv]) == 0) break;
                else myCount++;
            }
            for(edgeE=0; edgeE<securedE.length; edgeE++) {
                if((board.myBB&securedE[edgeE]) == 0) break;
                else myCount++;
            }
            if((edgeNinv<=3 && edgeE>=2) || (edgeN==6 && edgeE>=2)) {
                if((board.myBB & xCorner[1]) != 0) {
                    myCount++;
                    for(inEdgeNinv=inSecuredN.length-1;inEdgeNinv>=inEdgeN;inEdgeNinv--) {
                        if((board.myBB&inSecuredN[inEdgeNinv]) == 0) break;
                        else myCount++;
                    }
                    for(inEdgeE=0; inEdgeE<edgeE-2; inEdgeE++) {
                        if((board.myBB&inSecuredE[inEdgeE]) == 0) break;
                        else myCount++;
                    }
                    if((inEdgeNinv<=1 && inEdgeE>=2) || (inEdgeN==4 && inEdgeE>=2)) {
                        if((board.myBB & fourCorner[1]) != 0) {
                            myCount++;
                            for(innerEdgeNinv=innerSecuredN.length-1;innerEdgeNinv>=innerEdgeN;innerEdgeNinv--) {
                                if((board.myBB&innerSecuredN[innerEdgeNinv]) == 0) break;
                                else myCount++;
                            }
                            for(innerEdgeE=0;innerEdgeE<inEdgeE-2;innerEdgeE++) {
                                if((board.myBB&innerSecuredE[innerEdgeE]) == 0) break;
                                else myCount++;
                            }
                            if((innerEdgeNinv==-1 && innerEdgeE==2) || (innerEdgeN==2 && innerEdgeE==2))
                                if((board.myBB & center[1]) != 0)
                                    myCount++;
                        }
                    }
                }
            }
        }//checking from h-1 corner
        
        //--- Checking from lower-left corner
        if((board.myBB & corner[2]) != 0) {
            myCount++;
            for(edgeWinv=securedW.length-1;edgeWinv>=edgeW;edgeWinv--) {
                if((board.myBB&securedW[edgeWinv]) == 0) break;
                else myCount++;
            }
            for(edgeS=0; edgeS<securedS.length; edgeS++) {
                if((board.myBB&securedS[edgeS]) == 0) break;
                else myCount++;
            }
            if((edgeWinv<=3 && edgeS>=2) || (edgeW==6 && edgeS>=2)) {
                if((board.myBB & xCorner[2]) != 0) {
                    myCount++;
                    for(inEdgeWinv=inSecuredW.length-1;inEdgeWinv>=inEdgeW;inEdgeWinv--) {
                        if((board.myBB&inSecuredW[inEdgeWinv]) == 0) break;
                        else myCount++;
                    }
                    for(inEdgeS=0; inEdgeS<edgeS-2; inEdgeS++) {
                        if((board.myBB&inSecuredS[inEdgeS]) == 0) break;
                        else myCount++;
                    }
                    if((inEdgeWinv<=1 && inEdgeS>=2) || (inEdgeW==4 && inEdgeS>=2)) {
                        if((board.myBB & fourCorner[2]) != 0) {
                            myCount++;
                            for(innerEdgeWinv=innerSecuredW.length-1;innerEdgeWinv>=innerEdgeW;innerEdgeWinv--) {
                                if((board.myBB&innerSecuredW[innerEdgeWinv]) == 0) break;
                                else myCount++;
                            }
                            for(innerEdgeS=0;innerEdgeS<inEdgeS-2;innerEdgeS++) {
                                if((board.myBB&innerSecuredS[innerEdgeS]) == 0) break;
                                else myCount++;
                            }
                            if((innerEdgeWinv==-1 && innerEdgeS==2) || (innerEdgeW==2 && innerEdgeS==2))
                                if((board.myBB & center[2]) != 0)
                                    myCount++;
                        }
                    }
                }
            }
        }//checking from a-8 corner
        
        //--- Check from lower-right corner
        if((board.myBB & corner[3]) != 0) {
            myCount++;
            for(edgeSinv=securedS.length-1;edgeSinv>edgeS;edgeSinv--) {
                if((board.myBB&securedS[edgeSinv]) == 0) break;
                else myCount++;
            }
            for(edgeEinv=securedE.length-1; edgeEinv>edgeE; edgeEinv--) {
                if((board.myBB&securedE[edgeEinv]) == 0) break;
                else myCount++;
            }
            if((edgeSinv<=3 && edgeEinv<=3) || (edgeS==6 && edgeE==6)
                    || (edgeSinv<=3 && edgeE==6) || (edgeEinv<=3 && edgeS==6)) {
                if((board.myBB & xCorner[3]) != 0) {
                    myCount++;
                    for(inEdgeSinv=inSecuredS.length-1;inEdgeSinv>=inEdgeS;inEdgeSinv--) {
                        if((board.myBB&inSecuredS[inEdgeSinv]) == 0) break;
                        else myCount++;
                    }
                    for(inEdgeEinv=inSecuredE.length-1; inEdgeEinv>=inEdgeE; inEdgeEinv--) {
                        if((board.myBB&inSecuredE[inEdgeEinv]) == 0) break;
                        else myCount++;
                    }
                    if((inEdgeSinv<=1 && inEdgeEinv<=1) || (inEdgeS==4 && inEdgeE==4)
                            || (inEdgeSinv<=1 && inEdgeE==4) || (inEdgeEinv<=1 && inEdgeS==4)) {
                        if((board.myBB & fourCorner[3]) != 0) {
                            myCount++;
                            for(innerEdgeSinv=innerSecuredS.length-1;innerEdgeSinv>=innerEdgeS;innerEdgeSinv--) {
                                if((board.myBB&innerSecuredS[innerEdgeSinv]) == 0) break;
                                else myCount++;
                            }
                            for(innerEdgeEinv=innerSecuredE.length-1;innerEdgeEinv>=innerEdgeE;innerEdgeEinv--) {
                                if((board.myBB&innerSecuredE[innerEdgeEinv]) == 0) break;
                                else myCount++;
                            }
                            if((innerEdgeSinv==-1 && innerEdgeEinv==-1) || (innerEdgeS==2 && innerEdgeE==-1)
                                    || (innerEdgeSinv==-1 && innerEdgeE==2) || (innerEdgeEinv==-1 && innerEdgeS==2))
                                if((board.myBB & center[1]) != 0)
                                    myCount++;
                        }
                    }
                }
            }
        }//checking from h-8 corner
        
        return myCount;
    }
    
    /**
     * @return the total count of opponent's secured discs
     */
    public int oppSecuredDisk() {
        int oppCount = 0;
        int edgeN=0, edgeNinv, inEdgeN=0, inEdgeNinv, innerEdgeN=0, innerEdgeNinv;
        int edgeE=0, edgeEinv, inEdgeE=0, inEdgeEinv, innerEdgeE=0, innerEdgeEinv;
        int edgeW=0, edgeWinv, inEdgeW=0, inEdgeWinv, innerEdgeW=0, innerEdgeWinv;
        int edgeS=0, edgeSinv, inEdgeS=0, inEdgeSinv, innerEdgeS=0, innerEdgeSinv;
 
        //--- Check MY-BitBoard from upper-left corner
        if ((board.opponentBB & corner[0]) != 0) {
            oppCount++;
            for (edgeN=0; edgeN<securedN.length; edgeN++) {
                if ((board.opponentBB & securedN[edgeN]) == 0) break; 
                else oppCount++;
            }//checks northern border
            for (edgeW=0; edgeW<securedW.length; edgeW++) {
                if ((board.opponentBB & securedW[edgeW]) == 0) break;
                else oppCount++;
            }
            if(edgeN>=2 && edgeW>=2) {  //check from b-2
                if((board.opponentBB & xCorner[0]) != 0) {
                    oppCount++;
                    for(inEdgeN=0; inEdgeN<edgeN-2; inEdgeN++) {
                        if((board.opponentBB&inSecuredN[inEdgeN]) == 0) break;
                        else oppCount++;
                    }
                    for(inEdgeW=0; inEdgeW<edgeW-2; inEdgeW++) {
                        if((board.opponentBB&inSecuredW[inEdgeW]) == 0) break;
                        else oppCount++;
                    }
                    if(inEdgeN>=2 && inEdgeW>=2) {
                        if((board.opponentBB & fourCorner[0])!= 0) {  //check from c-3
                            oppCount++;
                            for(innerEdgeN=0;innerEdgeN<inEdgeN-2;innerEdgeN++) {
                                if((board.opponentBB&innerSecuredN[innerEdgeN]) == 0) break;
                                else oppCount++;
                            }
                            for(innerEdgeW=0;innerEdgeW<inEdgeW-2;innerEdgeW++) {
                                if((board.opponentBB&innerSecuredW[innerEdgeW]) == 0) break;
                                else oppCount++;
                            }
                            if(innerEdgeW==2 && innerEdgeW==2)
                                if((board.opponentBB & center[0]) != 0) oppCount++;
                        }
                    }
                }
            }
        }//checking from a-1 corner
        
        //--- Check from upper-right corner
        if((board.opponentBB & corner[1]) != 0) {
            oppCount++;
            for(edgeNinv=securedN.length-1;edgeNinv>=edgeN;edgeNinv--) {
                if((board.opponentBB&securedN[edgeNinv]) == 0) break;
                else oppCount++;
            }
            for(edgeE=0; edgeE<securedE.length; edgeE++) {
                if((board.opponentBB&securedE[edgeE]) == 0) break;
                else oppCount++;
            }
            if((edgeNinv<=3 && edgeE>=2) || (edgeN==6 && edgeE>=2)) {
                if((board.opponentBB & xCorner[1]) != 0) {
                    oppCount++;
                    for(inEdgeNinv=inSecuredN.length-1;inEdgeNinv>=inEdgeN;inEdgeNinv--) {
                        if((board.opponentBB&inSecuredN[inEdgeNinv]) == 0) break;
                        else oppCount++;
                    }
                    for(inEdgeE=0; inEdgeE<edgeE-2; inEdgeE++) {
                        if((board.opponentBB&inSecuredE[inEdgeE]) == 0) break;
                        else oppCount++;
                    }
                    if((inEdgeNinv<=1 && inEdgeE>=2) || (inEdgeN==4 && inEdgeE>=2)) {
                        if((board.opponentBB & fourCorner[1]) != 0) {
                            oppCount++;
                            for(innerEdgeNinv=innerSecuredN.length-1;innerEdgeNinv>=innerEdgeN;innerEdgeNinv--) {
                                if((board.opponentBB&innerSecuredN[innerEdgeNinv]) == 0) break;
                                else oppCount++;
                            }
                            for(innerEdgeE=0;innerEdgeE<inEdgeE-2;innerEdgeE++) {
                                if((board.opponentBB&innerSecuredE[innerEdgeE]) == 0) break;
                                else oppCount++;
                            }
                            if((innerEdgeNinv==-1 && innerEdgeE==2) || (innerEdgeN==2 && innerEdgeE==2))
                                if((board.opponentBB & center[1]) != 0)
                                    oppCount++;
                        }
                    }
                }
            }
        }//checking from h-1 corner
        
        //--- Checking from lower-left corner
        if((board.opponentBB & corner[2]) != 0) {
            oppCount++;
            for(edgeWinv=securedW.length-1;edgeWinv>=edgeW;edgeWinv--) {
                if((board.opponentBB&securedW[edgeWinv]) == 0) break;
                else oppCount++;
            }
            for(edgeS=0; edgeS<securedS.length; edgeS++) {
                if((board.opponentBB&securedS[edgeS]) == 0) break;
                else oppCount++;
            }
            if((edgeWinv<=3 && edgeS>=2) || (edgeW==6 && edgeS>=2)) {
                if((board.opponentBB & xCorner[2]) != 0) {
                    oppCount++;
                    for(inEdgeWinv=inSecuredW.length-1;inEdgeWinv>=inEdgeW;inEdgeWinv--) {
                        if((board.opponentBB&inSecuredW[inEdgeWinv]) == 0) break;
                        else oppCount++;
                    }
                    for(inEdgeS=0; inEdgeS<edgeS-2; inEdgeS++) {
                        if((board.opponentBB&inSecuredS[inEdgeS]) == 0) break;
                        else oppCount++;
                    }
                    if((inEdgeWinv<=1 && inEdgeS>=2) || (inEdgeW==4 && inEdgeS>=2)) {
                        if((board.opponentBB & fourCorner[2]) != 0) {
                            oppCount++;
                            for(innerEdgeWinv=innerSecuredW.length-1;innerEdgeWinv>=innerEdgeW;innerEdgeWinv--) {
                                if((board.opponentBB&innerSecuredW[innerEdgeWinv]) == 0) break;
                                else oppCount++;
                            }
                            for(innerEdgeS=0;innerEdgeS<inEdgeS-2;innerEdgeS++) {
                                if((board.opponentBB&innerSecuredS[innerEdgeS]) == 0) break;
                                else oppCount++;
                            }
                            if((innerEdgeWinv==-1 && innerEdgeS==2) || (innerEdgeW==2 && innerEdgeS==2))
                                if((board.opponentBB & center[2]) != 0)
                                    oppCount++;
                        }
                    }
                }
            }
        }//checking from a-8 corner
        
        //--- Check from lower-right corner
        if((board.opponentBB & corner[3]) != 0) {
            oppCount++;
            for(edgeSinv=securedS.length-1;edgeSinv>edgeS;edgeSinv--) {
                if((board.opponentBB&securedS[edgeSinv]) == 0) break;
                else oppCount++;
            }
            for(edgeEinv=securedE.length-1; edgeEinv>edgeE; edgeEinv--) {
                if((board.opponentBB&securedE[edgeEinv]) == 0) break;
                else oppCount++;
            }
            if((edgeSinv<=3 && edgeEinv<=3) || (edgeS==6 && edgeE==6)
                    || (edgeSinv<=3 && edgeE==6) || (edgeEinv<=3 && edgeS==6)) {
                if((board.opponentBB & xCorner[3]) != 0) {
                    oppCount++;
                    for(inEdgeSinv=inSecuredS.length-1;inEdgeSinv>=inEdgeS;inEdgeSinv--) {
                        if((board.opponentBB&inSecuredS[inEdgeSinv]) == 0) break;
                        else oppCount++;
                    }
                    for(inEdgeEinv=inSecuredE.length-1; inEdgeEinv>=inEdgeE; inEdgeEinv--) {
                        if((board.opponentBB&inSecuredE[inEdgeEinv]) == 0) break;
                        else oppCount++;
                    }
                    if((inEdgeSinv<=1 && inEdgeEinv<=1) || (inEdgeS==4 && inEdgeE==4)
                            || (inEdgeSinv<=1 && inEdgeE==4) || (inEdgeEinv<=1 && inEdgeS==4)) {
                        if((board.opponentBB & fourCorner[3]) != 0) {
                            oppCount++;
                            for(innerEdgeSinv=innerSecuredS.length-1;innerEdgeSinv>=innerEdgeS;innerEdgeSinv--) {
                                if((board.opponentBB&innerSecuredS[innerEdgeSinv]) == 0) break;
                                else oppCount++;
                            }
                            for(innerEdgeEinv=innerSecuredE.length-1;innerEdgeEinv>=innerEdgeE;innerEdgeEinv--) {
                                if((board.opponentBB&innerSecuredE[innerEdgeEinv]) == 0) break;
                                else oppCount++;
                            }
                            if((innerEdgeSinv==-1 && innerEdgeEinv==-1) || (innerEdgeS==2 && innerEdgeE==-1)
                                    || (innerEdgeSinv==-1 && innerEdgeE==2) || (innerEdgeEinv==-1 && innerEdgeS==2))
                                if((board.opponentBB & center[1]) != 0)
                                    oppCount++;
                        }
                    }
                }
            }
        }//checking from h-8 corner
        
        return oppCount;
    }
    
    /**
     * @return the normalized value of any wedge on my board vs. any wedge on opp board
     */
    public double wedge() {
        int myCount=0;
        int oppCount=0;
        for(int i=0;i<wedge.length;i++) {
            if(Long.bitCount(board.myBB&wedge[i]) == 2) myCount++; 
            if(Long.bitCount(board.opponentBB&wedge[i]) == 2) oppCount++;
        }
        for(int i=0;i<stonerWarning.length;i++) {
            if(Long.bitCount(board.myBB&stonerWarning[i]) == 2) {
                myCount++;
                if(Long.bitCount(board.myBB&stoner[i]) == 4) myCount+=10;
            }
            if(Long.bitCount(board.opponentBB&stonerWarning[i]) == 2) {
                oppCount++;
                if(Long.bitCount(board.opponentBB&stoner[i]) == 4) oppCount+=10;
            }
        }
        double total = myCount+oppCount;
        if(total!=0) {
            return (oppCount-myCount)/total;
        }
        return 0.0;
    }

}
