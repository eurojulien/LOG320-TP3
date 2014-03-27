package MiniMax;

import java.util.ArrayList;

import LinesOfActions.IA;

public class MiniMax extends Thread{

	// Profodeur maximale de l'arbre MiniMax par defaut
	// Toujours un multiple de DEUX !!
	private final static int PROFONDEUR_MAXIMALE_PERMISE_PAR_DEFAUT 	= 2;
	
	// Premiere feuille de l'arbre
	private static Feuille feuilleSouche;
	
	// La profondeur maximale de l'arbre MiniMax peut etre augmentee
	// s'il y a moins de piece a calculee sur le jeu
	private static int profondeurMaximalePermise[] 	= SyncThread.currentMaxTreeDepth;
	
	private static IA megaMind;
	
	private static int currentPlayer;
	
	private static boolean bestMoveHasBeenFound[] 	= SyncThread.bestMoveHasBeenFound;
	
	public static int nombreElagage;
	
	private static int nbFeuillesCreees;

	
	public MiniMax(){ 
		this.setPriority(Thread.NORM_PRIORITY); 
	}
	
	// Instancie l'arbre MinMax
	// Cette fonction doit etre appeler de commencer a jouer notre premier coup seulement
	public static void initaliserMinMax(int [][] tableauJeu, int numeroJoueur){
	
		MiniMax.profondeurMaximalePermise[0]	= PROFONDEUR_MAXIMALE_PERMISE_PAR_DEFAUT;
		MiniMax.megaMind						= new IA(tableauJeu, numeroJoueur);
		MiniMax.currentPlayer					= numeroJoueur;

	}
	
	public static IA getIA(){
		
		return MiniMax.megaMind;
	}
	
	// Cree un arbre MiniMax vide
	// Trouve tous les deplacement permis pour un etat du tableau de jeu
	// Creer une feuille par deplacement permis
	// Repete le traitement pour le nombre maximal de profondeur permise
	private static void construireArbre(){
		
		// Profonfeur de construction de l'arbre
		int profondeurArbre = 0;
		
		MiniMax.feuilleSouche = null;
		nombreElagage = 0;
		MiniMax.feuilleSouche = new Feuille(true, "");
		
		construireArbre(MiniMax.megaMind, MiniMax.feuilleSouche, profondeurArbre, MiniMax.feuilleSouche.getScore());
	}
	
	// Fonction recursive de construction d'arbre
	private static void construireArbre(IA nextIA, Feuille feuille, int profondeurArbre, int scoreElagage){
		
		try {
			Thread.sleep(0);
		} catch (InterruptedException e1) {
			Thread.currentThread().interrupt();
			return;
		}

		nbFeuillesCreees ++;
		
		if (profondeurArbre == profondeurMaximalePermise[0]){
		

			// Conserve les meilleurs score
			feuille.setScore(nextIA.getScoreForBoard(currentPlayer));
		}
		
		else{
			

			// Genere la liste des mouvements
			// TODO : Lance un StackOverFlow Error !
			nextIA.generateMoveList(false,0);
			
			ArrayList<String >deplacements = nextIA.getListeMouvements();

			// Securite
			// Si le calcul depasse 4500 MilliSecondes et qu'il
			// faut renvoyer un mouvement, ce mouvement sera renvoye
			if (profondeurArbre == 0){
				feuille.setCoupJoue(deplacements.get(0));
			}
			
			for (String deplacement : deplacements){
					
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
	
	// DEBUG : Affichage a la console
	public static int getScoreFromBestMove(){
		return MiniMax.feuilleSouche.getScore();
	}
	
	// DEBUG : Affichage a la console
	public static int getProfondeurArbre(){
		return MiniMax.profondeurMaximalePermise[0];
	}
	
	public static boolean bestMoveHasBeenFound(){
		return MiniMax.bestMoveHasBeenFound[0];
	}

	public static int getNbFeuillesCreees(){
		return nbFeuillesCreees;
	}
	
	@Override
	public void run() {	
	
		MiniMax.nbFeuillesCreees = 0;
		
		construireArbre();
		
		System.out.println(" ********** Arbre MiniMax termine ! ********** ");
		MiniMax.bestMoveHasBeenFound[0] = true;
	}
}
