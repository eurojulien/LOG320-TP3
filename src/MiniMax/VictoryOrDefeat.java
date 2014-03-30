package MiniMax;

import java.util.ArrayList;

import LinesOfActions.IA;

public class VictoryOrDefeat extends Thread{

	private ArrayList<Feuille> FeuilleList 		= null;
	private int player					= 0;

    public ArrayList<String> winningMoves = new ArrayList<String>();
    public ArrayList<String> losinggMoves = new ArrayList<String>();

	public static final int VICTORY		= 1;
	public static final int DEFEAT		= -1;
	public static final int NOTHING		= 0;
    public static boolean started = false;

    public VictoryOrDefeat(int player){
		Thread.currentThread().setPriority(NORM_PRIORITY);
		this.player				    = player;
	}

    public void addToList(ArrayList<Feuille> FeuilleList){
        if(this.FeuilleList == null){
            this.FeuilleList = new ArrayList<Feuille>();
        }
        this.FeuilleList.addAll(FeuilleList);

    }

    public void clearLists(){
        losinggMoves.clear();
        winningMoves.clear();
        FeuilleList.clear();
    }


	// Thread
	public void run(){
        if(FeuilleList == null){
            FeuilleList = new ArrayList<Feuille>();
        }

		int victoryOrDefeat = 0;
        // on va essayer d'utiliser le thread avec une liste variable plutôt

        while(true){
        for (int i = 0; i < FeuilleList.size(); i++){
            Feuille feuille = FeuilleList.get(i);
            IA VoD = feuille.mindForFeuille;

            victoryOrDefeat = VoD.findMateThreat(this.player);
            //todo : mettre breakpoints ici julien
           if(victoryOrDefeat == VICTORY || victoryOrDefeat == DEFEAT){
                if(victoryOrDefeat == VICTORY){
                    System.out.println("Victory detected! | Niveau Arbre : " + feuille.profondeurArbre + " --- move primordial: " + feuille.getPremierCoupJouer());
                    winningMoves.add(feuille.getPremierCoupJouer());
                }
                if(victoryOrDefeat == DEFEAT)
                {
                    System.out.println("Defeat detected!  | Niveau Arbre : " + feuille.profondeurArbre + " --- move primordial: " + feuille.getPremierCoupJouer());
                    losinggMoves.add(feuille.getPremierCoupJouer());
                }
            }
        }
            FeuilleList.clear();
        }

        //System.out.println("VoD : I fini ! List size :" + FeuilleList.size());


	}
}
