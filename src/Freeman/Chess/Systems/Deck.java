package Freeman.Chess.Systems;

import Freeman.Chess.Structure.ChessPile;
import Freeman.Chess.Structure.Piece;

/** abstract Deck 使抽牌堆的來源   可以從本機、網路，  有彈性而不被限制 **/
public abstract class Deck extends ChessPile
{
	public abstract Piece draw(int player_type);
}
