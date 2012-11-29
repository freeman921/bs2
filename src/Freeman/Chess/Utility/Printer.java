package Freeman.Chess.Utility;

import java.io.PrintStream;

import Freeman.Chess.Player;

public class Printer 
{
	PrintStream out;
	
	public Printer(PrintStream out){ this.out = out; }
	
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
