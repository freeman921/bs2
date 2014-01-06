package Freeman.Chess.Systems;

import java.util.LinkedList;
import java.util.ListIterator;
import java.io.*;

import Freeman.Bs2.Screen;
import Freeman.Chess.ChessMajian;
import Freeman.Chess.Strategy.EasyStrategy;
import Freeman.Chess.Strategy.NormalStrategy;
import Freeman.Chess.Strategy.Strategy;
import Freeman.Chess.Structure.Piece;
import Freeman.Chess.Utility.Tools;


public class Bs2Deck extends Deck
{
	Screen screen;
	PrintStream socketOut;
	
	public Bs2Deck(Screen screen,PrintStream out) { 
		this.screen=screen; 
		socketOut = out;
	}
	
	String s;
	char c;
	
	public Piece draw(int player_type)  // draw from BS2
	{
		
		if (player_type==ChessMajian.BS2)
			return new Piece("x"); // no matter
		
		//Tools.waitForRefresh( Tools.DET_I_GOT,screen );
		while ( true ) // wait for bs2 to show piece
		{
			c = screen.getValueAt(17,27); // the fifth piece of mine
			if ( c != ' ' ) // not empty
				break;
			//Thread.sleep(0);
		}
		
		return new Piece( Character.toString(c) ); 
	}
	
	boolean testDrawGame()
	{
		char c = screen.getValueAt(24,2);
		char c2 = screen.getValueAt(24,5);
		
		if (c=='¡»' && c2=='¬y')
			return true;
		return false;
	}
}




