package MiniMax;

import java.util.ArrayList;

import LinesOfActions.IA;

/**
 * 
 * @author julien
 *
 * Classe qui represente un coup joue sur le tableau de jeu
 */
public class Feuille {

	// Pointeur sur la feuille parent
	private ArrayList <Feuille> feuilleEnfants;
	public IA mindForFeuille;

    public static final int VICTORY		= 1;
    public static final int DEFEAT		= -1;
    public static final int NOTHING		= 0;

	// Vrai : Notre Pion
	// Faux : Pion adverse
    private static int compteurProfondeur = -1;
	private int score;
    private int scoreWinLose = 0;
    public int profondeurArbre;
	
	// Moyenne du score des enfants
	private int moyenneScoreEnfants;
	
	// Coup joue pour atteindre cette feuille
	private String coupJoue;
    private String premierCoupJouer;
	
	// Constructeur pour creer une feuille dans l'arbre MiniMax
	public Feuille(String coupJoue, IA mindForFeuille, int profondeurArbre, String premierCoupJouer){

        this.premierCoupJouer = premierCoupJouer;
        this.mindForFeuille = mindForFeuille;
		this.feuilleEnfants = new ArrayList<Feuille>();
		this.score = 0;
		this.coupJoue = coupJoue;
		this.profondeurArbre=profondeurArbre;
		this.moyenneScoreEnfants = 0;
	}
	
	public void ajouterFeuilleEnfant(Feuille feuilleEnfant){
		this.feuilleEnfants.add(feuilleEnfant);
	}

    private void findWinLoseConditions(int playerToScore){
        int victoryOrDefeat = 0;
        victoryOrDefeat = mindForFeuille.findMateThreat(playerToScore);
        //todo : mettre breakpoints ici julien
        if(victoryOrDefeat == VICTORY || victoryOrDefeat == DEFEAT){
            if(victoryOrDefeat == VICTORY){
                //System.out.println("Victory detected! | Niveau Arbre : " + profondeurArbre);
                MiniMax.foundVictoryOrDefeat = true;
                setScoreWinLose(1000);
            }
            if(victoryOrDefeat == DEFEAT)
            {
                MiniMax.foundVictoryOrDefeat = true;
                //System.out.println("Defeat detected! | Niveau Arbre : " + profondeurArbre);
                setScoreWinLose(-1000);
            }
        }
    }

	// Attribue le score a cette feuille selon le meilleur score
	// des enfants de cette feuille
	// Parametre profondeur : Si egal a zero (Feuille parent), la feuille parent
	// copie le score ET le mouvement relie a ce score
	public void updateFeuilleAvecMeilleurFeuilleEnfant(int profondeur, int playerToScore){
		
		Feuille feuilleAComparer = new Feuille("",null,profondeur,this.premierCoupJouer);

        if(compteurProfondeur == -1){
            compteurProfondeur = profondeur % 2;
        }

        findWinLoseConditions(playerToScore);

		this.moyenneScoreEnfants	= 0;
		
		// La condition est en dehors de la boucle.
		// Cela oblige a avoir deux boucles, mais moins
		// de comparaison.


        int compareScore = 0;
        if (isJoueurEstMAX(profondeur)) {
            compareScore=  (-10000);
        }else{
            compareScore=  (10000);
        }

		// Conserve le plus grand score possible
		if(isJoueurEstMAX(profondeur)){
		
			for (Feuille enfant : this.feuilleEnfants){
				/*if(profondeur==0){
                    System.out.println("Move jouer : " + enfant.getCoupJoue() + ":  " + enfant.getScore());
                }*/
				// Meilleur score
				if(compareScore < enfant.getScore()){

                    compareScore = enfant.getScore();
					feuilleAComparer.setCoupJoue(enfant.getCoupJoue());
					feuilleAComparer.setMoyenneScoreEnfant(enfant.getMoyenneScoreEnfant());
					
				}
				
				// En cas d'egalite
				else if(compareScore == enfant.getScore()){
				
					if (feuilleAComparer.getMoyenneScoreEnfant() == 0 && Math.random() > 0.5d){

                        compareScore = enfant.getScore();
						feuilleAComparer.setCoupJoue(enfant.getCoupJoue());
						feuilleAComparer.setMoyenneScoreEnfant(enfant.getMoyenneScoreEnfant());
					}
					
					else if(feuilleAComparer.getMoyenneScoreEnfant() < enfant.getMoyenneScoreEnfant()){

                        compareScore = enfant.getScore();
						feuilleAComparer.setCoupJoue(enfant.getCoupJoue());
						feuilleAComparer.setMoyenneScoreEnfant(enfant.getMoyenneScoreEnfant());
					}
					
				}
				
				this.moyenneScoreEnfants += enfant.getScore();
			}
		}
		
		// Conserve le plus petit score possible
		else{

			for (Feuille enfant : this.feuilleEnfants){
				
				// Meilleur score
				if(compareScore > enfant.getScore()){

                    compareScore = (enfant.getScore());
					feuilleAComparer.setCoupJoue(enfant.getCoupJoue());
					feuilleAComparer.setMoyenneScoreEnfant(enfant.getMoyenneScoreEnfant());
				}
				
				// En cas d'egalite
				else if(compareScore == enfant.getScore()){
				
					if (feuilleAComparer.getMoyenneScoreEnfant() == 0 && Math.random() > 0.5d){

                        compareScore = (enfant.getScore());
						feuilleAComparer.setCoupJoue(enfant.getCoupJoue());
						feuilleAComparer.setMoyenneScoreEnfant(enfant.getMoyenneScoreEnfant());
					}
					
					else if(feuilleAComparer.getMoyenneScoreEnfant() > enfant.getMoyenneScoreEnfant()){

                        compareScore = (enfant.getScore());
						feuilleAComparer.setCoupJoue(enfant.getCoupJoue());
						feuilleAComparer.setMoyenneScoreEnfant(enfant.getMoyenneScoreEnfant());
					}	
				}
				
				this.moyenneScoreEnfants += enfant.getScore();
			}
		}
		
		// Mise a jour de cette feuille avec le 'meilleur' score de ses enfants
		this.setScore(compareScore);
		
		this.moyenneScoreEnfants /= this.feuilleEnfants.size();
		
		// Si c'est la feuille racine, elle conserve aussi le meilleur mouvement
		// dans le but de l'envoyer au serveur apres
		if (profondeur == 0 ){
			this.setCoupJoue(feuilleAComparer.getCoupJoue());
		}
	}
	
	public void setScore(int score){this.score = score;}

    public void setScoreWinLose(int scoreWinLose){
        this.scoreWinLose += scoreWinLose;
    }
	
	public int getScore(){
		return this.score + scoreWinLose;
	}
	
	public void setCoupJoue(String coupJoue){
		this.coupJoue = coupJoue;
	}
	
	public String getCoupJoue(){
		return this.coupJoue;
	}

    public boolean isJoueurEstMAX(int profondeur){
        if(profondeur == 0){
            //pour que les addition de 1000 ou soustranction de 1000 fonctionne (win lose) la branche la plus haute doit etre un max
            return true;
        }

        // la branche la plus basse sera un max aussi puisque sinon on coupe serieusement nos chances
        return profondeur % 2 == compteurProfondeur;
    }

	private int getMoyenneScoreEnfant(){
		return this.moyenneScoreEnfants;
	}
	
	private void setMoyenneScoreEnfant(int moyenne){
		this.moyenneScoreEnfants = moyenne;
	}

    public String getPremierCoupJouer(){ return premierCoupJouer; }
}
