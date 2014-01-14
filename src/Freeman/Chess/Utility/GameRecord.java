package Freeman.Chess.Utility;

import java.io.PrintStream;

import Freeman.Chess.Systems.Hand;

public class GameRecord
{
	public static final int LAST_N =1;
	public static final int CHANGE_WHEN_GREATEQ = 1 ;
	public static final int CHANGE_WHEN_SMALLEQ = -1 ;
	
	public static int winTime=0;
	public static int drawTime=0;
	public static int loseTime=0;
	public static int winMoney=0;
	
	public int moneyGameN[];
	public Hand winPattern;
	
	public int latestScore=0;
	public int[] latestResult ;
	
	public GameRecord(int n) 
	{ 
		moneyGameN = new int[n+1];
		
		latestResult = new int[LAST_N];
		for (int i=0;i<LAST_N; i++)
			latestResult[i] = 0;
	}
	
	°®¯Ü§ï¦¨µá static§a!
	
	static void statistics(int curGame, PrintStream resultOut)
	{
		//out.println("----------------------");
		
		resultOut.println("\n|||||||||||||||||||||||||");
		resultOut.println( (curGame-1)+ " rounds.... ");
		resultOut.println( winTime + " Wins.");
		resultOut.println( drawTime + " Draws.");
		resultOut.println( loseTime + " Loses.");
		resultOut.println( "Money won: " + winMoney );
	}
	
	
}
