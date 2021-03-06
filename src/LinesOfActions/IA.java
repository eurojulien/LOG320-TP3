package LinesOfActions;

import java.util.ArrayList;

public class IA implements Runnable{

    // considerations sur le board :
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

    public enum direction { NE, NW, E, N, S, W, SE, SW};
    // definition des directions :
    // DIRECTION NE
    // 00X
    // 0X0
    // X00
    // DIRECTION NW
    // X00
    // 0X0
    // 00X
    // DIRECTION EST
    // 000
    // XXX
    // 000
    // DIRECTION N
    // 0X0
    // 0X0
    // 0X0

    // ceci devrais etre 2 ou 4
    private int compteurTour = 0;
    private static final int BOARDSIZE = 8;
    private int playerNumber;
    private int enemyPlayerID;
    private int[][] playBoard;
    private int[][] solvingBoard;
    private ArrayList<int[]> positionsPions = new ArrayList<int[]>();
    private ArrayList<int[]> positionsPionsEnemy = new ArrayList<int[]>();
    private ArrayList<String> lstPossibleMove = new ArrayList<String>();
    private ArrayList<int[]> pieceCourantes = new ArrayList<int[]>();
    private ArrayList<int[]> piecesVisitees = new ArrayList<int[]>();
    

    
    
    private String bestMove = "";
    private int bestPointage = -100;
    private double centreIDeMasseAllier = 4.5;
    private double centreJDeMasseAllier = 4.5;
    private double centreIDeMasseEnemy = 4.5;
    private double centreJDeMasseEnemy = 4.5;

    // valeurs qui pourraient etre modifiee
    private static final int VALEUR_TUILE_ADVERSE = -4;
    private static final int TUILE_ADJACENTE_ALLIER = 1;
    private static final int POSITION_MASK_EXTERNE = 10;
    private static final int POSITION_MASK_MILLIEU = 17;
    private static final int POSITION_MASK_INTERIEUR = 25;
    private static final int BLOQUER_MOUVEMENT_ENEMY = 1;
    private static final int TROU_INTERMOTON = 4;
    private static final int BRISE_MOTON_ADVERSE = 10;
    private static final int NB_TOURS_DISTANCE_MASK = 10;
    private static final int NB_TOURS_BOUGER_PIECES_INITIALES = 6;

    public IA(int[][] playBoard, int playerNumber){
        cloneBoard(playBoard);
        this.playerNumber = playerNumber;
        initializeSolvingBoard();
        if(playerNumber == 4){
            enemyPlayerID = 2;
        }else{
            enemyPlayerID = 4;
        }
    }

    @Override
    // Thread de compilation d'arbre MiniMax
    public void run() {
        lstPossibleMove.clear();
        initializeSolvingBoard();
        initializePositionsList();
        bestMove = "";
        bestPointage = -100;
        compteurTour ++;
        // todo : are we keeping this ? check avec julien
        this.generateFastTree();
    }

    public String getBestMove(){
        generateBestScore();
        return this.bestMove;
    }

    public int getMeilleurScore(){
        generateBestScore();
        return bestPointage;
    }

    public void generateMoveList(boolean fastGen){
        if(!fastGen){
            initializePositionsList();
            fillInSolvingBoard();
        }
        for(int x =0; x<positionsPions.size();x++){
            genererMouvementPiece(positionsPions.get(x)[0], positionsPions.get(x)[1]);
        }
    }


    public ArrayList<String> getListeMouvements(){
        return this.lstPossibleMove;
    }

    private void generateBestScore(){
        for(int x = 0; x < lstPossibleMove.size();x ++){
            String currentMove = lstPossibleMove.get(x);
            int indexJ = getIndexFromLetter(currentMove.charAt(5));
            int indexi = Integer.parseInt(currentMove.substring(6, 7)) -1;
            if(solvingBoard[indexi][indexJ] > bestPointage){
                bestPointage = solvingBoard[indexi][indexJ];
                bestMove = currentMove;
            }
        }
    }


