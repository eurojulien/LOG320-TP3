package LinesOfActions;

import java.util.ArrayList;

public class IA {

    // considérations sur le board :
    // - I EST VERTICAL (CHIFFRES)
    // - J EST HORIZONTAL (LETTRE)

    //I 0222222220
    //I 4000000004
    //I 4000000004
    //I 4000000004
    //I 4000000004
    //I 4000000004
    //I 4000000004
    //I 4000000004
    //I 4000000004
    //I 0222222220
    //  JJJJJJJJJJ

    public enum direction { NE, NW, E, N};
    // définition des directions :
    // DIRECTION NE
    // 00X
    // 0X0
    // X00
    // DIRECTION NW
    // X00
    // 0X0
    // 00X
    // DIRECTION E
    // 000
    // XXX
    // 000
    // DIRECTION S
    // 0X0
    // 0X0
    // 0X0

    // ceci devrais etre 2 ou 4
    private static final int BOARDSIZE = 10;
    private static int playerNumber;
    private int[][] playBoard;
    private int[][] solvingBoard;
    private ArrayList<int[]> positionsPions = new ArrayList<int[]>();

    // valeurs qui pourraient être modifiée
    private static final int enemyPawnDevalue = -3;
    private static final int tuileToucheUnAllier = 1;
    private static final int importanceCentraleIncrement1 = 1;
    private static final int importanceCentraleIncrement2 = 2;
    private static final int importanceCentraleIncrement3 = 3;
    private static final int importanceCentraleIncrement4 = 4;

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


    public void notifyMovementEnemyTeam(String movement){
        // cette méthode permet a l'algoritme de prendre compte des déplacments
        // que notre adversaire fait !
        // IMPORTANT : Le format doit toujours être "A5_-_B5"

        char[] tabLettres = movement.toLowerCase().toCharArray();
        int posJDepart = getIndexFromLetter(tabLettres[0]);
        int posIDepart = Character.getNumericValue(tabLettres[1])-1;
        int posJFin = getIndexFromLetter(tabLettres[5]);
        int posIFin = Character.getNumericValue(tabLettres[6]) -1;

        playBoard[posIFin][posJFin] = playBoard[posIDepart][posJDepart];
        playBoard[posIDepart][posJDepart] = 0;

        for(int i=0;i<positionsPions.size();i++){
            int[] comparaison = positionsPions.get(i);
            if(comparaison[0] == posIFin && comparaison[1] == posJFin){
                positionsPions.remove(i);
            }
        }

        initializeSolvingBoard();
        removePiecesFromSolving();
        fillInSolvingBoard();
    }


