package LinesOfActions.MiniMax;

import java.util.ArrayList;

/**
 * 
 * @author julien
 *
 * Classe qui represente un coup joue sur le tableau de jeu
 */
public class Feuille {

	// Liste des feuilles enfant de cette feuille
	// Cette approche a ete choisie car il est possible que deux feuilles
	// parent aient la meme feuille enfant
	private ArrayList <Feuille> feuilleEnfants;
	
	// Vrai : Notre Pion
	// Faux : Pion adverse
	private boolean joueurEstMAX;
	private int score;
	
	// Coup joue pour atteindre cette feuille
	private String coupJoue;
	
	// Constructeur pour creer une feuille dans l'arbre MiniMax
	public Feuille(boolean joueurAdverse, String coupJoue){
		
		this.feuilleEnfants = new ArrayList<Feuille>();
		this.joueurEstMAX = joueurAdverse;
		this.score = 0;
		this.coupJoue = coupJoue;
	}
	
	public void ajouterFeuilleEnfant(Feuille feuilleEnfant){
		this.feuilleEnfants.add(feuilleEnfant);
	}
	
	// Attribue le score a cette feuille selon le meilleur score
	// des enfants de cette feuille
	public void updateFeuilleScoreAvecMeilleurScoreEnfants(){
		
		int scoreAComparer = (int) Math.pow(10, 5);
		
		if (this.joueurEstMAX) { scoreAComparer = -(int) Math.pow(10, 5);}
		
		// La condition est en dehors de la boucle.
		// Cela oblige a avoir deux boucles, mais moins
		// de comparason.
		
		// Conserve le plus petit score possible
		if(this.joueurEstMAX){
		
			for (Feuille enfant : this.feuilleEnfants){
				
				if(scoreAComparer > enfant.getScore()){
					
					scoreAComparer = enfant.getScore();
				}
			}
		}
		
		// Conserve le plus grand score possible
		else{
			
			for (Feuille enfant : this.feuilleEnfants){
				
				if(scoreAComparer < enfant.getScore()){
					
					scoreAComparer = enfant.getScore();
				}
			}
		}
		
		// Mise a jour de cette feuille avec le 'meilleur' score de ses enfants
		this.setScore(scoreAComparer);
	}
	
	public void setScore(int score){
		this.score = score;
	}
	
	public int getScore(){
		return this.score;
	}
	
	public void setCoupJoue(String coupJoue){
		this.coupJoue = coupJoue;
	}
	
	public String getCoupJoue(){
		return this.coupJoue;
	}
	
	public boolean isJoueurEstMAX(){
		return this.joueurEstMAX;
	}
	
	
}
