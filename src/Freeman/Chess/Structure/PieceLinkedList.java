package Freeman.Chess.Structure;

import java.util.LinkedList;
import java.util.ListIterator;

public class PieceLinkedList extends LinkedList<Piece>  // £¾
{
	int search(int v)
	{
		ListIterator<Piece> li =  listIterator();
		while ( li.hasNext() )
			if ( li.next().value() == v )
				return li.previousIndex(); // listIterator has no present location
		
		System.out.println( "can't find by index in PieceLinkedList" );
		return -1;
	}
}
