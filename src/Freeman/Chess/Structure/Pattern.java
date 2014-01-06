package Freeman.Chess.Structure;

import java.util.LinkedList;
import java.util.ListIterator;

////////////Pattern Family   ////////////
public class Pattern
{
	public Piece[] pieceArray;
	int length;
	
	int[] scoreMatchNum;
	public boolean[] used;
	
	public Pattern(Piece[] pieceArray,int[] smn,int length)
	{
		this.pieceArray = pieceArray;
		this.length = length;
		this.scoreMatchNum = smn;
		used = new boolean[length];
	}
	void print()
	{
		for (int i=0; i<length; i++)
			System.out.print( pieceArray[i].toString() + " ");
		//System.out.println("");
	}
	public void cleanUsedBits()
	{
		for (int i=0; i < length ;i++)
			used[i] = false;
	}
	
	public int length() { return length; }
	public int scoreForMatchNum(int n) { return scoreMatchNum[n]; }
		
	
	@Override
	public boolean equals(Object obj)
	{
		Pattern pat2 = (Pattern)obj;
		if (length != pat2.length)
			return false;
		for (int i=0;i<length;i++)
			if ( ! pieceArray[i].equals( pat2.pieceArray[i] ) )
				return false;
		// else	
		return true;
	}
}

