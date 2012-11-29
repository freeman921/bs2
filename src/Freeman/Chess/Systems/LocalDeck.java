package Freeman.Chess.Systems;

import java.util.LinkedList;
import java.util.ListIterator;
import java.io.*;

import Freeman.Bs2.Screen;
import Freeman.Chess.Strategy.EasyStrategy;
import Freeman.Chess.Strategy.NormalStrategy;
import Freeman.Chess.Strategy.Strategy;
import Freeman.Chess.Structure.Piece;


public class LocalDeck extends Deck
{
	public LocalDeck() { } 
	LocalDeck(String name) 
	{ 
		this.name = name;
	} 
	
	public Piece draw(int player_type) // draw by Random.
	{
		int pos =(int)(( Math.random()*pile.size() ));
		Piece p = removeByPos(pos);
		
		//if (p==null) still returns null
		return p;
	}
	public void reGenerate()  // ��
	{
		int i;
		
		pile.clear();
		pile.add(new Piece("��") );
		pile.add(new Piece("�N") );
		
		for (i=0;i<2;i++) // two
		{
			pile.add(new Piece("�h"));
			pile.add(new Piece("�H"));
			pile.add(new Piece("��"));
			pile.add(new Piece("��"));
			pile.add(new Piece("�]"));
			pile.add(new Piece("�K"));
			pile.add(new Piece("��"));
			pile.add(new Piece("��")); // encoding MS950 wrong
			pile.add(new Piece("�X"));
			pile.add(new Piece("��"));
		}
		for (i=0;i<5;i++) // five
		{
			pile.add(new Piece("��"));
			pile.add(new Piece("�L"));
		}
	} //generate()
}