    public void generateFastTree(){
        // todo : are we keeping this ?
        generateMoveList(false);
        //System.out.println("Essais de move :");
        ArrayList<IA> listeDeMoveEtageInferieure = new ArrayList<IA>();
        for(int x = 0; x < lstPossibleMove.size();x ++){
            String currentMove = lstPossibleMove.get(x);
            int indexJ = getIndexFromLetter(currentMove.charAt(5));
            int indexi = Integer.parseInt(currentMove.substring(6, 7)) -1;
            IA newBoardTryOut = new IA(this.playBoard, enemyPlayerID);
            newBoardTryOut.notifyMovementEnemyTeam(currentMove);
            int meilleurScoreEnfant =  newBoardTryOut.getBestScore();
            //System.out.println(currentMove + " Valeur du move (MAX) :"+ solvingBoard[indexi][indexJ] + " valeu enemy meilleur =" +  meilleurScoreEnfant);
            if(solvingBoard[indexi][indexJ]  - meilleurScoreEnfant > bestPointage){
                bestPointage = solvingBoard[indexi][indexJ] - meilleurScoreEnfant;
                bestMove = currentMove;
            }
        }
    }

    public IA notifyAndGetNewIA(String movement){
        // cette methode permet a l'algoritme de prendre compte des deplacments
        // que notre adversaire fait !
        // IMPORTANT : Le format doit toujours etre "A5_-_B5"
        IA retour = new IA(playBoard, enemyPlayerID);
        retour.notifyMovementEnemyTeam(movement);
        return retour;
    }

    public void notifyMovementEnemyTeam(String movement){
        // cette methode permet a l'algoritme de prendre compte des deplacments
        // que notre adversaire fait !
        // IMPORTANT : Le format doit toujours etre "A5_-_B5"
        try{
        char[] tabLettres = movement.toCharArray();
        int posJDepart = getIndexFromLetter(tabLettres[0]);
        int posIDepart = Character.getNumericValue(tabLettres[1]) -1;
        int posJFin = getIndexFromLetter(tabLettres[5]);
        int posIFin = Character.getNumericValue(tabLettres[6]) -1;

        playBoard[posIFin][posJFin] = playBoard[posIDepart][posJDepart];
        playBoard[posIDepart][posJDepart] = 0;
        }catch (Exception ex){
            System.out.println("wtf happened ?");
        }
    }


    public void notifyMovementMyTeam(String movement){
        // cette methode permet a l'algoritme de prendre compte des deplacments
        // que nous fesons
        // IMPORTANT : Le format doit toujours etre "A5_-_B5"
        char[] tabLettres = movement.toCharArray();
        int posJDepart = getIndexFromLetter(tabLettres[0]);
        int posIDepart = Character.getNumericValue(tabLettres[1])-1;
        int posJFin = getIndexFromLetter(tabLettres[5]);
        int posIFin = Character.getNumericValue(tabLettres[6]) -1;
        playBoard[posIDepart][posJDepart] = 0;
        playBoard[posIFin][posJFin] = playerNumber;
    }

    public void drawBoard(boolean showSolvingBoard){
        // nous fait un dessin du board, pour le debugging
        System.out.println("====== PLAY BOARD ========");
        for(int i =BOARDSIZE-1; i >= 0; i--){
            System.out.println("");
            for(int j = 0; j< BOARDSIZE; j++){
                printASlot(playBoard[i][j]+"");
            }
        }
        System.out.println("");
        System.out.println("=========================");
        if(showSolvingBoard){
            System.out.println("====== SOLVE BOARD ========");
            for(int i =BOARDSIZE-1; i >= 0; i--){
                System.out.println("");
                for(int j = 0; j< BOARDSIZE; j++){
                    if(playBoard[i][j] == playerNumber)
                        printASlot("X");
                    else
                        printASlot(solvingBoard[i][j] + "");
                }
            }
            System.out.println("");
            System.out.println("=========================");
            System.out.println("");
            System.out.println("===== CENTRE DE MASSE =====");
            System.out.println("Allie : [" + centreIDeMasseAllier + " ; " + centreJDeMasseAllier + " ]");
            System.out.println("Enemy : [" + centreIDeMasseEnemy + " ; " + centreJDeMasseEnemy + " ]");
            System.out.println("Nombre de move possible : " + lstPossibleMove.size());
        }
    }

