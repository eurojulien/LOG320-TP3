package LinesOfActions;
import MiniMax.*;

public class Main{

	// Couleur des joueurs
	private static final int BLACK = 2;
	private static final int WHITE = 4;
	
	// Numero des joueurs
	private static final char FIRST_PLAYER = '1';
	private static final char SECOND_PLAYER = '2';
	
	private static Main instance = null;
	private static ServerConnect server;
	private static int playerColor = 0;
	private static long startTime = 0;
	private static long endTime = 0;
	
	private Main(){

		server = new ServerConnect();
	}
	
	public static Main CreateConnection(){
		
		if(instance == null){
			instance = new Main();
		}
		
		return instance;
		
	}
	
	// Thread Principal
	public void LancerJeu(){
		
		// Reception de la couleur de nos pions
		// Les blancs jouent en premier
		// Les noirs attendent une reponse du serveur avant de commencer a jouer
		switch( server.getServerCommand())
		{
			case FIRST_PLAYER: playerColor = WHITE;
					break;
					
			case SECOND_PLAYER: playerColor = BLACK;
					break;
		}

		MiniMax.initaliserMinMax(server.getBoardSetup(), playerColor);
		
		if(playerColor == WHITE){

			new WatchDog().start();
			
			// Attente de calcul de l'arbre MinMax
			do{
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {}
			} while(!MiniMax.bestMoveHasBeenFound());
			
			server.sendServerCommand(MiniMax.getBestMove());
			MiniMax.getIA().notifyMovementMyTeam(MiniMax.getBestMove());
		}
		
		do{
			
			// Reception de la reponse du serveur
			server.getServerCommand();
			
			// Demarrage du timer
			startTime = System.nanoTime();
			
			MiniMax.getIA().notifyMovementEnemyTeam(server.getLastTurn().trim());
			new WatchDog().start();
			// TODO : Attente de 4500 millisecondes, temps maximum alloue a Minimax
			// pour generer un arbre
			// Cette attente devrait etre levee quand la classe IA le permet. Durant le temps
			// de traitement de IA, il nous est possible de faire d'autre changements aussi.
			
			// Attente de calcul de l'arbre MinMax
			do{
				try {
					Thread.sleep(SyncThread.WAITING_STEP_TIME);
				} catch (InterruptedException e) {}
			} while(!MiniMax.bestMoveHasBeenFound());
			
			endTime = System.nanoTime();
			
			server.sendServerCommand(MiniMax.getBestMove());
			MiniMax.getIA().notifyMovementMyTeam(MiniMax.getBestMove());
			
			// DEBUG
			System.out.println("Mouvement		: " + MiniMax.getBestMove());
			System.out.println("Score			: " + MiniMax.getScoreFromBestMove());		
			System.out.println("Temps de calcul		: " + (endTime - startTime)/(1000000) + " milliseconds");
			System.out.println("Profondeur Arbre	: " + MiniMax.getProfondeurArbre());
			
		}while(true);
	}
	
	public static void main(String[] args) {
	
		// Connexion au serveur
		Main thread = Main.CreateConnection();
		
		// Lancement du thread principal
		thread.LancerJeu();
	}
}
	
