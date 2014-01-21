package Freeman.Chess.PlayerType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;



import Freeman.Bs2.Screen;
import Freeman.Chess.ChessMajian;
import Freeman.Chess.Utility.Tools;
import Freeman.Chess.Structure.*;
import Freeman.Chess.Systems.Hand;


public abstract class Player 
{
	Hand hand;
	ChessPile trash;
	public String name;
	int type;
	//Strategy stg;
	
	Player(String name) 
	{ 
		this.name = name; 
		trash = new ChessPile("trash");
	}
	public void clear()
	{
		hand.pile.clear();
		trash.clear();
	}
	public void clearHand() { hand.pile.clear(); }
	
	public void printHand(PrintStream out)
	{
		out.print(name + ": ");
		hand.print(out);
	}
	public int type() { return type; }
	public ChessPile getTrashPile() { return trash; }
	public Hand getHand() { return hand; }
	
	public abstract String chooseMove(ChessPile enemytrash,int round);
	public abstract String chooseToThrow(ChessPile enemytrash, int round);
}




