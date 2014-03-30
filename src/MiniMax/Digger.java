package MiniMax;

import java.util.ArrayList;

import LinesOfActions.IA;

public class Digger extends Thread{

	private int treeDepth;
	private ArrayList<IA> nextIAs;
	private ArrayList<Feuille> nextLeaves; 
	private int currentPlayer;
	
	
	public Digger(ArrayList<IA> nextIAs, int treeDepth, boolean joueurEstMax, int currentPlayer){
		
		this.nextIAs 		= nextIAs;
		this.treeDepth		= treeDepth;
		this.nextLeaves		= new ArrayList<Feuille> ();
		this.currentPlayer	= currentPlayer;
		
		for(int i = 0; i < this.nextIAs.size(); i ++){
			this.nextLeaves.add(new Feuille(joueurEstMax, ""));
		}
	}
	
	public void run(){
		
		creuserBranche();
	}
	
	// Cree un arbre MiniMax vide
		// Trouve tous les deplacement permis pour un etat du tableau de jeu
		// Creer une feuille par deplacement permis
		// Repete le traitement pour le nombre maximal de profondeur permise
		private void creuserBranche(){
			
			int index = 0;
			
			for(IA megaMind : this.nextIAs){
				creuserBranche(megaMind, this.nextLeaves.get(index), treeDepth, this.nextLeaves.get(index ++).getScore());
			}
		}
		
		// Fonction recursive de construction d'arbre
		private void creuserBranche(IA nextIA, Feuille feuille, int profondeurArbre, int scoreElagage){
			
			// Si watchdog a atteint 4500 millisecondes
			// L'arbre arrete de calculer
			if(SyncThread.bestMoveHasBeenFound){
				return;
			}
			
			if (profondeurArbre < SyncThread.currentMaxTreeDepth){
				
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
				}

				// Creation des IA pour les enfants de la feuille courante
				for(String deplacement : deplacements){
					IAs.add(nextIA.notifyAndGetNewIA(deplacement));
				}
				
				if (!SyncThread.victoryOrDefautHasBeenFound){
					//new VictoryOrDefeat(IAs, MiniMax.currentPlayer, profondeurArbre+1).start();
				}
				
				int index = 0;
				for (IA ia : IAs){
						
					// Construction d'une feuille enfant
					Feuille feuilleEnfant = new Feuille(!feuille.isJoueurEstMAX(), deplacements.get(index++));
					
					// Ajout de cette feuille dans la liste des enfants de la feuille en cours
					feuille.ajouterFeuilleEnfant(feuilleEnfant);
					
					
					// Appel recursif avec la feuille enfant
					creuserBranche(ia, feuilleEnfant, profondeurArbre + 1, feuille.getScore());
					
					// ELAGAGE
					if (profondeurArbre >= 1 && scoreElagage != 0 && feuille.getScore() != 0){
						
						// MAX
						// Si la valeur de mon parent est plus petite, j'arrete de creuser
						if (feuille.isJoueurEstMAX() && feuille.getScore() >= scoreElagage){
							break;
						}
						
						// MIN
						// Si la la valeur de mon parent est plus grande, j'arrete de creuser
						else if (!feuille.isJoueurEstMAX() && feuille.getScore() <= scoreElagage){
							break;
						}
					}
				}
			
				feuille.updateFeuilleAvecMeilleurFeuilleEnfant(profondeurArbre);
			}

			// Calcul du score de la derniere feuille de l'arbre
			else if (profondeurArbre == SyncThread.currentMaxTreeDepth){
			
				// Conserve les meilleurs score
				feuille.setScore(nextIA.getScoreForBoard(this.currentPlayer));
			}	
		}

	// Retourne le score de toutes les feuilles de ce thread
	public int[] getLeavesScores(){
		
		int scores[] = new int[this.nextLeaves.size()];
		int cpt = 0;
		
		for(Feuille enfant : this.nextLeaves){
			scores[cpt++] = enfant.getScore();
		}
		
		return scores;
	}
	
}
