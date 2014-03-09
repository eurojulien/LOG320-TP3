package LinesOfActions_2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class BoardInOut {

	public static final int BOARDSIZE = 8;
	
	private ServerConnect server = new ServerConnect();
	private final boolean joueurHumain = true;
	private BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
	private int[][] board = new int[BOARDSIZE][BOARDSIZE];
	
	public static void main(String[] args) {
		BoardInOut io = new BoardInOut();
		
		while(true){
			try {
				io.talkToServer();
			} catch (IOException e) {e.printStackTrace();}
		}
	}
	
	public void talkToServer() throws IOException{
		
		char cmd = server.getServerCommand();
		String coup = "";
				
		if(cmd == '1'){
			board = server.getBoardSetup();
			printBoard();
            System.out.println("Nouvelle partie! Vous jouez blanc, entrez votre premier coup : ");
            coup = console.readLine();
            jouer(coup);
        }
		
        // Debut de la partie en joueur Noir
        if(cmd == '2'){
        	board = server.getBoardSetup();
        	printBoard();
        	System.out.println("Nouvelle partie! Vous jouez noir, attendez le coup des blancs");
        }
        
		// Le serveur demande le prochain coup
		// Le message contient aussi le dernier coup joue.
		if(cmd == '3'){
			coup = server.getLastTurn();
			System.out.println("Dernier coup : "+ coup);
			updateBoard(coup);
			printBoard();
			
	       	System.out.println("Entrez votre coup : ");
	       	coup = console.readLine();
	       	jouer(coup);
	       	updateBoard(coup);
			printBoard();	       	
		}
		
		// Le dernier coup est invalide
		if(cmd == '4'){
			System.out.println("Coup invalide, entrez un nouveau coup : ");
			server.sendServerCommand(console.readLine());
		}
    }
	
	public void printBoard(){
		
        System.out.print("\n********************************************\n************** Planche de Jeu **************\n********************************************\n\n");
        
        for(int i = 0 ; i < BOARDSIZE ; i++){
            for(int j = 0 ; j < BOARDSIZE ; j++){
    			System.out.print(board[i][j] + " ");
    			if(j == 7) System.out.print("\n");
    		}
        }
        
        System.out.print("\n\n");
    }
	
	public void jouer(String coup){
				

			if(joueurHumain)
	       		server.sendServerCommand(coup);
			//else
	        	//Proposition :
	        	//server.sendServerCommand(getNextMove(board));

		
	}
	
	private int getIndexFromLetter(char letter){
        
		int retour = -1;
        
        switch (letter){
            case 'a': retour = 0; break;
            case 'b': retour = 1; break;
            case 'c': retour = 2; break;
            case 'd': retour = 3; break;
            case 'e': retour = 4; break;
            case 'f': retour = 5; break;
            case 'g': retour = 6; break;
            case 'h': retour = 7; break;
        }

        return retour;
    }

	//Mise a jour du board	
	public void updateBoard(String s){
		
		s = s.toLowerCase().trim();
		String delemiter = " - ";
		
		if(s.contains(delemiter)) 
			s = s.replace(" - ","");
		
		char[] charArray = s.toCharArray();
		
		int posA1 = BOARDSIZE - Character.getNumericValue(charArray[1]);
		int posA2 = getIndexFromLetter(charArray[0]);
		int posB1 = BOARDSIZE - Character.getNumericValue(charArray[3]);
		int posB2 = getIndexFromLetter(charArray[2]);
		
		int chiffrePion = board[posA1][posA2];
		board[posA1][posA2] = 0;
		board[posB1][posB2] = chiffrePion;
	}
}