    private void printASlot(String text){
        char[] tableChar = text.toCharArray();
        for(int i = 0; i < 3; i++){
            if( i < tableChar.length){
                System.out.print(tableChar[i]);
            }else{
                System.out.print(" ");
            }
        }
        System.out.print(" ");
    }

    private void genererMouvementPiece(int i, int j){
        if(compteurTour > NB_TOURS_BOUGER_PIECES_INITIALES || (i==0 || j == 0 || i== BOARDSIZE-1 || j== BOARDSIZE-1 )){
            int distanceEstWest = distanceMove(i,j,direction.E);
            int distanceNordSud = distanceMove(i,j,direction.N);
            int distanceNordEst = distanceMove(i,j,direction.NE);
            int distanceNordWest = distanceMove(i, j, direction.NW);

            Boolean gauche = true;
            Boolean droite = true;
            // todo : watch for impossible move
            for(int x = 1; x <= distanceEstWest;x++){

                if(x+j >= BOARDSIZE){
                    droite = false;
                }
                else if(x == distanceEstWest){
                    if(playBoard[i][j+x] == playerNumber)
                        droite = false;
                }
                else if(playBoard[i][j+x] != playerNumber && playBoard[i][j+x]!= 0){
                    droite = false;
                }

                if(j-x < 0){
                    gauche = false;
                }
                else if(x == distanceEstWest){
                    if(playBoard[i][j-x] == playerNumber)
                        gauche = false;
                }
                else if(playBoard[i][j-x] != playerNumber && playBoard[i][j-x]!= 0){
                    gauche = false;
                }
            }

            if(droite){
                String toAdd = getLetterFromIndex(j)+ "" + (i+1) + " - " + getLetterFromIndex(j + distanceEstWest) + (i+1);
                lstPossibleMove.add(toAdd);
            }

            if(gauche){
                String toAdd = getLetterFromIndex(j) + "" + (1 + i) + " - " + getLetterFromIndex(j - distanceEstWest) + (i+1);
                lstPossibleMove.add(toAdd);
            }

            Boolean up = true;
            Boolean down = true;
            for(int x = 1; x <= distanceNordSud;x++){

                if(x+i >= BOARDSIZE){
                    down = false;
                }
                else if(x == distanceNordSud){
                    if(playBoard[i+x][j] == playerNumber)
                        down = false;
                }
                else if(playBoard[i+x][j] != playerNumber && playBoard[i+x][j]!= 0){
                    down = false;
                }

                if(i-x < 0){
                    up = false;
                }
                else if(x == distanceNordSud){
                    if(playBoard[i-x][j] == playerNumber)
                        up = false;
                }
                else if(playBoard[i-x][j] != playerNumber && playBoard[i-x][j]!= 0){
                    up = false;
                }

            }
            if(down){
                int indexI = i + distanceNordSud +1;
                String toAdd = getLetterFromIndex(j)+ "" + (i +1) + " - " + getLetterFromIndex(j) + indexI;
                lstPossibleMove.add(toAdd);
            }

            if(up){
                int indexI = i - distanceNordSud +1;
                String toAdd = getLetterFromIndex(j)+ "" + (i +1) + " - " + getLetterFromIndex(j) + indexI;
                lstPossibleMove.add(toAdd);
            }


            // todo : optimiser
            // todo : make sure it works !!
            gauche = true;
            droite = true;
            for(int x = 1; x <= distanceNordEst;x++){
                if(x+j >= BOARDSIZE || i-x < 0){
                    droite = false;
                }
                else if(x == distanceNordEst) {
                    if(playBoard[i-x][j+x] == playerNumber)
                        droite = false;
                }
                else if(playBoard[i-x][j+x] != playerNumber || playBoard[i-x][j+x]!= 0){
                    droite = false;
                }

                if(j-x < 0 || x+i >= BOARDSIZE ){
                    gauche = false;
                }
                else if(x == distanceNordEst){
                    if(playBoard[i+x][j-x] == playerNumber)
                        gauche = false;
                }
                else if(playBoard[i+x][j-x] != playerNumber || playBoard[i+x][j-x]!= 0){
                    gauche = false;
                }
            }

            if(droite){
                int indexI = i - distanceNordEst;
                int indexJ = j + distanceNordEst;
                String toAdd = getLetterFromIndex(j)+ "" + (i+1) + " - " + getLetterFromIndex(indexJ) + (1+indexI);
                lstPossibleMove.add(toAdd);
            }

            if(gauche){
                int indexI = i + distanceNordEst;
                int indexJ = j - distanceNordEst;
                String toAdd = getLetterFromIndex(j)+ "" + (i+1) + " - " + getLetterFromIndex(indexJ) + (1+indexI);
                lstPossibleMove.add(toAdd);
            }

            gauche = true;
            droite = true;
            for(int x = 1; x <= distanceNordWest;x++){
                if(x+j >= BOARDSIZE || i+x >= BOARDSIZE){
                    droite = false;
                }
                else if(x == distanceNordWest){
                    if(playBoard[i+x][j+x] == playerNumber)
                        droite = false;
                }
                else if(playBoard[i+x][j+x] != playerNumber || playBoard[i+x][j+x]!= 0)
                {
                    droite = false;
                }

                if(j-x < 0 || i-x < 0 ){
                    gauche = false;
                }
                else if(x == distanceNordWest){
                    if(playBoard[i-x][j-x] == playerNumber)
                        gauche = false;
                }
                else if(playBoard[i-x][j-x] != playerNumber || playBoard[i-x][j-x]!= 0){
                    gauche = false;
                }
            }

            if(droite){
                int indexI = i + distanceNordWest;
                int indexJ = j + distanceNordWest;
                String toAdd = getLetterFromIndex(j)+ "" + (i+1) + " - " + getLetterFromIndex(indexJ) + (indexI +1);
                lstPossibleMove.add(toAdd);
            }

            if(gauche){
                int indexI = i - distanceNordWest;
                int indexJ = j - distanceNordWest;
                String toAdd = getLetterFromIndex(j)+ "" + (i+1) + " - " + getLetterFromIndex(indexJ) + (1 + indexI);
                lstPossibleMove.add(toAdd);
            }
        }
    }

