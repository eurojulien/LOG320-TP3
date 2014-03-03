package LinesOfActions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class InOut {

	private ServerConnect server = new ServerConnect();
	BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
	
	public static void main(String[] args) {
		InOut io = new InOut();
		
		while(true){
			try {
				io.talkToServer();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void talkToServer() throws IOException{
		
		char cmd = server.readServerCommand();
		
		if(cmd == '1'){
			server.refreshBoard();
			printBoard();
            System.out.println("Nouvelle partie! Vous jouez blanc, entrez votre premier coup : ");
            server.sendServerCommand(console.readLine());
        }
		
        // Debut de la partie en joueur Noir
        if(cmd == '2'){
        	server.refreshBoard();
        	printBoard();
        	System.out.println("Nouvelle partie! Vous jouez noir, attendez le coup des blancs");
        }
        
		// Le serveur demande le prochain coup
		// Le message contient aussi le dernier coup joue.
		if(cmd == '3'){
			//Prochain coup?
			String s = server.readLastTurn();
			System.out.println("Dernier coup : "+ s);
			
			printBoard();
			
	       	System.out.println("Entrez votre coup : ");
	       	server.sendServerCommand(console.readLine());
		}
		
		// Le dernier coup est invalide
		if(cmd == '4'){
			System.out.println("Coup invalide, entrez un nouveau coup : ");
			server.sendServerCommand(console.readLine());
		}
    }
	
	public void printBoard(){
		
		int[][] board = server.getBoard();
        int boardLength  = board.length;
        System.out.print("\n********************************************\n************** Planche de Jeu **************\n********************************************\n\n");
        
        for(int i = 0 ; i < boardLength ; i++){
            for(int j = 0 ; j < boardLength ; j++){
    			System.out.print(board[i][j] + " ");
    			if(j == 7)
    				System.out.print("\n");
    		}
        }
        
        System.out.print("\n\n");
    }
	
}
