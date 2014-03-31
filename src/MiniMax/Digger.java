package MiniMax;

import java.util.ArrayList;

import com.sun.corba.se.impl.orbutil.concurrent.Sync;

import LinesOfActions.IA;

public class Digger extends Thread{

	private int treeDepth;
	private IA masterMind = null;
	private ArrayList<Feuille> nextLeaves;
	private ArrayList<String> deplacements;
	private int currentPlayer;
	private boolean joueurEstMax;
	
	
	public Digger(){
		
		this.nextLeaves		= new ArrayList<Feuille>();
		this.deplacements	= new ArrayList<String>();
	
		this.setPriority(NORM_PRIORITY);
		
	}
	
	public void run(){
		
		creuserBranche();
	}
	
	private void creuserBranche(){
	
		// En attente d'etre arrete ...
		while(SyncThread.keepDiggerAlive){
	
			// Attend du travail
			do{
				try {
					Thread.sleep(SyncThread.WAITING_STEP_TIME);
				} catch (InterruptedException e) {}
			}while(!SyncThread.minMaxIsReadyToBeDigged);
		
			// Destruction des branche
			this.nextLeaves.clear();
			
			// Execute le travail
			for(String mouvement : this.deplacements){
				
				Feuille feuille = new Feuille(this.joueurEstMax, mouvement);
				creuserBranche(masterMind.notifyAndGetNewIA(mouvement), feuille , treeDepth, 0);
				this.nextLeaves.add(feuille);
				
				if(SyncThread.computationTimeIsFinished){
					break;
				}
			}
			
			// Vidange des anciens mouvements
			this.deplacements.clear();

			// Travail termine
			SyncThread.diggersAreDone ++;
			
			SyncThread.minMaxIsReadyToBeDigged = false;
		}
		
	}
	
	// Fonction recursive de construction d'arbre
	private void creuserBranche(IA nextIA, Feuille feuille, int profondeurArbre, int scoreElagage){
		
		if(SyncThread.computationTimeIsFinished){
			return;
		}
		
		if (profondeurArbre < SyncThread.currentMaxTreeDepth){
			
			// Genere la liste des mouvements
			// TODO : Lance un StackOverFlow Error !
			nextIA.generateMoveList(false,0);
			
			// Deplacements
			ArrayList<String> deplacements	= nextIA.getListeMouvements();
			ArrayList<IA> IAs				= new ArrayList<IA>();

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
	
	public void AddMoveToDig(String mouvement){
		this.deplacements.add(mouvement);
	}
	
	public void setDiggerToDig(IA ia, int treeDepth, int currentPlayer){
		this.masterMind 	= ia;
		this.treeDepth		= treeDepth;
		this.currentPlayer	= currentPlayer;
	}
	
	public ArrayList<Feuille> getFoundLeaves(){
		return this.nextLeaves;
	}
	
}
