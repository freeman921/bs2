package Freeman.Chess.Systems;

import java.io.PrintStream;

import Freeman.Bs2.Screen;
import Freeman.Chess.*;
import Freeman.Chess.Player.AIPlayer;
import Freeman.Chess.Player.Bs2Player;
import Freeman.Chess.Player.Player;
import Freeman.Chess.Structure.Piece;
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
	
	void giveFirstHand() // only player1 will use it
	{
			for (int i=0;i<4;i++)
			{
				player1.getHand().add( system.getDeck().draw(player1.type() ) );
				player2.getHand().add( system.getDeck().draw(player2.type() ) );
			}
	}
	
}
*/