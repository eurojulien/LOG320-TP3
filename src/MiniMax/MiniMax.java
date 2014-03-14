package MiniMax;

import java.util.ArrayList;

public class MiniMax {

	// Profodeur maximale de l'arbre MiniMax par defaut
	private final static int PROFONDEUR_MAXIMALE_PERMISE_PAR_DEFAUT 	= 3;
	
	// Premiere feuille de l'arbre
	private static Feuille feuilleSouche;
	
	// Singleton
	private static MiniMax instance = null;
	
	// La profondeur maximale de l'arbre MiniMax peut etre augmentee
	// s'il y a moins de piece a calculee sur le jeu
	private static int profondeurMaximalePermise;
	
	private MiniMax(){
		
		profondeurMaximalePermise = PROFONDEUR_MAXIMALE_PERMISE_PAR_DEFAUT;
	}
	
	public static MiniMax getInstance(){
		
		if(instance == null){
			instance = new MiniMax();
		}
		
		return instance;
	}
	
	public static void setArbreProfondeurMaximale(int nouvelleProfondeurPermise){
		profondeurMaximalePermise = nouvelleProfondeurPermise;
	}
	
	// Cree un arbre MiniMax vide
	// Trouve tous les deplacement permis pour un etat du tableau de jeu
	// Creer une feuille par deplacement permis
	// Repete le traitement pour le nombre maximal de profondeur permise
	public static String construireArbre(int[][] tableauDeJeu, boolean joueurEstMAX){
		
		// Profonfeur de construction de l'arbre
		int profondeurArbre = 0;
		
		feuilleSouche = new Feuille(joueurEstMAX, "");
		construireArbre(tableauDeJeu, feuilleSouche, profondeurArbre + 1);
		
		// Retourne le meilleur coup a jouer pour cet arbre MinMax
		return feuilleSouche.getCoupJoue();
	}
	
	// Fonction recursive de construction d'arbre
	private static void construireArbre(int[][] tableauDeJeu, Feuille feuille, int profondeurArbre){
			
		// Calcul du score
		if (profondeurArbre == profondeurMaximalePermise){
			
			// Calculer score du tableauDeJeu
			// Attribuer ce score a la feuille en cours Ex : feuilleParent.setScore(score)
		}
		
		else{
			// Trouver les coups possible
			ArrayList<String> coupsPossible = new ArrayList<String>();
			
			for (String deplacement : coupsPossible){
				
				// Construction d'une feuille enfant
				Feuille feuilleEnfant = new Feuille(!feuille.isJoueurEstMAX(), deplacement);
				feuille.ajouterFeuilleEnfant(feuilleEnfant);
				construireArbre(tableauDeJeu, feuilleEnfant, profondeurArbre + 1);
				
				feuille.updateFeuilleScoreAvecMeilleurScoreEnfants();
			}
		}
	}
	
	public static void resetArbre(){
		feuilleSouche = null;
	}
}
