package Freeman.Chess.Systems;

import java.io.PrintStream;

import Freeman.Bs2.Screen;
import Freeman.Chess.*;
import Freeman.Chess.Player.AIPlayer;
import Freeman.Chess.Player.Bs2Player;
import Freeman.Chess.Player.Player;
import Freeman.Chess.Utility.GameRecord;

public class Bs2System extends ChessSystem  
{
	int AI_TYPE;
	String moneyToPlay,moneyShouldPlay;
	PrintStream socketOut,resultOut,out;
	GameRecord gameRecord;
	
	public Bs2System (Player p1, Player p2,int AI_TYPE, String moneyShouldPlay  )
	{
		type = BS2;
		player1 = p1;
		player2 = p2;
		this.AI_TYPE = AI_TYPE;
		
		this.moneyShouldPlay = moneyShouldPlay;
		moneyToPlay = moneyShouldPlay;
	}
	public void init(Screen screen, PrintStream socketOut)
	{
		player1 = new AIPlayer("AI", AI_TYPE); 
		player2 = new Bs2Player("Bs2",screen);  
		deck = new Bs2Deck(screen,socketOut);  
	}
	
	public void sendVar(GameRecord gr, 
			  PrintStream socketOut,PrintStream resultOut, PrintStream out)
	{
		gameRecord = gr;
		this.socketOut = socketOut;
		this.resultOut = resultOut;
		this.out = out;
	}
	
	public void roundStart()
	{
		socketOut.print(moneyToPlay + "\r");
	}
	public void addMoneyToResult(int gameStatus,int money, int curGame )
	{
		// before replacing , deduct the oldest record.
		gameRecord.latestScore -= gameRecord.latestResult[curGame% GameRecord.LAST_N ];
		
		if (gameStatus== ChessMajian.PLAYER_1_WIN)
		{
			(gameRecord.winTime) ++;
			gameRecord.latestResult[curGame% GameRecord.LAST_N ] = 1;
			
			out.println("Player 1 wins ~~~~~~!!!");
		}
		else if (gameStatus== ChessMajian.PLAYER_2_WIN)
		{
			(gameRecord.loseTime) ++;
			gameRecord.latestResult[curGame% GameRecord.LAST_N ] = -1;
			
			out.println("Player 2 (Opponent) wins ~~~~~~!!!");
			money *= -1;
		}
		
		money -= Integer.parseInt(moneyToPlay);
		gameRecord.winMoney += money;
		gameRecord.moneyGameN[curGame] += money;
		
		resultOut.println("Game "+curGame+": "+"money="+gameRecord.moneyGameN[curGame]);
		detMoneyToPlay(curGame);
	}
	
	void detMoneyToPlay(int curGame)
	{
		gameRecord.latestScore += gameRecord.latestResult[curGame% GameRecord.LAST_N ];
		
		if ( moneyToPlay.equals("1") && (gameRecord.latestScore >= GameRecord.CHANGE_WHEN_GREATEQ) )
		{
			moneyToPlay = moneyShouldPlay;
			resultOut.println("Change (moneyToPlay) to: " + moneyShouldPlay);
		}
		if ( moneyToPlay.equals(moneyShouldPlay) && (gameRecord.latestScore <= GameRecord.CHANGE_WHEN_SMALLEQ) )
		{
			moneyToPlay = "1";
			resultOut.println("Change (moneyToPlay) to: " + "1");
		}
		
	}
	
}
