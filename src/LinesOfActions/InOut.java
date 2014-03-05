package LinesOfActions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class InOut {

	private ServerConnect server = new ServerConnect();
	private boolean humain = true;
	BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
	private static final int BOARDSIZE = 8;
	private int[][] board = new int[BOARDSIZE][BOARDSIZE];
	
	public static void main(String[] args) {
		InOut io = new InOut();
		
		while(true){
			try {
				io.talkToServer();
			} catch (IOException e) {e.printStackTrace();}
		}
	}
	
	public void talkToServer() throws IOException{
		
		char cmd = server.readServerCommand();
		
		if(cmd == '1'){
			board = server.getBoardSetup();
			printBoard();
            System.out.println("Nouvelle partie! Vous jouez blanc, entrez votre premier coup : "); 
            jouer();
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
			String s = server.getLastTurn();
			System.out.println("Dernier coup : "+ s);
			updateBoard(s);
			printBoard();
			
	       	System.out.println("Entrez votre coup : ");
	       	
	       	jouer();
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
	
	public void jouer(){
		
		if(humain)
       		server.sendServerCommand(console.readLine());
       	else
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

	//Mise à jour du board	
	public void updateBoard(String s, boolean blancs){
		
		s = s.toLowerCase();
		String delemiter = " - ";
		
		if(s.contains(delemiter)) 
			s = s.replace(" - ","");
		
		char[] charArray = s.toCharArray();
		
		int posA1 = getIndexFromLetter(charArray[0]);
		int pions = 2;
		if(blancs) pions = 4;
		
		board[getIndexFromLetter(charArray[0])][(charArray[1]] = 0;
		board[getIndexFromLetter(charArray[2])][(charArray[3]] = pions;
	}
}
