package MiniMax;

import LinesOfActions.IA;

public class MiniMax implements Runnable{

	// Profodeur maximale de l'arbre MiniMax par defaut
	private final static int PROFONDEUR_MAXIMALE_PERMISE_PAR_DEFAUT 	= 1;
	
	// Premiere feuille de l'arbre
	private static Feuille feuilleSouche;
	
	// La profondeur maximale de l'arbre MiniMax peut etre augmentee
	// s'il y a moins de piece a calculee sur le jeu
	private static int profondeurMaximalePermise;
	
	private static IA megaMind;
	
	private static int currentPlayer;
	
	private static MiniMax occurence = null;
	
	private MiniMax(){}
	
	// Instancie l'arbre MinMax
	// Cette fonction doit etre appeler de commencer a jouer notre premier coup seulement
	public static MiniMax initaliserMinMax(int [][] tableauJeu, int numeroJoueur){
	
		if(occurence == null) { occurence = new MiniMax();}
		
		MiniMax.profondeurMaximalePermise 	= PROFONDEUR_MAXIMALE_PERMISE_PAR_DEFAUT;
		MiniMax.megaMind					= new IA(tableauJeu, numeroJoueur);
		MiniMax.currentPlayer				= numeroJoueur;
		
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
		
		MiniMax.resetArbre();
		MiniMax.feuilleSouche = new Feuille(true, "");
		
		construireArbre(MiniMax.megaMind, MiniMax.feuilleSouche, profondeurArbre, MiniMax.feuilleSouche.getScore());
	}
	
	// Fonction recursive de construction d'arbre
	private static void construireArbre(IA nextIA, Feuille feuille, int profondeurArbre, int scoreElagage){
		
		// Calcul du score
		if (profondeurArbre == profondeurMaximalePermise){

			// Genere la list des mouvements
			nextIA.generateMoveList(true);
			
			// Conserve les meilleurs score
			feuille.setScore(nextIA.getMeilleurScore());

		}		
		else{
			
			// Genere la list des mouvements
			nextIA.generateMoveList(false);
						
			for (String deplacement : nextIA.getListeMouvements()){

				// Construction d'une feuille enfant
				Feuille feuilleEnfant = new Feuille(!feuille.isJoueurEstMAX(), deplacement);
				
				// Ajout de cette feuille dans la liste des enfants de la feuille en cours
				feuille.ajouterFeuilleEnfant(feuilleEnfant);
				
				// Appel recursif avec la feuille enfant
				construireArbre(nextIA.notifyAndGetNewIA(deplacement), feuilleEnfant, profondeurArbre + 1, feuille.getScore());
			}
			
			// Mis a jour de la feuille en cours avec le meilleur score de ses enfants
			feuille.updateFeuilleAvecMeilleurFeuilleEnfant();
		}
	}
	
	public static void resetArbre(){
		feuilleSouche = null;
	}
	
	public static String getBestMove(){
		return MiniMax.feuilleSouche.getCoupJoue();
	}

	@Override
	public void run() {
		
		// TODO Auto-generated method stub
		construireArbre();
		
	}
}
