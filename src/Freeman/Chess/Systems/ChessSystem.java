package Freeman.Chess.Systems;

import java.io.PrintStream;

import Freeman.Chess.*;
import Freeman.Chess.Utility.GameRecord;

public abstract class ChessSystem 
{
	int type;
	Player player1, player2;
			
	public static final int LOCAL	=0;
	public static final int BS2 	=1;
	
	// Constructor
	public ChessSystem(){}
	public ChessSystem(Player p1, Player p2)
	{
		player1 = p1;
		player2 = p2;
	}
	
	// functions
	public int type() { return type; }
	public void setType(int t) { type = t; }
	
	public abstract void roundStart();
	public abstract void addMoneyToResult( int gameStatus,int money,int curGame);
	public abstract void sendVar( GameRecord gr, 
			  PrintStream socketOut,PrintStream resultOut, PrintStream out);
	
}
