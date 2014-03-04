package LinesOfActions;

import java.util.ArrayList;

public class IA {

    // ceci devrais etre 2 ou 4
    private static final int BOARDSIZE = 10;
    private static int playerNumber;
    private int[][] playBoard;
    private int[][] solvingBoard;
    private ArrayList<int[]> positionsPions = new ArrayList<int[]>();


    public IA(int[][] playBoard, int playerNumber){
        this.playBoard = playBoard;
        this.playerNumber = playerNumber;
        initializeSolvingBoard();
        initializePositionsList();
        fillInSolvingBoard();
    }

    public IA(int[][] playBoard, int playerNumber,int[][] solvingBoard){
        this.playBoard = playBoard;
        this.playerNumber = playerNumber;
        this.solvingBoard = solvingBoard;
        initializePositionsList();
        fillInSolvingBoard();
    }

    public IA(int[][] playBoard, int playerNumber,int[][] solvingBoard,ArrayList<int[]> positionsPions){
        this.playBoard = playBoard;
        this.playerNumber = playerNumber;
        this.solvingBoard = solvingBoard;
        this.positionsPions = positionsPions;
        fillInSolvingBoard();
    }

    public void notifyMovement(String movement){
        char[] tabLettres = movement.toLowerCase().toCharArray();
        int posJDepart = getIndexFromLetter(tabLettres[0]);
        int posIDepart = Character.getNumericValue(tabLettres[1])-1;
        int posJFin = getIndexFromLetter(tabLettres[5]);
        int posIFin = Character.getNumericValue(tabLettres[6]) -1;

        playBoard[posIDepart][posJDepart] = 0;
        playBoard[posIFin][posJFin] =playerNumber;

        for(int i=0;i<positionsPions.size();i++){
            int[] comparaison = positionsPions.get(i);
            if(comparaison[0] == posIDepart && comparaison[1] == posJDepart){
                positionsPions.remove(i);
                positionsPions.add(new int[] {posIFin,posJFin});
            }
        }

        initializeSolvingBoard();
        removePiecesFromSolving();
        fillInSolvingBoard();
    }

    public void drawBoard(boolean showSolvingBoard){
        System.out.println("====== PLAY BOARD ========");
        for(int i =0; i < BOARDSIZE; i++){
            System.out.println("");
            for(int j = 0; j< BOARDSIZE; j++){
                System.out.print(playBoard[i][j] + " ");
            }
        }
        System.out.println("");
        System.out.println("=========================");

        if(showSolvingBoard){
            System.out.println("====== SOLVE BOARD ========");
            for(int i =0; i < BOARDSIZE; i++){
                System.out.println("");
                for(int j = 0; j< BOARDSIZE; j++){
                    System.out.print(solvingBoard[i][j] + " ");
                }
            }
            System.out.println("");
            System.out.println("=========================");

        }

    }

    private void removePiecesFromSolving(){
        for(int i = 0 ; i < positionsPions.size(); i++){
            solvingBoard[positionsPions.get(i)[0]][positionsPions.get(i)[1]] = -1;
        }
    }

    private int getIndexFromLetter(char letter){
        int retour = -1;
        switch (letter){
            case 'a':
                retour = 0;
                break;
            case 'b':
                retour = 1;
                break;
            case 'c':
                retour = 2;
                break;
            case 'd':
                retour = 3;
                break;
            case 'e':
                retour = 4;
                break;
            case 'f':
                retour = 5;
                break;
            case 'g':
                retour = 6;
                break;
            case 'h':
                retour = 7;
                break;
        }

        return retour;
    }

    private void initializeSolvingBoard(){
        solvingBoard = new int[10][10];
    }

    private void initializePositionsList(){
        positionsPions = new ArrayList<int[]>();
        for(int i=0;i<BOARDSIZE;i++){
            for(int j=0;j<BOARDSIZE;j++){
                if(playBoard[i][j] == playerNumber){
                    positionsPions.add(new int[] {i,j});
                    solvingBoard[i][j] = -1;
                }
            }
        }
    }

    private void fillInSolvingBoard(){
        // on incrémente autour de toutes nos pieces pour indiquer que ce sont des positions favorables
        for(int i =0;i<positionsPions.size();i++){
            incrementAround(positionsPions.get(i)[0],positionsPions.get(i)[1]);
        }

        // on incrémente pour les pions connectés avec un autre pion
        for(int i =0;i<positionsPions.size();i++){
            int[] coordLookup = positionsPions.get(i);
            for(int j =0;j<positionsPions.size();j++){
                if(i != j){
                    int[] coordCompare = positionsPions.get(j);
                    if(coordCompare[0]-1 == coordLookup[0] || coordCompare[0]+1 == coordLookup[0] ||
                       coordCompare[1]-1 == coordLookup[1] || coordCompare[1]+1 == coordLookup[1]){
                        // ici, la donnée comparée est a 1 de distance (elle touche) l'autre pièce.
                        incrementAround(coordLookup[0],coordLookup[1]);
                    }

                }
            }
        }
        applyPositionMask();
    }


    private void applyPositionMask(){
        for(int i =0; i < BOARDSIZE; i++){
            for(int j = 0; j< BOARDSIZE; j++){
                if(i >= 1 && j >= 1 && i <= (BOARDSIZE-2) && j <= (BOARDSIZE-2)){
                    incrementPositionWithValidation(i,j);
                }
                if(i >= 2 && j >= 2 && i <= (BOARDSIZE-3) && j <= (BOARDSIZE-3)){
                    incrementPositionWithValidation(i,j);
                }
                if(i >= 3 && j >= 3 && i <= (BOARDSIZE-4) && j <= (BOARDSIZE-4)){
                    incrementPositionWithValidation(i,j);
                }
                if(i >= 4 && j >= 4 && i <= (BOARDSIZE-5) && j <= (BOARDSIZE-5)){
                    incrementPositionWithValidation(i,j);
                }
            }
        }
    }



    private void incrementAround(int i, int j){
        if(i > 0){
            incrementPositionWithValidation(i-1,j);
            if(j > 0)
                incrementPositionWithValidation(i-1,j-1);
            if(j < BOARDSIZE-1)
                incrementPositionWithValidation(i-1,j+1);
        }

        if(i < BOARDSIZE-1){
            incrementPositionWithValidation(i+1,j);
            if(j > 0)
                incrementPositionWithValidation(i+1,j-1);
            if(j < BOARDSIZE-1)
                incrementPositionWithValidation(i+1,j+1);
        }

        if(j > 0)
            incrementPositionWithValidation(i,j - 1);
        if(j < BOARDSIZE-1)
            incrementPositionWithValidation(i,j + 1);

    }

    private void incrementPositionWithValidation(int i, int j){
        if(solvingBoard[i][j] != -1)
            solvingBoard[i][j]++;
    }
}
