package LinesOfActions;

public class Main extends Thread{

	private static final int BLACK = 2;
	private static final int WHITE = 4;
	
	private static Main instance = null;
	private static ServerConnect server;
	private static BoardInOut controller = null;
	private static IA megaMind = null;
	private static int playerColor = 0;
	
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
		// Les noirs jouent en premier
		// Les blancs attendent une reponse du serveur avant de commencer a jouer
		switch( server.getServerCommand())
		{
			case 1: playerColor = BLACK;
					break;
					
			case 2: playerColor = WHITE;
					break;
		}
		
		// Initalisation du plateau
		megaMind = new IA(server.getBoardSetup(), playerColor);
		
		if(playerColor == BLACK){
			
			megaMind.run();
			server.sendServerCommand(megaMind.getMove());
			megaMind.notifyMovementMyTeam(megaMind.getMove());
		}
		
		
		// TODO : while true temporaire
		// La condition de sortie de cette boucle est
		// - Partie gagnee
		// - Partie Perdue
		// - Partie Abandonnee
		do{
			
			// Reception de la reponse du serveur
			megaMind.notifyMovementEnemyTeam(server.getLastTurn());
			
			// Arbre MiniMax
			megaMind.run();
			
			// Envoie de la reponse
			server.sendServerCommand(megaMind.getMove());
			megaMind.notifyMovementMyTeam(megaMind.getMove());
			
			
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
	
