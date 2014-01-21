package Freeman.Chess.PlayerType;

import Freeman.Chess.ChessMajian;
import Freeman.Chess.Structure.ChessPile;
import Freeman.Chess.Structure.Piece;
import Freeman.Chess.Systems.Hand;
import Freeman.Chess.Utility.Tools;

public class AIPlayer extends Player
{
	public AIPlayer(String name,int stg_type) 
	{ 
		super(name); 
		type=ChessMajian.AI; 
		hand = new Hand(stg_type);
	}
	
	public String chooseMove( ChessPile enemytrash, int round ) // returns draw or eat
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
	
	public String chooseToThrow(ChessPile trash, int round)
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
