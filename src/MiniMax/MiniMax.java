package MiniMax;

import LinesOfActions.IA;

public class MiniMax implements Runnable{

	// Profodeur maximale de l'arbre MiniMax par defaut
	private final static int PROFONDEUR_MAXIMALE_PERMISE_PAR_DEFAUT 	= 3;
	
	// Premiere feuille de l'arbre
	private static Feuille feuilleSouche;
	
	// snapshot du plateau de jeu de la premiere feuille de l'arbre
	private static int [][] plateauJeu;
	
	// La profondeur maximale de l'arbre MiniMax peut etre augmentee
	// s'il y a moins de piece a calculee sur le jeu
	private static int profondeurMaximalePermise;
	
	private static IA megaMind;
	
	
	private MiniMax(){}
	
	// Instancie l'arbre MinMax
	// Cette fonction doit etre appeler de commencer a jouer notre premier coup seulement
	public static void initaliserMinMax(int [][] tableauJeu, int numeroJoueur){
	
		MiniMax.plateauJeu 					= tableauJeu;
		MiniMax.profondeurMaximalePermise 	= PROFONDEUR_MAXIMALE_PERMISE_PAR_DEFAUT;
		MiniMax.megaMind					= new IA(tableauJeu.clone(), numeroJoueur);
		MiniMax.feuilleSouche				= new Feuille(MiniMax.megaMind, true, "");
	}
	
	// Donne une nouvelle profondeur de recherche de l'arbre MiniMax
	public static void setArbreProfondeurMaximale(int nouvelleProfondeurPermise){
		profondeurMaximalePermise = nouvelleProfondeurPermise;
	}
	
	// Cree un arbre MiniMax vide
	// Trouve tous les deplacement permis pour un etat du tableau de jeu
	// Creer une feuille par deplacement permis
	// Repete le traitement pour le nombre maximal de profondeur permise
	public static String construireArbre(){
		
		// Profonfeur de construction de l'arbre
		int profondeurArbre = 0;
		
		construireArbre(MiniMax.plateauJeu, MiniMax.feuilleSouche, profondeurArbre + 1);
		
		// Retourne le meilleur coup a jouer pour cet arbre MinMax
		return feuilleSouche.getCoupJoue();
	}
	
	// Fonction recursive de construction d'arbre
	private static void construireArbre(int[][] tableauDeJeu, Feuille feuille, int profondeurArbre){
		
		// Calcul du score
		if (profondeurArbre == profondeurMaximalePermise){
			
			feuille.getIA().generateMoveList();
			feuille.setCoupJoue(feuille.getIA().getBestMove());
			
			// Calculer score du tableauDeJeu
			// Attribuer ce score a la feuille en cours Ex : feuilleParent.setScore(score)
		}
		
		else{
			
			// IA
			IA newIA = null;
			
			if (feuille.isJoueurEstMAX()) {newIA = new IA(tableauDeJeu, 2);}
			else {newIA = new IA(tableauDeJeu, 2);}
			 
			newIA.generateMoveList();
			
			for (String deplacement : newIA.getListeMouvements()){
				
				// Construction d'une feuille enfant
				Feuille feuilleEnfant = new Feuille(newIA, !feuille.isJoueurEstMAX(), deplacement);
				feuille.ajouterFeuilleEnfant(feuilleEnfant);
				construireArbre(tableauDeJeu.clone(), feuilleEnfant, profondeurArbre + 1);
				
				feuille.updateFeuilleScoreAvecMeilleurScoreEnfants();
			}
		}
	}
	
	public static void resetArbre(){
		feuilleSouche = null;
	}
	
	public 

	@Override
	public void run() {
		
		// TODO Auto-generated method stub
		construireArbre();
		
	}
}
