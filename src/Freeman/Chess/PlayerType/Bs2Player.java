package Freeman.Chess.PlayerType;

import Freeman.Bs2.Screen;
import Freeman.Chess.ChessMajian;
import Freeman.Chess.Structure.ChessPile;
import Freeman.Chess.Systems.Hand;

public class Bs2Player extends Player
{
	Screen screen;
	public Bs2Player(String name,Screen screen) 
	{ 
		super(name);
		type = ChessMajian.BS2;
		hand = new Hand();
		this.screen = screen;
	}
	
	int bs2TrashY=10, playerTrashY=13;
	//int mytrashX,enemytrashX,;
	char c,c2;
	
	public String chooseMove(ChessPile enemytrash, int round)
	{
		c = screen.getValueAt( playerTrashY-1 , 3+(round-1)*4 );
		
		if (c=='¢w')
			return "draw";
		else if (c=='¡´')
			return "eat";
		else
			return "SOMETHING_WRONG";
	}
	public String chooseToThrow(ChessPile enemytrash, int round)
	{
		//c = screen.scrBuf[ 6 ].charAt( 3 ); // see if Bs2 shows it's pile
		
		c = screen.getValueAt( bs2TrashY , 3+(round-1)*4 );
		return Character.toString(c);
	}
}
