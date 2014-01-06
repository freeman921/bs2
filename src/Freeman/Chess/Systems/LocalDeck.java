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
	public void reGenerate()  // ˇ
	{
		int i;
		
		pile.clear();
		pile.add(new Piece("帥") );
		pile.add(new Piece("將") );
		
		for (i=0;i<2;i++) // two
		{
			pile.add(new Piece("士"));
			pile.add(new Piece("象"));
			pile.add(new Piece("車"));
			pile.add(new Piece("馬"));
			pile.add(new Piece("包"));
			pile.add(new Piece("仕"));
			pile.add(new Piece("相"));
			pile.add(new Piece("硨")); // encoding MS950 wrong
			pile.add(new Piece("傌"));
			pile.add(new Piece("炮"));
		}
		for (i=0;i<5;i++) // five
		{
			pile.add(new Piece("卒"));
			pile.add(new Piece("兵"));
		}
	} //generate()
}