    public void notifyMovementMyTeam(String movement){
        // cette méthode permet a l'algoritme de prendre compte des déplacments
        // que nous fesons
        // IMPORTANT : Le format doit toujours être "A5_-_B5"


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
        // nous fait un dessin du board, pour le debugging
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

   public int distanceMove(int i, int j, direction d){
       // cette méthode retourne le nombre de pions sur une ligne d'action
       int retour = 0;
       int x;
       switch (d){
           case E:
               for(x = 0; x< BOARDSIZE;x++){ if(playBoard[i][x] != 0) retour ++; }
               break;

           case N:
               for(x = 0; x< BOARDSIZE;x++){ if(playBoard[x][j] != 0) retour ++; }
               break;

           case NE:
               x = 0;
               while(x+j < BOARDSIZE && x+i < BOARDSIZE){
                   if(playBoard[i+x][j+x] != 0) retour ++;
                   x++;
               }
               x = 1;
               while(j-x >= 0 && i-x >= 0){
                   if(playBoard[i-x][j-x] != 0) retour ++;
                   x++;
               }
               break;

           case NW:
               x = 0;
               while(x+j < BOARDSIZE && i-x >= 0){
                   if(playBoard[i-x][j+x] != 0) retour ++;
                   x++;
               }
               x = 1;
               while(j-x >= 0 && i+x < BOARDSIZE){
                   if(playBoard[i+x][j-x] != 0) retour ++;
                   x++;
               }
               break;
       }
       return retour;
   }

    private void removePiecesFromSolving(){
        // méthode qui enlève (met a -1) toutes les pièces de notre jeux du solving board
        for(int i = 0 ; i < positionsPions.size(); i++){
            solvingBoard[positionsPions.get(i)[0]][positionsPions.get(i)[1]] = -1;
        }
    }

    private char getLatterFromIndex(int letter){
        // retourne la lettre lié a l'index donné
        char retour = 'z';
        switch (letter){
            case 1:
                retour = 'A';
                break;
            case 2:
                retour = 'B';
                break;
            case 3:
                retour = 'C';
                break;
            case 4:
                retour = 'D';
                break;
            case 5:
                retour = 'E';
                break;
            case 6:
                retour = 'F';
                break;
            case 7:
                retour = 'G';
                break;
            case 8:
                retour = 'H';
                break;
        }

        return retour;
    }

    private int getIndexFromLetter(char letter){
        // retourne l'index lié a la lettre donnée
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
        // initialise le solving board
        solvingBoard = new int[10][10];
    }

    private void initializePositionsList(){
        // trouve toutes nos pièces et les ajoutes dans l'array de pieces
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
        // cette méthode fournie une évaluation de chaque tuile du jeu ainsi que leurs valeurs.
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
        reduceEnemyPositions();
    }


    private void reduceEnemyPositions(){
        // on applique un malus sur les tuiles enemies (manger l'adversaire est negatif)
        for(int i = 0; i < BOARDSIZE; i++){
            for(int j = 0; j < BOARDSIZE; j++){
                if(playBoard[i][j] != 0 && playBoard[i][j] != playerNumber){
                    solvingBoard[i][j] += enemyPawnDevalue;
                }
            }
        }
    }


    private void applyPositionMask(){
        // on applique sur le solving board le masque de position ou le centre est plus favorable
        for(int i =0; i < BOARDSIZE; i++){
            for(int j = 0; j< BOARDSIZE; j++){
                if(i >= 1 && j >= 1 && i <= (BOARDSIZE-2) && j <= (BOARDSIZE-2)){
                    incrementPositionWithValidation(i,j,importanceCentraleIncrement1);
                }
                else if(i >= 2 && j >= 2 && i <= (BOARDSIZE-3) && j <= (BOARDSIZE-3)){
                    incrementPositionWithValidation(i,j,importanceCentraleIncrement2);
                }
                else if(i >= 3 && j >= 3 && i <= (BOARDSIZE-4) && j <= (BOARDSIZE-4)){
                    incrementPositionWithValidation(i,j,importanceCentraleIncrement3);
                }
                else if(i >= 4 && j >= 4 && i <= (BOARDSIZE-5) && j <= (BOARDSIZE-5)){
                    incrementPositionWithValidation(i,j,importanceCentraleIncrement4);
                }
            }
        }
    }



    private void incrementAround(int i, int j){
        // fait l'incrément de toutes les tuiles autour de [i][j] ne contenant pas de pièces qui nous appartiens
        if(i > 0){
            incrementPositionWithValidation(i-1,j,tuileToucheUnAllier);
            if(j > 0)
                incrementPositionWithValidation(i-1,j-1,tuileToucheUnAllier);
            if(j < BOARDSIZE-1)
                incrementPositionWithValidation(i-1,j+1,tuileToucheUnAllier);
        }

        if(i < BOARDSIZE-1){
            incrementPositionWithValidation(i+1,j,tuileToucheUnAllier);
            if(j > 0)
                incrementPositionWithValidation(i+1,j-1,tuileToucheUnAllier);
            if(j < BOARDSIZE-1)
                incrementPositionWithValidation(i+1,j+1,tuileToucheUnAllier);
        }

        if(j > 0)
            incrementPositionWithValidation(i,j - 1,tuileToucheUnAllier);
        if(j < BOARDSIZE-1)
            incrementPositionWithValidation(i,j + 1,tuileToucheUnAllier);

    }

    private void incrementPositionWithValidation(int i, int j,int value){
        // incrémente la valeur d'une tuile en s'assurant que cette tuile n'est pas un de nos pions
        if(solvingBoard[i][j] != -1)
            solvingBoard[i][j]+= value;
    }
}
