package MiniMax;

import java.util.ArrayList;

import LinesOfActions.IA;

public class MiniMax extends Thread{

	// Profodeur maximale de l'arbre MiniMax par defaut
	// Toujours un nombre impair pour que les avant dernieres feuilles soient MAX
	private final static int PROFONDEUR_MAXIMALE_PERMISE_PAR_DEFAUT 	= 1;
	private final static int MAX_NUMBER_OF_SIMULATNEOUS_THREAD			= 4;
	
	// Threads de l'arbre MinMax
	private static Digger diggers[];
	
	// Premiere feuille de l'arbre
	private static Feuille feuilleSouche;
	
	private static IA megaMind;
	
	private static int currentPlayer;

	public MiniMax(){ 
		this.setPriority(Thread.MAX_PRIORITY); 
	}
	
	// Instancie l'arbre MinMax
	// Cette fonction doit etre appeler de commencer a jouer notre premier coup seulement
	public static void initaliserMinMax(int [][] tableauJeu, int numeroJoueur){
	
		SyncThread.currentMaxTreeDepth			= PROFONDEUR_MAXIMALE_PERMISE_PAR_DEFAUT;
		MiniMax.megaMind						= new IA(tableauJeu, numeroJoueur);
		MiniMax.currentPlayer					= numeroJoueur;
		diggers									= new Digger[SyncThread.ALL_DIGGERS_ARE_DONE];
		
		// Creation des creuseurs
		for (int i = 0; i < SyncThread.ALL_DIGGERS_ARE_DONE; i ++){
			diggers[i] = new Digger();
		}
		
		// Demarrage des diggers
		for(Digger dig : diggers){
			dig.start();
		}
		
		// Thread victoire ou defaite
		VictoryOrDefeat.getInstance().start();
	}
	
	public static IA getIA(){
		
		return MiniMax.megaMind;
	}
	
	// Cree un arbre MiniMax vide
	// Trouve tous les deplacement permis pour un etat du tableau de jeu
	// Creer une feuille par deplacement permis
	// Repete le traitement pour le nombre maximal de profondeur permise
	private static void construireArbre(){
		
		long startTime = System.nanoTime();
		
		// Profondeur actuelle de l'arbre
		int profondeurActuelleArbre = 0;
				
		// ReInitialisation de la feuille souche
		MiniMax.feuilleSouche = null;
		MiniMax.feuilleSouche = new Feuille(true, "");
		
		// Preparation des creuseur
		SyncThread.diggersAreDone = 0;
		
		// Generation des mouvements (Feuille Racine)
		MiniMax.megaMind.generateMoveList(false, 0);
		ArrayList<String> deplacements	= MiniMax.megaMind.getListeMouvements();
		
		// Assignation du IA de base
		for (Digger dig : diggers){
			dig.setDiggerToDig(MiniMax.megaMind, 1, MiniMax.currentPlayer);
		}
		// Assignation des mouvements a verifier
		for (int cpt = 0; cpt < deplacements.size(); cpt ++){
			diggers[cpt % SyncThread.ALL_DIGGERS_ARE_DONE].AddMoveToDig(deplacements.get(cpt));
		}
		
		// Demarrage des diggers
		SyncThread.minMaxIsReadyToBeDigged = true;
		
		// Atente que les diggers aient fini
		do{
			try {
				Thread.sleep(SyncThread.WAITING_STEP_TIME);
			} catch (InterruptedException e) {}
		}while(SyncThread.diggersAreDone != SyncThread.ALL_DIGGERS_ARE_DONE);
		
		// Unification de toutes les feuilles souches des threads
		for(Digger dig : diggers){
			MiniMax.feuilleSouche.ajouterFeuilleEnfants(dig.getFoundLeaves());
		}
		
		// Meilleur score trouve
		MiniMax.feuilleSouche.updateFeuilleAvecMeilleurFeuilleEnfant(0);
		
		long endTime = System.nanoTime();
		System.out.println("Diggers time		: " + (endTime - startTime)/(1000000) + " milliseconds");
	}

	public static String getBestMove(){
		return MiniMax.feuilleSouche.getCoupJoue();
	}
	
	// DEBUG : Affichage a la console
	public static int getScoreFromBestMove(){
		return MiniMax.feuilleSouche.getScore();
	}
	
	// DEBUG : Affichage a la console
	public static int getProfondeurArbre(){
		return SyncThread.currentMaxTreeDepth;
	}
	
	public static boolean bestMoveHasBeenFound(){
		return SyncThread.bestMoveHasBeenFound;
	}
	
	@Override
	public void run() {	
		construireArbre();
		
		SyncThread.bestMoveHasBeenFound = true;
		SyncThread.flushVictoryOrDefeat	= true;
		System.out.println(" ********** Arbre MiniMax termine ! ********** ");
	}
}
