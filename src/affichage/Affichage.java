package affichage;

public class Affichage {

	private final static String DISPLAY_BLANC 	= "o";
	private final static String DISPLAY_NOIR	= "x";
	private final static String DISPLAY_VIDE	= " ";
	private final static String DISPLAY_COL		= "|";
	private final static String DISPLAY_LIN		= " - - - - - - - - - - ";
	
	public final static int PION_BLANC			= 2;
	public final static int PION_NOIR			= 4;
	public final static int CASE_VIDE			= 0;
	private final static int DIMENSION_MAX		= 8;
	
	public static void printBoard(final int grilleJeu[][]){
		
		String boardLine = DISPLAY_LIN;
		
		System.out.println(boardLine);
		
		for(int i = 0; i < DIMENSION_MAX; i ++)
		{

			boardLine = DISPLAY_COL;
			
			for(int j = 0; j < DIMENSION_MAX; j ++){
				
				if(grilleJeu[j][i] == CASE_VIDE){
					
					boardLine = boardLine + DISPLAY_VIDE;
				}
				
				else if(grilleJeu[j][i] == PION_BLANC){
					
					boardLine = boardLine + DISPLAY_BLANC;
				}
				
				else if(grilleJeu[j][i] == PION_NOIR){
					
					boardLine = boardLine + DISPLAY_NOIR;
				}
				
				boardLine = boardLine + DISPLAY_COL;
			}
			
			System.out.println(boardLine);
			
			boardLine = DISPLAY_LIN;
			System.out.println(boardLine);
		}
	}
	
}
