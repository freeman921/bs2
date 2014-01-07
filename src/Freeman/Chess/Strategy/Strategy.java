package Freeman.Chess.Strategy;

import Freeman.Chess.Systems.Hand;

public abstract class Strategy
{
	Hand ownerHand;
	
	public abstract int countScore();
}



