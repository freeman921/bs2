package Freeman.Chess.Systems;
import Freeman.Chess.ChessMajian;
import Freeman.Chess.Strategy.*;
import Freeman.Chess.Structure.ChessPile;
import Freeman.Chess.Structure.PieceLinkedList;

public class Hand extends ChessPile
{
	Strategy stg;
	int stg_type;
	
	public Hand(){}
	public Hand( int stgType ) 
	{  
		stg_type = stgType;
		makeStrategy();
	}
	Hand(PieceLinkedList pl, int stgType ) 
	{ 
		pile =pl ; 
		stg_type = stgType;
		makeStrategy();
	}
	
	
	void makeStrategy()
	{
		if (stg_type==ChessMajian.EASY_AI )
			stg = new EasyStrategy(this);
		else if (stg_type==ChessMajian.NORMAL_AI )
			stg = new NormalStrategy(this);
		
	}
	
	public int countScore() { return stg.countScore(); } // first makeStrategy() !!!
	
	
	public void cleanUsedBits()
	{
		int len = pile.size();
		for (int i=0; i < len ;i++)
			pile.get(i).used  = false;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException
	{
		return new Hand( (PieceLinkedList)(pile.clone()),stg_type );
	}
	
}
