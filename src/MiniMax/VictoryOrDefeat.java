package MiniMax;

import java.util.ArrayList;

import LinesOfActions.IA;

public class VictoryOrDefeat extends Thread{

	private ArrayList<IA> IAList 		= null;
	private int currentTreeDepth		= 0;
	private int player					= 0;
	
	public static final int VICTORY		= 1;
	public static final int DEFEAT		= -1;
	public static final int NOTHING		= 0;
	
	public VictoryOrDefeat(ArrayList<IA> IAList, int player, int currentTreeDepth){
		
		Thread.currentThread().setPriority(NORM_PRIORITY);
		
		this.IAList 			= IAList;
		this.player				= player;
		this.currentTreeDepth	= currentTreeDepth;
	}
	
	// Thread
	public void run(){
		
		int victoryOrDefeat = 0;
		
		for (IA VoD : this.IAList){
			victoryOrDefeat = VoD.findMateThreat(this.player);
			if(SyncThread.victoryOrDefautHasBeenFound[0]){
				break;
			}
			
			else if(victoryOrDefeat == VICTORY || victoryOrDefeat == DEFEAT){
                if(victoryOrDefeat == VICTORY)
				    System.out.println("Victory detected! | Niveau Arbre : " + this.currentTreeDepth);
                if(victoryOrDefeat == DEFEAT)
                    System.out.println("Defeat detected!  | Niveau Arbre : " + this.currentTreeDepth);
				SyncThread.currentMaxTreeDepth[0] = this.currentTreeDepth;
				SyncThread.victoryOrDefautHasBeenFound[0] = true;
				break;
			}
		}
	}
}
