package MiniMax;

import java.util.ArrayList;

import LinesOfActions.IA;

public class VictoryOrDefeat extends Thread{

	private static ArrayList<IA> IAList;
	private static ArrayList<Feuille> leafList;
	private static VictoryOrDefeat vod				= null;
	private static int index;
	
	private static int VICTORY						= 1;
	private static int DEFEAT						= -1;
	
	private VictoryOrDefeat(){
		
		Thread.currentThread().setPriority(NORM_PRIORITY);
		
		this.IAList		= new ArrayList<IA>();
		this.leafList	= new ArrayList<Feuille>();
		index		= 0;
	}
	
	public static VictoryOrDefeat getInstance(){
		if(vod == null){
			vod = new VictoryOrDefeat();
		}
		
		return  vod;
	}
	
	// Thread
	public void run(){
		
		int victoryOrDefeat = 0;
		while(SyncThread.keepThreadsAlive){
			
			// Attente de travail
			while(index == VictoryOrDefeat.IAList.size()){
				
				
				if(SyncThread.flushVictoryOrDefeat){
				
					VictoryOrDefeat.leafList.clear();
					VictoryOrDefeat.IAList.clear();
					index = 0;
					
					SyncThread.flushVictoryOrDefeat = false;
				}
				
				try {
					Thread.sleep(SyncThread.WAITING_STEP_TIME);
				} catch (InterruptedException e) {}
			}
			
			// Execution du travail
			do{
		
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {}
				
				victoryOrDefeat = IAList.get(index).findMateThreat(2);
				
				if(victoryOrDefeat == VICTORY){
					leafList.get(index).setScoreVictoryOrDefeat(Feuille.SCORE_VICTORY);
					System.out.println("VICTORY");
				}
				
				else if (victoryOrDefeat == DEFEAT){
					leafList.get(index).setScoreVictoryOrDefeat(Feuille.SCORE_DEFEAT);
					System.out.println("DEFEAT");
				}
				
				index ++;
				
			}while(index < leafList.size());
		}	
	}
	
	public static void addLeafToCheck(Feuille feuille, IA ia){
		
		VictoryOrDefeat.leafList.add(feuille);
		VictoryOrDefeat.IAList.add(ia);
	}
}
