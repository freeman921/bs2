package Freeman.Chess.Utility;

import java.io.PrintStream;

import Freeman.Chess.Player.Player;

public class MyIO
{
	PrintStream out,errOut,resultOut;
	
	public MyIO(PrintStream out,PrintStream errOut,PrintStream resultOut)
	{
		this.out = out; 
		this.errOut = errOut;
		this.resultOut = resultOut;
	}
	
	public void printOut (String s) { out.println(s); }
	public void printErrOut (String s) { errOut.println(s); }
	public void printResultOut (String s) { resultOut.println(s); }
	
	public void gameNum(int curGame)
	{
		out.println("|||||||||||||||||||||||||");
		out.println("        Game " + curGame );
		out.println("|||||||||||||||||||||||||");
	}
	public void roundInfo(int round,Player player1,Player player2)
	{
		out.println("---------- Round" + round + " ----------");
		player1.printHand(out);
		player2.printHand(out);
		out.println("------------------------");
	}
}
