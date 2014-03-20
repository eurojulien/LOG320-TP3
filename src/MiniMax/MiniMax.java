package MiniMax;

import java.util.ArrayList;

import LinesOfActions.IA;

public class MiniMax implements Runnable{

	// Profodeur maximale de l'arbre MiniMax par defaut
	// Toujours un multiple de DEUX !!
	private final static int PROFONDEUR_MAXIMALE_PERMISE_PAR_DEFAUT 	= 2;
	
	// Premiere feuille de l'arbre
	private static Feuille feuilleSouche;
	
	// La profondeur maximale de l'arbre MiniMax peut etre augmentee
	// s'il y a moins de piece a calculee sur le jeu
	private static int profondeurMaximalePermise;
	
	private static IA megaMind;
	
	private static int currentPlayer;
	
	private static MiniMax occurence = null;
	
	private static boolean bestMoveHasBeenFound[] = {false};
	
	private static WatchDog watchDog;
	
	public static int nombreElagage;
	
	private MiniMax(){}
	
	// Instancie l'arbre MinMax
	// Cette fonction doit etre appeler de commencer a jouer notre premier coup seulement
	public static MiniMax initaliserMinMax(int [][] tableauJeu, int numeroJoueur){
	
		if(occurence == null) { occurence = new MiniMax();}
		
		MiniMax.profondeurMaximalePermise 	= PROFONDEUR_MAXIMALE_PERMISE_PAR_DEFAUT;
		MiniMax.megaMind					= new IA(tableauJeu, numeroJoueur);
		MiniMax.currentPlayer				= numeroJoueur;
		watchDog							= occurence.new WatchDog(bestMoveHasBeenFound);
		
		return occurence;
	}
	
	public static IA getIA(){
		
		return MiniMax.megaMind;
	}
	
	// Donne une nouvelle profondeur de recherche de l'arbre MiniMax
	public static void setArbreProfondeurMaximale(int nouvelleProfondeurPermise){
		profondeurMaximalePermise = nouvelleProfondeurPermise;
	}
	
	// Cree un arbre MiniMax vide
	// Trouve tous les deplacement permis pour un etat du tableau de jeu
	// Creer une feuille par deplacement permis
	// Repete le traitement pour le nombre maximal de profondeur permise
	public static void construireArbre(){
		
		// Profonfeur de construction de l'arbre
		int profondeurArbre = 0;
		
		MiniMax.feuilleSouche = null;
		nombreElagage = 0;
		MiniMax.feuilleSouche = new Feuille(true, "");
		
		construireArbre(MiniMax.megaMind, MiniMax.feuilleSouche, profondeurArbre, MiniMax.feuilleSouche.getScore());
	}
	
	// Fonction recursive de construction d'arbre
	private static void construireArbre(IA nextIA, Feuille feuille, int profondeurArbre, int scoreElagage){
		
		// Calcul du score
		if (profondeurArbre == profondeurMaximalePermise){

			// Conserve les meilleurs score
			feuille.setScore(nextIA.getScoreForBoard());

		}		
		else{
			
			// Genere la list des mouvements
			nextIA.generateMoveList(false);
			
			ArrayList<String >deplacements = nextIA.getListeMouvements();

			for (String deplacement : deplacements){
			
				if (profondeurArbre == 0){
					//System.out.println(" 0 : " + deplacement);
				}
					
				// Construction d'une feuille enfant
				Feuille feuilleEnfant = new Feuille(!feuille.isJoueurEstMAX(), deplacement);
				
				// Ajout de cette feuille dans la liste des enfants de la feuille en cours
				feuille.ajouterFeuilleEnfant(feuilleEnfant);
				
				// Appel recursif avec la feuille enfant
				construireArbre(nextIA.notifyAndGetNewIA(deplacement), feuilleEnfant, profondeurArbre + 1, feuille.getScore());
			
				// Mis a jour de la feuille en cours avec le meilleur score de ses enfants
				feuille.updateFeuilleAvecMeilleurFeuilleEnfant(profondeurArbre);
			
				// ELAGAGE
				if (profondeurArbre >= 1 && scoreElagage != 0 && feuille.getScore() != 0){
					
					// MAX
					// Si la valeur de mon parent est plus petite, j'arrete de creuser
					if (feuille.isJoueurEstMAX() && feuille.getScore() >= scoreElagage){
						nombreElagage ++;
						break;
					}
					
					// MIN
					// Si la la valeur de mon parent est plus grande, j'arrete de creuser
					else if (feuille.getScore() <= scoreElagage){
						nombreElagage ++;
						break;
					}
				}
			}
		}
	}
	
	public static void resetArbre(){
		feuilleSouche = null;
	}
	
	public static String getBestMove(){
		return MiniMax.feuilleSouche.getCoupJoue();
	}
	
	public static boolean bestMoveHasBeenFound(){
		return MiniMax.bestMoveHasBeenFound[0];
	}

	@Override
	public void run() {
		
		MiniMax.bestMoveHasBeenFound[0] = false;
		MiniMax.watchDog.run();
		
		// TODO Auto-generated method stub
		construireArbre();
		
	}
	
	private class WatchDog implements Runnable{

		private static final int MILLISECONDS_BEFORE_WAKE_THE_DOG = 4500;
		private boolean watchDog[];
		
		public WatchDog(boolean watch[]){
			watchDog = watch;
		}
		
		@Override
		public void run() {
			
			boolean miniMaxIsOk = false;
			int waiting = 0;
			
			do{
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {}
				
				waiting += 100;
				
				if (watchDog[0]){
					miniMaxIsOk = true;
				}
				
			}while(!miniMaxIsOk && waiting < MILLISECONDS_BEFORE_WAKE_THE_DOG);
			
			
			if (!miniMaxIsOk) {this.watchDog[0] = true;}
			
		}
		
	}
}
