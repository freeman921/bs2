package Freeman.Chess.Structure;

import java.util.LinkedList;
import java.util.ListIterator;

import Freeman.Chess.Utility.Tools;

//Piece 為一顆象棋  , 用class來表示
public class Piece implements Cloneable // ˇ
{
	public static final int red=0,black=1;

	int value;
	public boolean used=false;
	
	public Piece(int v)
	{
		value = v;
	}
	public Piece(String name)
	{
		value = Tools.getPieceValue(name);
	}
	public int value()
	{
		return value;
	}
	@Override
	public String toString()
	{
		return Tools.getPieceStr(value);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		Piece p = (Piece)obj;
		if (p==null)
			return false;
		//System.out.println(this.value==p.value);
		return (this.value==p.value);
	}
	@Override
	public int hashCode()
	{
		return value;
	}
	@Override
	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}
}

