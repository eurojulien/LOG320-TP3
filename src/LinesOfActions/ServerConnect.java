package LinesOfActions;
import java.io.*;
import java.net.*;


class ServerConnect {
	public static void main(String[] args) {

        // partie debug alex
        int[][] board = new int[8][8];
        board[0] = new int[] {0,2,2,2,2,2,2,0};
        board[1] = new int[] {4,0,0,0,0,0,0,4};
        board[2] = new int[] {4,0,0,0,0,0,0,4};
        board[3] = new int[] {4,0,0,0,0,0,0,4};
        board[4] = new int[] {4,0,0,0,0,0,0,4};
        board[5] = new int[] {4,0,0,0,0,0,0,4};
        board[6] = new int[] {4,0,0,0,0,0,0,4};
        board[7] = new int[] {0,2,2,2,2,2,2,0};

        IA boardSolver = new IA(board,4);
        boardSolver.obtainMove();
        boardSolver.drawBoard(true);
        //boardSolver.notifyMovementMyTeam("A3 - C3");
        //boardSolver.notifyMovementEnemyTeam("D1 - D3");
        //boardSolver.drawBoard(true);
    /*
	Socket MyClient;
	BufferedInputStream input;
	BufferedOutputStream output;
    int[][] board = new int[8][8];

	try {
		MyClient = new Socket("localhost", 8888);
	   	input    = new BufferedInputStream(MyClient.getInputStream());
		output   = new BufferedOutputStream(MyClient.getOutputStream());
		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

		
		
		
	   	while(true){
			char cmd = 0;

            cmd = (char)input.read();

            // Debut de la partie en joueur blanc
            if(cmd == '1'){
                byte[] aBuffer = new byte[1024];
				
				int size = input.available();
				//System.out.println("size " + size);
				input.read(aBuffer,0,size);
                String s = new String(aBuffer).trim();
                String[] boardValues;
                boardValues = s.split(" ");
                
                int x=0,y=0;
                for(int i=0; i<boardValues.length;i++){
                    board[x][y] = Integer.parseInt(boardValues[i]);
                    x++;
                    if(x == 8){
                        x = 0;
                        y++;
                    }
                }


                
                System.out.println("Nouvelle partie! Vous jouer blanc, entrez votre premier coup : ");
                String move = null;
                move = console.readLine();
				output.write(move.getBytes(),0,move.length());
				output.flush();
            }
            
            // Debut de la partie en joueur Noir
            if(cmd == '2'){
                System.out.println("Nouvelle partie! Vous jouer noir, attendez le coup des blancs");
                byte[] aBuffer = new byte[1024];
				
				int size = input.available();
				//System.out.println("size " + size);
				input.read(aBuffer,0,size);
                String s = new String(aBuffer).trim();
                String[] boardValues;
                boardValues = s.split(" ");
                int x=0,y=0;
                for(int i=0; i<boardValues.length;i++){
                    board[x][y] = Integer.parseInt(boardValues[i]);
                    x++;
                    if(x == 8){
                        x = 0;
                        y++;
                    }
                }
            }


			// Le serveur demande le prochain coup
			// Le message contient aussi le dernier coup joue.
			if(cmd == '3'){
				byte[] aBuffer = new byte[16];
				
				int size = input.available();
				//System.out.println("size " + size);
				input.read(aBuffer,0,size);
				
				String s = new String(aBuffer);
				System.out.println("Dernier coup : "+ s);
		       	System.out.println("Entrez votre coup : ");
				String move = null;
				move = console.readLine();
				output.write(move.getBytes(),0,move.length());
				output.flush();
				
			}
			// Le dernier coup est invalide
			if(cmd == '4'){
				System.out.println("Coup invalide, entrez un nouveau coup : ");
		       	String move = null;
				move = console.readLine();
				output.write(move.getBytes(),0,move.length());
				output.flush();
				
			}
        }
	}
	catch (IOException e) {
   		System.out.println(e);
	}
*/
    }


}