    private int getBestScore(){
    // todo : peut etre enelver
        generateMoveList(false);
    	for(int x = 0; x < lstPossibleMove.size();x ++){
            String currentMove = lstPossibleMove.get(x);
            int indexJ = getIndexFromLetter(currentMove.charAt(5));
            int indexi = Integer.parseInt(currentMove.substring(6, 7));
            int valueMove = solvingBoard[indexi-1][indexJ];
            // todo : keep more than 1 value !
            if(valueMove > bestPointage){
                bestPointage = valueMove;
                bestMove = currentMove;
            }
        }
        return bestPointage;
    }

    public int[] getCoordsAfterMove(int i, int j, int distance, direction dir){
        int [] retour = new int[2];
        switch (dir){
            case E:
                retour = new int[] {i,j + distance};
                break;
            case N:
                retour = new int[] {i+ distance,j};
                break;
            case NE:
                retour = new int[] {i- distance,j + distance};
                break;
            case NW:
                retour = new int[] {i- distance,j - distance};
                break;
            case W:
                retour = new int[] {i,j - distance};
                break;
            case S:
                retour = new int[] {i + distance,j};
                break;
            case SW:
                retour = new int[] {i+ distance,j - distance};
                break;
            case SE:
                retour = new int[] {i+ distance,j + distance};
                break;
        }
        return retour;
    }

