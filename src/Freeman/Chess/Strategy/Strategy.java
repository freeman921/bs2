package Freeman.Chess.Strategy;

import Freeman.Chess.Structure.PatternList;
import Freeman.Chess.Systems.Hand;

public abstract class Strategy
{
	int sumUpScore,bestScore[],bestPatIdx[]; // the Pattern index of the best score
	PatternList list;
	Hand ownerHand;
	
	public abstract int countScore();
}



