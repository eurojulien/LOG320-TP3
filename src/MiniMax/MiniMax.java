package MiniMax;

import java.util.ArrayList;

import LinesOfActions.IA;

public class MiniMax extends Thread{

	// Profodeur maximale de l'arbre MiniMax par defaut
	// Toujours un nombre impair pour que les avant dernieres feuilles soient MAX
	private final static int PROFONDEUR_MAXIMALE_PERMISE_PAR_DEFAUT 	= 1;
	
	// Premiere feuille de l'arbre
	private static Feuille feuilleSouche;

    private static String failSafe = "";
	
	private static IA megaMind;
	
	private static int currentPlayer;

    public static boolean foundVictoryOrDefeat = false;

	public MiniMax(){ 
		this.setPriority(Thread.NORM_PRIORITY); 
	}
	
	// Instancie l'arbre MinMax
	// Cette fonction doit etre appeler de commencer a jouer notre premier coup seulement
	public static void initaliserMinMax(int [][] tableauJeu, int numeroJoueur){
		SyncThread.currentMaxTreeDepth[0]		= PROFONDEUR_MAXIMALE_PERMISE_PAR_DEFAUT;
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

		// Profondeur actuelle de l'arbre
		int profondeurActuelleArbre = 0;
        SyncThread.bestMoveHasBeenFound[0] = false;
		
		// ReInitialisation de la feuille souche
		MiniMax.feuilleSouche = null;
		MiniMax.feuilleSouche = new Feuille("", megaMind,0,null);

		// Fonction de recursivite de construction d'arbre
		construireArbre(MiniMax.megaMind, MiniMax.feuilleSouche, profondeurActuelleArbre, MiniMax.feuilleSouche.getScore());
	}
	
	// Fonction recursive de construction d'arbre
	private static void construireArbre(IA nextIA, Feuille feuille, int profondeurArbre, int scoreElagage){
		
		// Si watchdog a atteint 4500 millisecondes
		// L'arbre arrete de calculer
		if(SyncThread.bestMoveHasBeenFound[0]){
			return;
		}

		if (profondeurArbre < SyncThread.currentMaxTreeDepth[0]){
			
			// Genere la liste des mouvements
			// TODO : Lance un StackOverFlow Error !
			nextIA.generateMoveList(false,0);
			
			// Deplacements
			ArrayList<String> deplacements	= nextIA.getListeMouvements();
			ArrayList<IA> IAs				= new ArrayList<IA>();
			// Securite
			// Si le calcul depasse 4500 MilliSecondes et qu'il
			// faut renvoyer un mouvement, ce mouvement sera renvoye
			if (profondeurArbre == 0){
				feuille.setCoupJoue(deplacements.get(0));
                failSafe = deplacements.get(0);
			}

			// Creation des IA pour les enfants de la feuille courante
			for(String deplacement : deplacements){
				IAs.add(nextIA.notifyAndGetNewIA(deplacement));
			}
			
			int index = 0;
			for (IA ia : IAs){
				// Construction d'une feuille enfant
                String premierCoupJouerBranche = "";

                if(profondeurArbre == 1){
                    // on veut conserver l'information du coup "primaire"
                    premierCoupJouerBranche = ia.coupJouer;
                }else{
                    premierCoupJouerBranche = nextIA.coupJouer;
                }

				Feuille feuilleEnfant = new Feuille(deplacements.get(index++),ia,profondeurArbre,premierCoupJouerBranche);

				// Ajout de cette feuille dans la liste des enfants de la feuille en cours
				feuille.ajouterFeuilleEnfant(feuilleEnfant);


                // Appel recursif avec la feuille enfant
                construireArbre(feuilleEnfant.mindForFeuille, feuilleEnfant, profondeurArbre + 1, feuille.getScore());


                // Mis a jour de la feuille en cours avec le meilleur score de ses enfants
                feuille.updateFeuilleAvecMeilleurFeuilleEnfant(profondeurArbre,currentPlayer);


                if(!foundVictoryOrDefeat){
                    // ELAGAGE
                    if (profondeurArbre >= 1 && scoreElagage != 0 && feuille.getScore() != 0){
                        // MAX
                        // Si la valeur de mon parent est plus petite, j'arrete de creuser
                        if (feuille.isJoueurEstMAX(profondeurArbre) && feuille.getScore() >= scoreElagage){
                            break;
                        }

                        // MIN
                        // Si la la valeur de mon parent est plus grande, j'arrete de creuser
                        else if (!feuille.isJoueurEstMAX(profondeurArbre) && scoreElagage != 0  && feuille.getScore() <= scoreElagage){
                            break;
                        }

                    }
                }
			}

		}

		// Calcul du score de la derniere feuille de l'arbre
		else if (profondeurArbre == SyncThread.currentMaxTreeDepth[0]){

			// Conserve les meilleurs score
			feuille.setScore(nextIA.getScoreForBoard(currentPlayer));
		}

	}

	public static String getBestMove(){
        if(feuilleSouche.getCoupJoue().equals("")){
            System.out.println(" = Failsafe = ");
            return failSafe;
        }
		return MiniMax.feuilleSouche.getCoupJoue();
	}
	
	// DEBUG : Affichage a la console
	public static int getScoreFromBestMove(){
		return MiniMax.feuilleSouche.getScore();
	}
	
	// DEBUG : Affichage a la console
	public static int getProfondeurArbre(){
		return SyncThread.currentMaxTreeDepth[0];
	}
	
	public static boolean bestMoveHasBeenFound(){
		return SyncThread.bestMoveHasBeenFound[0];
	}
	
	@Override
	public void run() {	
		construireArbre();
		//System.out.println(" ********** Arbre MiniMax termine ! ********** ");
		SyncThread.bestMoveHasBeenFound[0] = true;
	}
}
