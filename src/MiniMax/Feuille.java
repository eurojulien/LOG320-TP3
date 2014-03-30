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
	
	// Vrai : Notre Pion
	// Faux : Pion adverse
	private boolean joueurEstMAX;
	private int score;
	
	// Moyenne du score des enfants
	private int moyenneScoreEnfants;
	
	// Coup joue pour atteindre cette feuille
	private String coupJoue;
	
	// Constructeur pour creer une feuille dans l'arbre MiniMax
	public Feuille(boolean joueurEstMAX, String coupJoue){
		
		this.feuilleEnfants = new ArrayList<Feuille>();
		this.joueurEstMAX = joueurEstMAX;
		this.score = 0;
		this.coupJoue = coupJoue;
		
		this.moyenneScoreEnfants = 0;
	}
	
	public void ajouterFeuilleEnfant(Feuille feuilleEnfant){
		this.feuilleEnfants.add(feuilleEnfant);
	}
	
	public void ajouterFeuilleEnfants(ArrayList<Feuille> feuilleEnfants){
		this.feuilleEnfants.addAll(feuilleEnfants);
	}
	
	// Attribue le score a cette feuille selon le meilleur score
	// des enfants de cette feuille
	// Parametre profondeur : Si egal a zero (Feuille parent), la feuille parent
	// copie le score ET le mouvement relie a ce score
	public void updateFeuilleAvecMeilleurFeuilleEnfant(int profondeur){
		
		Feuille feuilleAComparer = new Feuille(true,"");
	
		if (this.joueurEstMAX) { 
			feuilleAComparer.setScore(-10000);
		}
		
		else{
			feuilleAComparer.setScore(10000);
		}
		
		this.moyenneScoreEnfants	= 0;
		
		// La condition est en dehors de la boucle.
		// Cela oblige a avoir deux boucles, mais moins
		// de comparaison.
		
		// Conserve le plus grand score possible
		if(this.joueurEstMAX){
		
			for (Feuille enfant : this.feuilleEnfants){
				
				// Meilleur score
				if(feuilleAComparer.getScore() < enfant.getScore()){
					
					feuilleAComparer.setScore(enfant.getScore());
					feuilleAComparer.setCoupJoue(enfant.getCoupJoue());
					feuilleAComparer.setMoyenneScoreEnfant(enfant.getMoyenneScoreEnfant());
					
				}
				
				// En cas d'egalite
				else if(feuilleAComparer.getScore() == enfant.getScore()){
				
					if (feuilleAComparer.getMoyenneScoreEnfant() == 0 && Math.random() > 0.5d){
						
						//System.out.println(" RANDOM MAX [" + profondeur + "] " + feuilleAComparer.getCoupJoue() + " > " + enfant.getCoupJoue());
						
						feuilleAComparer.setScore(enfant.getScore());
						feuilleAComparer.setCoupJoue(enfant.getCoupJoue());
						feuilleAComparer.setMoyenneScoreEnfant(enfant.getMoyenneScoreEnfant());
					}
					
					else if(feuilleAComparer.getMoyenneScoreEnfant() < enfant.getMoyenneScoreEnfant()){
						
						//System.out.println(" MAX [" + profondeur + "] " + feuilleAComparer.getCoupJoue() + " > " + enfant.getCoupJoue());
						
						feuilleAComparer.setScore(enfant.getScore());
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
				if(feuilleAComparer.getScore() > enfant.getScore()){
					
					feuilleAComparer.setScore(enfant.getScore());
					feuilleAComparer.setCoupJoue(enfant.getCoupJoue());
					feuilleAComparer.setMoyenneScoreEnfant(enfant.getMoyenneScoreEnfant());
				}
				
				// En cas d'egalite
				else if(feuilleAComparer.getScore() == enfant.getScore()){
				
					if (feuilleAComparer.getMoyenneScoreEnfant() == 0 && Math.random() > 0.5d){
						
						//System.out.println(" RANDOM MIN [" + profondeur + "] " + feuilleAComparer.getCoupJoue() + " > " + enfant.getCoupJoue());
						
						feuilleAComparer.setScore(enfant.getScore());
						feuilleAComparer.setCoupJoue(enfant.getCoupJoue());
						feuilleAComparer.setMoyenneScoreEnfant(enfant.getMoyenneScoreEnfant());
					}
					
					else if(feuilleAComparer.getMoyenneScoreEnfant() > enfant.getMoyenneScoreEnfant()){
						
						//System.out.println(" MIN [" + profondeur + "] " + feuilleAComparer.getCoupJoue() + " > " + enfant.getCoupJoue());
						
						feuilleAComparer.setScore(enfant.getScore());
						feuilleAComparer.setCoupJoue(enfant.getCoupJoue());
						feuilleAComparer.setMoyenneScoreEnfant(enfant.getMoyenneScoreEnfant());
					}	
				}
				
				this.moyenneScoreEnfants += enfant.getScore();
			}
		}
		
		// Mise a jour de cette feuille avec le 'meilleur' score de ses enfants
		this.setScore(feuilleAComparer.getScore());
		
		this.moyenneScoreEnfants /= this.feuilleEnfants.size();
		
		// Si c'est la feuille racine, elle conserve aussi le meilleur mouvement
		// dans le but de l'envoyer au serveur apres
		if (profondeur == 0 ){
			this.setCoupJoue(feuilleAComparer.getCoupJoue());
		}
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
	
	private int getMoyenneScoreEnfant(){
		return this.moyenneScoreEnfants;
	}
	
	private void setMoyenneScoreEnfant(int moyenne){
		this.moyenneScoreEnfants = moyenne;
	}
}
