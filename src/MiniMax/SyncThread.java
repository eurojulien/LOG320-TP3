package MiniMax;

public class SyncThread {

	// Variable partagee par les threads
	public static boolean bestMoveHasBeenFound 			= false;
	public static boolean computationTimeIsFinished		= false;
	public static int currentMaxTreeDepth 				= 0;
	
	public static boolean flushVictoryOrDefeat			= false;
	
	
	// Attente du main pour envoyer une reponse
	public final static int MILLISECONDS_BEFORE_WAKE_THE_DOG 	= 4000;
	public final static int WAITING_STEP_TIME					= 10;
	public static boolean keepThreadsAlive						= true;
	public static boolean minMaxIsReadyToBeDigged				= false;

	// Variables des diggers
	// Nombre de threads qui son demarres
	public static final int ALL_DIGGERS_ARE_DONE		= 4;
	public static int diggersAreDone					= 0;
	
	}
