package Freeman.Chess.Structure;

import java.util.LinkedList;
import java.util.ListIterator;

public class PatternList extends LinkedList<Pattern>
{
	void print()
	{
		ListIterator<Pattern> li = listIterator();
		while ( li.hasNext() )
		{
			li.next().print();
			System.out.print(", ");
		}
		System.out.println("");
	}
}
