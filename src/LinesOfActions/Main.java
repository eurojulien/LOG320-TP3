package LinesOfActions;
import MiniMax.MiniMax;

public class Main extends Thread{

	private static final int BLACK = 4;
	private static final int WHITE = 2;
	
	private static Main instance = null;
	private static ServerConnect server;
	private static BoardInOut controller = null;
	private static IA megaMind = null;
	private static MiniMax miniMax;
	private static int playerColor = 0;
	
	private static String theirLastMove = "";
	private static String ourLastMove	= "";
	
	private Main(){

		server = new ServerConnect();
	}
	
	public static Main CreateThread(){
		
		if(instance == null){
			instance = new Main();
		}
		
		return instance;
		
	}
	
	// Thread Principal
	public void run(){
		
		// Reception de la couleur de nos pions
		// Les blancs jouent en premier
		// Les noirs attendent une reponse du serveur avant de commencer a jouer
		switch( server.getServerCommand())
		{
			case '1': playerColor = WHITE;
					break;
					
			case '2': playerColor = BLACK;
					break;
		}
		
		// Initalisation du plateau
		int[][] board = server.getBoardSetup().clone();
		//megaMind 	= new IA(board, playerColor);
		miniMax 	= MiniMax.getInstance(board, playerColor);
		
		if(playerColor == WHITE){
			
			//megaMind.run();
			//server.sendServerCommand(megaMind.getBestMove());
			//megaMind.notifyMovementMyTeam(megaMind.getBestMove());
			
			miniMax.run();
			server.sendServerCommand(miniMax.getBestMove());
			miniMax.getIA().notifyMovementMyTeam(miniMax.getBestMove());
		}
		
		// TODO : while true temporaire
		// La condition de sortie de cette boucle est
		// - Partie gagnee
		// - Partie Perdue
		// - Partie Abandonnee
		do{
			
			//MiniMax.getIA().drawBoard(false);
			
			// Reception de la reponse du serveur
			server.getServerCommand();
			
			//megaMind.notifyMovementEnemyTeam(server.getLastTurn().trim());
			//megaMind.run();
			
			MiniMax.getIA().notifyMovementEnemyTeam(server.getLastTurn().trim());
			miniMax.run();
			
			// TODO : Attente de 4500 millisecondes, temps maximum alloue a Minimax
			// pour generer un arbre
			// Cette attente devrait etre levee quand la classe IA le permet. Durant le temps
			// de traitement de IA, il nous est possible de faire d'autre changements aussi.
			try {
				Thread.sleep(4500);
			} catch (InterruptedException e) {
				// TODO  Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				//megaMind.wait();
				miniMax.wait();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			
			// Envoie de la reponse
			//server.sendServerCommand(megaMind.getBestMove());
			//megaMind.notifyMovementMyTeam(megaMind.getBestMove());

            String move = MiniMax.getBestMove();
            System.out.println("We sent :" + move);
			server.sendServerCommand(move);
			MiniMax.getIA().notifyMovementMyTeam(move);
			
			// TODO : Traitement supplementaire lorsque l'adversaire joue
			
		}while(true);
	}
	
	public static void main(String[] args) {
	
		// Connexion au serveur
		Main thread = Main.CreateThread();
		
		// Lancement du thread principal
		thread.run();
	}
}
	
