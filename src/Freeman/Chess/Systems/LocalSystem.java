package Freeman.Chess.Systems;

import java.io.PrintStream;

import Freeman.Bs2.Screen;
import Freeman.Chess.*;
import Freeman.Chess.Player.AIPlayer;
import Freeman.Chess.Player.Bs2Player;
import Freeman.Chess.Player.Player;
import Freeman.Chess.Utility.Tools;

/*
public class LocalSystem extends ChessSystem
{
	public LocalSystem (Player p1, Player p2) {	super(p1,p2); type=LOCAL; }
	
	public void init(Screen screen, PrintStream socketOut)
	{
		player1 = new AIPlayer("player1",NORMAL_AI);
		player2 = new AIPlayer("player2",EASY_AI);
		//player2 = new HumanPlayer("Freeman",in,out);
		deck = new LocalDeck();
	}
	
	public void roundStart()
	{
		//((LocalDeck)deck).reGenerate();
	}
	
	//For Local System
	boolean checkWin(Hand player)
	{	
		if ( player.countScore() >= Tools.WIN )
			return true;
		return false;
	}
	

	
}
*/