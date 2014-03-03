package LinesOfActions;
import java.io.*;
import java.net.*;


class ServerConnect {
	
	// Test de commit
	private int[][] board = new int[8][8];
	private BufferedInputStream input;
	private BufferedOutputStream output;
	
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
	
	public char readServerCommand(){
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
	
	public String readLastTurn(){
		byte[] aBuffer = new byte[16];
		
		int size;
		
		try {
			size = input.available();
			input.read(aBuffer,0,size);
		} catch (IOException e){e.printStackTrace();}
		//System.out.println("size " + size);
		
		String s = new String(aBuffer);
		return s;
	}
	
	public void refreshBoard(){
		
		try {
			byte[] aBuffer = new byte[1024];
			
			int size = input.available();
			//System.out.println("size " + size);
			input.read(aBuffer,0,size);
	        String s = new String(aBuffer).trim();
	        String[] boardValues;
	        boardValues = s.split(" ");
	        
	        int x=0,y=0;
	        int boardLength  = boardValues.length;
	        for(int i = 0 ; i < boardLength ; i++){
	            board[x][y] = Integer.parseInt(boardValues[i]);
	            
	            x++;
	            if(x == 8){
	                x = 0;
	                y++;
	            }
	        }
	        
		}catch (IOException e) {
	   		System.out.println(e);
		}
	}
	
	public void sendServerCommand(String move){
		try {
			output.write(move.getBytes(),0,move.length());
			output.flush();
		} catch (IOException e){e.printStackTrace();}
	}
	
	public int[][] getBoard(){
		return board;
	}
	
	
}