    private int distanceMove(int i, int j, direction d){
        // cette methode retourne le nombre de pions sur une ligne d'action
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
                while(x+j < BOARDSIZE && i-x >= 0 ){
                    if(playBoard[i-x][j+x] != 0) retour ++;
                    x++;
                }
                x = 1;
                while(j-x >= 0 && i+x < BOARDSIZE){
                    if(playBoard[i+x][j-x] != 0) retour ++;
                    x++;
                }
                break;

            case NW:
                x = 0;
                while(x+j < BOARDSIZE && i+x < BOARDSIZE){
                    if(playBoard[i+x][j+x] != 0) retour ++;
                    x++;
                }
                x = 1;
                while(j-x >= 0 && i-x >= 0){
                    if(playBoard[i-x][j-x] != 0) retour ++;
                    x++;
                }
                break;

            case W:
                for(x = 0; x< BOARDSIZE;x++){ if(playBoard[i][x] != 0) retour ++; }
                break;

            case S:
                for(x = 0; x< BOARDSIZE;x++){ if(playBoard[x][j] != 0) retour ++; }
                break;

            case SW:
                x = 0;
                while(x+j < BOARDSIZE && i-x >= 0 ){
                    if(playBoard[i-x][j+x] != 0) retour ++;
                    x++;
                }
                x = 1;
                while(j-x >= 0 && i+x < BOARDSIZE){
                    if(playBoard[i-x][j-x] != 0) retour ++;
                    x++;
                }
                break;

            case SE:
                x = 0;
                while(x+j < BOARDSIZE && i+x < BOARDSIZE){
                    if(playBoard[i+x][j+x] != 0) retour ++;
                    x++;
                }
                x = 1;
                while(j-x >= 0 && i-x >= 0){
                    if(playBoard[i+x][j-x] != 0) retour ++;
                    x++;
                }
                break;
        }
        return retour;
    }

    private void removePiecesFromSolvingBoard(){
        // methode qui enleve (met a -1) toutes les pieces de notre jeux du solving board
        for(int i = 0 ; i < positionsPions.size(); i++){
            solvingBoard[positionsPions.get(i)[0]][positionsPions.get(i)[1]] = -10;
        }
    }

    private char getLetterFromIndex(int letter){
        // retourne la lettre liee a l'index donne
        letter++;
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
        // retourne l'index lie a la lettre donnee
        int retour = -1;
        switch (letter){
            case 'A':
                retour = 0;
                break;
            case 'B':
                retour = 1;
                break;
            case 'C':
                retour = 2;
                break;
            case 'D':
                retour = 3;
                break;
            case 'E':
                retour = 4;
                break;
            case 'F':
                retour = 5;
                break;
            case 'G':
                retour = 6;
                break;
            case 'H':
                retour = 7;
                break;
        }

        return retour;
    }

    public int getScoreForBoard(){
        generateMoveList(true);
        int boardScore = 0;
        boardScore += obtenirScoreMoton();
        boardScore += lstPossibleMove.size();
        boardScore += getScoreForCentrality();


        return boardScore;
    }

    public int getScoreForCentrality(){
        int retour =0;
        calculerCentreDeMasses();
        applyPositionMask();
        for(int x = 0; x < positionsPions.size(); x++){
            retour+= solvingBoard[positionsPions.get(x)[0]][positionsPions.get(x)[1]];
        }
        return retour;
    }


    private void initializeSolvingBoard(){
        // initialise le solving board
        solvingBoard = new int[8][8];
    }

    private void initializePositionsList(){
        // trouve toutes nos pieces et les ajoutes dans l'array de pieces
        positionsPions = new ArrayList<int[]>();
        for(int i=0;i<BOARDSIZE;i++){
            for(int j=0;j<BOARDSIZE;j++){
                if(playBoard[i][j] == playerNumber){
                    positionsPions.add(new int[] {i,j});
                    solvingBoard[i][j] = -10;
                }else if(playBoard[i][j] != 0){
                    positionsPionsEnemy.add(new int[] {i,j});
                }
            }
        }
    }

    private void fillInSolvingBoard(){
        // cette methode fournie une evaluation de chaque tuile du jeu ainsi que leurs valeurs.
        // on incremente autour de toutes nos pieces pour indiquer que ce sont des positions favorables
        removePiecesFromSolvingBoard();
        calculerCentreDeMasses();
        applyPositionMask();
        reduceEnemyPositions();
        applyCounterEnemy();
        trouverMotton();
        //drawBoard(true);
        //if(compteurTour <= NB_TOURS_DISTANCE_MASK)
            //applyDistanceMask();
    }

    private void applyCounterEnemy(){
        for(int i=0;i<BOARDSIZE ; i++){
            for(int j=0; j<BOARDSIZE;j++){
                if(playBoard[i][j] == 0){
                    for(int x = 0; x < positionsPionsEnemy.size();x++){

                        int posEnemyI = positionsPionsEnemy.get(x)[0];
                        int posEnemyJ = positionsPionsEnemy.get(x)[1];

                        if((posEnemyI+1 ==i || posEnemyI-1 == i || posEnemyI == i)
                                && (posEnemyJ+1 == j || posEnemyJ-1 == j || posEnemyJ == j)){
                            // la tuile est adjacente a une tuile enemie
                            if((posEnemyI < i && centreIDeMasseEnemy > i)
                                || (posEnemyI > i && centreIDeMasseEnemy < i)
                                || (posEnemyJ < j && centreJDeMasseEnemy > j)
                                || (posEnemyJ > j && centreJDeMasseEnemy < j)){

                                // la tuile se situe entre la piece examinee et le centre de masse de l'adversaire
                                incrementPositionWithValidation(i,j, BLOQUER_MOUVEMENT_ENEMY);
                            }
                        }
                    }
                }
            }
        }
    }


    private void calculerCentreDeMasses(){
        // cette methode calcule le centre de masse des joueurs
        double cummulateurI = 0;
        double cummulateurJ = 0;
        for(int x = 0; x < positionsPions.size();x++){
            cummulateurI += positionsPions.get(x)[0] + 1;
            cummulateurJ += positionsPions.get(x)[1] + 1;
        }

        centreIDeMasseAllier = cummulateurI / positionsPions.size();
        centreJDeMasseAllier = cummulateurJ / positionsPions.size();


        cummulateurI = 0;
        cummulateurJ = 0;
        for(int x = 0; x < positionsPionsEnemy.size();x++){
            cummulateurI += positionsPionsEnemy.get(x)[0] + 1;
            cummulateurJ += positionsPionsEnemy.get(x)[1] + 1;
        }

        centreIDeMasseEnemy = cummulateurI / positionsPionsEnemy.size();
        centreJDeMasseEnemy = cummulateurJ / positionsPionsEnemy.size();
    }




    private void reduceEnemyPositions(){
        // on applique un malus sur les tuiles enemies (manger l'adversaire est negatif)
        for(int i = 0; i < BOARDSIZE; i++){
            for(int j = 0; j < BOARDSIZE; j++){
                if(playBoard[i][j] != 0 && playBoard[i][j] != playerNumber){
                    solvingBoard[i][j] += VALEUR_TUILE_ADVERSE;
                }
            }
        }
    }


    private void applyPositionMask(){
        // on applique sur le solving board le masque de position ou le centre est plus favorable
        for(int i =0; i < BOARDSIZE; i++){
            for(int j = 0; j< BOARDSIZE; j++){
                if(i > (centreIDeMasseAllier-2) && j > (centreJDeMasseAllier-2)
                        && i < (centreIDeMasseAllier) && j < (centreJDeMasseAllier)){
                    incrementPositionWithValidation(i,j, POSITION_MASK_INTERIEUR);
                }
                else if(i > (centreIDeMasseAllier-3) && j > (centreJDeMasseAllier-3)
                        && i < (centreIDeMasseAllier+1) && j < (centreJDeMasseAllier+1)){
                    incrementPositionWithValidation(i,j, POSITION_MASK_MILLIEU);
                }
                else if(i > (centreIDeMasseAllier-4) && j > (centreJDeMasseAllier-4)
                        && i < (centreIDeMasseAllier+2) && j < (centreJDeMasseAllier+2)){
                    incrementPositionWithValidation(i,j, POSITION_MASK_EXTERNE);
                }
            }
        }
    }



    private void incrementAround(int i, int j, int howMuch){
        // fait l'increment de toutes les tuiles autour de [i][j] ne contenant pas de pieces qui nous appartiens
        if(i > 0){
            incrementPositionWithValidation(i-1,j,howMuch);
            if(j > 0)
                incrementPositionWithValidation(i-1,j-1,howMuch);
            if(j < BOARDSIZE-1)
                incrementPositionWithValidation(i-1,j+1,howMuch);
        }

        if(i < BOARDSIZE-1){
            incrementPositionWithValidation(i+1,j,howMuch);
            if(j > 0)
                incrementPositionWithValidation(i+1,j-1,howMuch);
            if(j < BOARDSIZE-1)
                incrementPositionWithValidation(i+1,j+1,howMuch);
        }

        if(j > 0)
            incrementPositionWithValidation(i,j - 1,howMuch);
        if(j < BOARDSIZE-1)
            incrementPositionWithValidation(i,j + 1,howMuch);

    }

    private void incrementPositionWithValidation(int i, int j,int value){
        // incremente la valeur d'une tuile en s'assurant que cette tuile n'est pas un de nos pions
        if(solvingBoard[i][j] != -10)
            solvingBoard[i][j]+= value;
    }

    // 4500 * 100 donne 500 millisecondes pour l'envoie des donnees
	private final double COMPUTING_TIME_LIMIT_IN_NANOSECONDS = 4.0 * Math.pow(10, 9);
	private long startTime 	= 0;


    private void cloneBoard(int[][] argumentBoard){
        // todo : est-ce que sa sameliore ??
        playBoard = new int[BOARDSIZE][BOARDSIZE];
        for(int i = 0;i<BOARDSIZE;i++){
            for(int j = 0;j<BOARDSIZE;j++){
                this.playBoard[i][j] = argumentBoard[i][j];
            }
        }
    }

    public int obtenirScoreMoton(){
        int retour = 0;
        pieceCourantes.clear();
        piecesVisitees.clear();
        for(int x =0; x<positionsPions.size();x++){
            parcoursMotton(positionsPions.get(x)[0],positionsPions.get(x)[1]);
            retour += Math.pow(pieceCourantes.size(), 2);
            pieceCourantes.clear();
        }
        return retour;
    }


    public void trouverMotton(){
    	pieceCourantes.clear();
        piecesVisitees.clear();



        for(int x =0; x<positionsPions.size();x++){

            parcoursMotton(positionsPions.get(x)[0],positionsPions.get(x)[1]);

            for(int y = 0 ; y < pieceCourantes.size() ; y++){
                incrementAround(pieceCourantes.get(y)[0],pieceCourantes.get(y)[1],pieceCourantes.size()*2);
            }

            pieceCourantes.clear();
        }

    }
    
    public void parcoursMotton(int i, int j){

        inspecterTuilePourMoton(i,j);
    	if(i > 0){
    			inspecterTuilePourMoton(i-1,j);
            if(j > 0)
            	inspecterTuilePourMoton(i-1,j-1);
            if(j < BOARDSIZE-1)
            	inspecterTuilePourMoton(i-1,j+1);
        }

        if(i < BOARDSIZE-1){
        	inspecterTuilePourMoton(i+1,j);
            if(j > 0)
            	inspecterTuilePourMoton(i+1,j-1);
            if(j < BOARDSIZE-1)
            	inspecterTuilePourMoton(i+1,j+1);
        }

        if(j > 0)
        	inspecterTuilePourMoton(i,j-1);
        if(j < BOARDSIZE-1)
        	inspecterTuilePourMoton(i,j+1);
    }
    
    public void inspecterTuilePourMoton(int i, int j){
    	
    	boolean dejaVisite = false;
    	for(int x = 0 ; x < piecesVisitees.size() ; x++)
    	{
    		if(piecesVisitees.get(x)[0] == i && piecesVisitees.get(x)[1] == j)
    			dejaVisite = true;
    	}
    	
    	if(playBoard[i][j] == playerNumber && !dejaVisite){
            if(i != 0 && j !=0 && i != BOARDSIZE-1 && j != BOARDSIZE-1){
                pieceCourantes.add(new int[]{i,j});
            }
    		piecesVisitees.add(new int[]{i,j});
    		parcoursMotton(i,j);
    	}
    }
    
    
}
