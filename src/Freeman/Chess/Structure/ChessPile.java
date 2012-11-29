package Freeman.Chess.Structure;

import java.io.PrintStream;
import java.util.ListIterator;

import Freeman.Chess.Utility.Tools;

/** �H�Ѱ�: ��PHand�B��P��Deck�B��P��Trash , �Ҭ�ChessPile **/
public class ChessPile implements Cloneable
{
	public PieceLinkedList pile;
	protected String name;
	
	public ChessPile(){ pile=new PieceLinkedList(); } // for child to use super()
	public ChessPile(String name){ this.name = name; pile=new PieceLinkedList();}
	ChessPile(PieceLinkedList pl) { pile =pl ; }
	
	public void print(PrintStream out) // ��
	{
		//out.print(name + ": ");
		ListIterator<Piece> li = pile.listIterator();
		while (li.hasNext())
			out.print( Tools.getPieceStr( li.next().value ) + " " );
		out.print("\r\n");
	}
	public int findPiece(int value)
	{
		return pile.search(value);
	}
	
	void add (int value) // ��
	{ 
		pile.add(new Piece(value) );   
	}
	public void add (Piece p) 
	{
		pile.add(p);
	}
	
	public Piece removeByPos ( int pos ) // ��
	{ 
		if ( ! pile.isEmpty() )
			return pile.remove(pos); 
		else
		{
			System.out.println(name + ": " + "  remove when empty..");
			return null;
		}
	}
	
	public Piece removeByValue ( int value ) // ��
	{
 		int pos = pile.search( value );
 		if (pos != -1)
 			return pile.remove(pos);
 		else
 			return new Piece(Tools.UNKNOWN);
	}
	
	public void clear() // clear all pieces  -> (to initial.
	{
		pile.clear();
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException
	{
		return new ChessPile( (PieceLinkedList)(pile.clone()) );
	}
}
