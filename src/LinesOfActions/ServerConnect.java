package LinesOfActions_2;
import java.io.*;
import java.net.*;


class ServerConnect {
	
	private BufferedInputStream input;
	private BufferedOutputStream output;
	private static final int BOARDSIZE = BoardInOut.BOARDSIZE;
	
	public ServerConnect(){
		
		Socket MyClient;

		try {
			MyClient = new Socket("localhost", 8888);
		   	input    = new BufferedInputStream(MyClient.getInputStream());
			output   = new BufferedOutputStream(MyClient.getOutputStream());
		}
		catch (IOException e) {
	   		System.out.println(e);
		}
	}
	
	public char getServerCommand(){
		char cmd = 0;
		
		try {
			while(cmd == 0){
				cmd = (char)input.read();
			}
	        
		}catch (IOException e) {
	   		System.out.println(e);
		}
		
		return cmd;
	}
	
	public String getLastTurn(){
		byte[] aBuffer = new byte[16];
		
		int size;
		
		try {
			size = input.available();
			input.read(aBuffer,0,size);
		} catch (IOException e){e.printStackTrace();}
		
		String s = new String(aBuffer);
		return s;
	}
	
	public int[][] getBoardSetup(){
		
		int[][] board = new int[BOARDSIZE][BOARDSIZE];
		
		try {
			byte[] aBuffer = new byte[1024];
			
			int size = input.available();
			input.read(aBuffer,0,size);
	        String s = new String(aBuffer).trim();
	        String[] boardValues;
	        boardValues = s.split(" ");
	        
	        int x=0,y=0;
	        int boardValuesLength = boardValues.length;
	        for(int i = 0 ; i < boardValuesLength ; i++){
	        	
	            board[x][y] = Integer.parseInt(boardValues[i]);
	            x++;
	            
	            if(x == BOARDSIZE){
	                x = 0;
	                y++;
	            }
	        }
		}catch (IOException e) {
	   		System.out.println(e);
		}
		
		return board;
	}
	
	public void sendServerCommand(String move){
		try {
			output.write(move.getBytes(),0,move.length());
			output.flush();
		} catch (IOException e){e.printStackTrace();}
	}
}
