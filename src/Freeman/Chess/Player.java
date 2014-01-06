package Freeman.Chess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;


import Freeman.Bs2.Screen;
import Freeman.Chess.Utility.Tools;
import Freeman.Chess.Structure.*;
import Freeman.Chess.Systems.Hand;


public abstract class Player 
{
	Hand hand;
	ChessPile trash;
	String name;
	int type;
	//Strategy stg;
	
	Player(String name) 
	{ 
		this.name = name; 
		trash = new ChessPile("trash");
	}
	void clear()
	{
		hand.pile.clear();
		trash.clear();
	}
	void clearHand() { hand.pile.clear(); }
	
	public void printHand(PrintStream out)
	{
		out.print(name + ": ");
		hand.print(out);
	}
	public int type() { return type; }
	
	abstract String chooseMove(ChessPile enemytrash,int round);
	abstract String chooseToThrow(ChessPile enemytrash, int round);
}

class Bs2Player extends Player
{
	Screen screen;
	Bs2Player(String name,Screen screen) 
	{ 
		super(name);
		type = ChessMajian.BS2;
		hand = new Hand();
		this.screen = screen;
	}
	
	int bs2TrashY=10, playerTrashY=13;
	//int mytrashX,enemytrashX,;
	char c,c2;
	
	String chooseMove(ChessPile enemytrash, int round)
	{
		c = screen.getValueAt( playerTrashY-1 , 3+(round-1)*4 );
		
		if (c=='¢w')
			return "draw";
		else if (c=='¡´')
			return "eat";
		else
			return "SOMETHING_WRONG";
	}
	String chooseToThrow(ChessPile enemytrash, int round)
	{
		//c = screen.scrBuf[ 6 ].charAt( 3 ); // see if Bs2 shows it's pile
		
		c = screen.getValueAt( bs2TrashY , 3+(round-1)*4 );
		return Character.toString(c);
	}
}

class HumanPlayer extends Player
{
	BufferedReader in;
	PrintStream out;
	
	HumanPlayer(String name,BufferedReader in,PrintStream out) 
	{ 
		super(name);
		
		this.in = in;
		this.out = out;
		
		hand = new Hand();
		type = ChessMajian.HUMAN;
	}
	
	String chooseMove(ChessPile enemytrash, int round)
	{
		String move="";
		
		out.print( name + ", choose your move (draw/eat) : ");
		try { move = in.readLine(); }
		catch (IOException IOe) { System.out.println("!! choose move wrong !!"); }
		
		return move;
	}
	String chooseToThrow(ChessPile enemytrash, int round)
	{
		String choice="";
		
		out.print( "choose the piece you wanna throw out (or type 'win'): " );
		try { choice = in.readLine(); }
		catch (IOException IOe) { System.out.println("!! throw piece wrong !!"); }
		
		return choice;
	}
}

class AIPlayer extends Player
{
	AIPlayer(String name,int stg_type) 
	{ 
		super(name); 
		type=ChessMajian.AI; 
		hand = new Hand(stg_type);
	}
	
	String chooseMove( ChessPile enemytrash, int round ) // returns draw or eat
	{
		if ( enemytrash.pile.isEmpty() ) // nothing to eat
			return "draw";
		
		// else
		Piece p = enemytrash.pile.peekLast();
		int score = hand.countScore();
		
		//System.out.print("Eat:  Original Score =" + score + "  ");
		if ( simulateEatScore(p) >= score + Tools.BASIC_SCORE_PER_MATCH /2 )
			return "eat";
		else
			return "draw";
	}
	
	int simulateEatScore(Piece p)
	{
		Hand simHand=null;
	
		try { simHand = (Hand)(hand.clone() ); }
		catch (Exception e) { System.out.println("!! simulateEatScore clone wrong !!"); }
		
		simHand.add(p); // =simHand.pile.add()
		int score = simHand.countScore();
		//System.out.println("Simulated EatScore =" + score);
		return score;
	}
	
	String chooseToThrow(ChessPile trash, int round)
	{
		// take turn throw out piece
		Hand haha=null;
		try { haha = (Hand)( hand.clone() ); }
		catch (Exception e) { System.out.println("!! aiThrowPiece() clone wrong !!"); }
		
		int scoreResult = hand.countScore();
		if ( scoreResult >= Tools.WIN )
			return "win";
		
		int score=0,bestScore=0,bestChoice=0;
		
		for (int i=0; i< haha.pile.size() ;i++ )
		{
			Piece p = haha.removeByPos(i);
			
			score = haha.countScore();
			//System.out.print("i="+i+",s="+score+" : ");
			if (score > bestScore)
			{
				bestScore = score;
				bestChoice = i;
			}
			
			haha.pile.add(i,p); // add p at index i
		}
		//System.out.println("");
		// ?? System.out.println("New Score = " + hand.score() );
		return haha.pile.get(bestChoice).toString();
	}
}
