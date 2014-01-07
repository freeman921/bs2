package Freeman.Chess.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

import Freeman.Chess.ChessMajian;
import Freeman.Chess.Structure.ChessPile;
import Freeman.Chess.Systems.Hand;

public class HumanPlayer extends Player
{
	BufferedReader in;
	PrintStream out;
	
	public HumanPlayer(String name,BufferedReader in,PrintStream out) 
	{ 
		super(name);
		
		this.in = in;
		this.out = out;
		
		hand = new Hand();
		type = ChessMajian.HUMAN;
	}
	
	public String chooseMove(ChessPile enemytrash, int round)
	{
		String move="";
		
		out.print( name + ", choose your move (draw/eat) : ");
		try { move = in.readLine(); }
		catch (IOException IOe) { System.out.println("!! choose move wrong !!"); }
		
		return move;
	}
	public String chooseToThrow(ChessPile enemytrash, int round)
	{
		String choice="";
		
		out.print( "choose the piece you wanna throw out (or type 'win'): " );
		try { choice = in.readLine(); }
		catch (IOException IOe) { System.out.println("!! throw piece wrong !!"); }
		
		return choice;
	}
}
