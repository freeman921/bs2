package Freeman.Chess.Systems;

import Freeman.Chess.Structure.ChessPile;
import Freeman.Chess.Structure.Piece;

/** abstract Deck �ϩ�P�諸�ӷ�   �i�H�q�����B�����A  ���u�ʦӤ��Q���� **/
public abstract class Deck extends ChessPile
{
	public abstract Piece draw(int player_type);
}
