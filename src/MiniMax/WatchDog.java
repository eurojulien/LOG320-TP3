package MiniMax;

// Classe qui verifie si le calcul de l'arbre depasse 4500 Millisecondes.
// Si oui, elle permet d'avertir le Main qu'il faut envoyer immediatement le meilleur coup trouve
public class WatchDog extends Thread {

	private static final int MILLISECONDS_BEFORE_WAKE_THE_DOG 	= 4500;
	private static final int TIME_NEEDED_FOR_CALCULATION		= 3;
	private static final int WAITING_STEP_TIME					= 100;
	
	private static final int STEP_TO_KEEP_MAX					= 2;
	
	private static MiniMax miniMax;

	// Thread lance par le main
	// S'assure que le temps de traitement de l'arbre ne depasse pas : MILLISECONDS_BEFORE_WAKE_THE_DOG
	public WatchDog(){
		this.setPriority(Thread.MAX_PRIORITY);
	}
	
	@Override
	public void run(){
		
		// Initalisation des flags
		SyncThread.bestMoveHasBeenFound[0] 			= false;
		SyncThread.victoryOrDefautHasBeenFound[0] 	= false;
		
		// Lancement du thread de MinMax
		miniMax = new MiniMax();
		miniMax.start();
		
		boolean minMaxHasFinished 	= false;
		int elapsedTime 			= 0;
		
		do{
			try {
				Thread.sleep(WAITING_STEP_TIME);
			} catch (InterruptedException e) {}
			
			elapsedTime += WAITING_STEP_TIME;
			
			if (MiniMax.bestMoveHasBeenFound()){
				minMaxHasFinished = true;
			}
			
		}while(!minMaxHasFinished && elapsedTime < MILLISECONDS_BEFORE_WAKE_THE_DOG);
		
		// Temps de calcul trop lent pour profondeur actuelle de l'arbre, on remonte de 1
		if (!minMaxHasFinished) {
			
			SyncThread.bestMoveHasBeenFound[0] = true;
			
			
			// Conserve les dernieres feuilles comme MAX

				SyncThread.currentMaxTreeDepth[0] --;

			
		}
		
		// Temps de cacul suffisament rapide pour augmenter la profondeur de l'arbre
		else if (elapsedTime * TIME_NEEDED_FOR_CALCULATION < MILLISECONDS_BEFORE_WAKE_THE_DOG && !SyncThread.victoryOrDefautHasBeenFound[0]){

			// Conserve les dernieres feuilles comme MAX

				SyncThread.currentMaxTreeDepth[0] ++;

						
		}
	}
}
