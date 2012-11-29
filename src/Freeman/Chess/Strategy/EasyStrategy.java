package Freeman.Chess.Strategy;

import java.util.ListIterator;

import Freeman.Chess.Structure.*;
import Freeman.Chess.Systems.Hand;
import Freeman.Chess.Utility.Tools;

public class EasyStrategy extends Strategy
{
	int sumUpScore,bestScore[],bestPatIdx[]; // the Pattern index of the best score
	PatternList list;
	Hand ownerHand;
	
	public EasyStrategy(Hand hand)
	{
		this.ownerHand = hand; 
		bestScore = new int[6];
		bestPatIdx = new int[6];
	}
	
	public int countScore() { 
		
		PieceLinkedList pieceList = ownerHand.pile;
		list = new PatternList();
		
		for (int i=0; i < pieceList.size() ;i++)
		{
			Piece p =pieceList.get(i);
			list.addAll( Tools.getPtListByPiece(p) );
		}
		count(); 
		return sumUpScore; 
	}
	
	void count()
	{
		// set clear
		ownerHand.cleanUsedBits();
		bestScore[5]=0;
		bestScore[3]=0;
		bestScore[2]=0;
		
		//testing(5);
		testing(3);
		testing(2);
		sumUpScore = bestScore[3] + bestScore[2];
	}
	
	void testing(int num)
	{
		bestScore[num] = 0;
		ListIterator<Pattern> patLI = list.listIterator();
		
		while ( patLI.hasNext() ) // for each Pattern
		{
			Pattern pat = patLI.next();
			if ( pat.length() != num) // limit 5 3 or 2
				continue;
			pat.cleanUsedBits(); // to avoid like 車 車 馬 包 = 4
			
			int score=0;
			ListIterator<Piece> handLI = ownerHand.pile.listIterator();
			
			while ( handLI.hasNext() ) // match how many Piece in Hand ?
			{
				Piece p = handLI.next();
				PatternList pl = Tools.getPtListByPiece(p);
				// Pattern list for single Piece
				
				// look if single piece is inside pattern list
				if ( p.used==false && matches(p,pat) )
					score += Tools.BASIC_SCORE_PER_MATCH;
				
			} 
			
			if ( score > bestScore[num])
			{ 
				bestScore[num] = score;
				bestPatIdx[num] = patLI.previousIndex();
			}
		} // for each Pattern
		
		// mark the Pieces which matches bestScore (to avoid repeating)
		if ( bestScore[num]!=0 )
			mark( bestPatIdx[num] );
		
	}// testing()
	
	void mark(int bestPatIdx) // mark by: bestPatIdx on hand
	{
		ListIterator<Piece> handLI = ownerHand.pile.listIterator();
		while ( handLI.hasNext() )
		{
			Piece p = handLI.next();
			Pattern pat = list.get(bestPatIdx);
			pat.cleanUsedBits(); // clean first
			
			if ( matches(p,pat) )
				p.used = true;
		}
	}
	
	boolean matches(Piece p, Pattern pat)
	{
		for (int i=0;i<pat.length();i++)
		{
			// the piece in pat is "not used" + "equal to p"
			if ( pat.used[i]==false && p.equals(pat.pieceArray[i]) )
			{
				pat.used[i]=true;
				return true;
			}
		}
		return false;
	}
}
