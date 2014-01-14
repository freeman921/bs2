package Freeman.Chess.Systems;

import java.io.PrintStream;
import java.util.Calendar;

import Freeman.Bs2.MainControl;
import Freeman.Bs2.Screen;
import Freeman.Chess.*;
import Freeman.Chess.Player.AIPlayer;
import Freeman.Chess.Player.Bs2Player;
import Freeman.Chess.Player.Player;
import Freeman.Chess.Structure.Piece;
import Freeman.Chess.Utility.GameRecord;
import Freeman.Chess.Parameters.*;

public class Bs2System extends ChessSystem  
{
	String moneyToPlay;
	PrintStream socketOut,resultOut,out;
	GameRecord gameRecord;
	
	public Bs2System ()  // Player p1, Player p2,int AI_TYPE, String moneyShouldPlay
	{
		type = BS2;
		moneyToPlay = Bs2Params.moneyPerRound;
	}
	public void init(Screen screen, PrintStream socketOut)
	{
		player1 = new AIPlayer("AI", Bs2Params.AI_TYPE); 
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
	
	void giveFirstHand() // only player1 will use it
	{
		player1.clearHand();
		char c;
		int i;
		for (i=3;i<=21;i+=6) // 3 9 15 21
		{
			c = screen.getValueAt(17,i);
			player1.getHand().add( new Piece( Character.toString(c) ));
		}
	}
	
	public void roundStart()
	{
		socketOut.print(moneyToPlay + "\r");
	}
	
	public void addMoneyToResult(int gameStatus,int money, int curGame )
	{
		// before replacing , deduct the oldest record.
		gameRecord.latestScore -= gameRecord.latestResult[curGame% GameRecord.LAST_N ];
		
		if (gameStatus== GameParams.PLAYER_1_WIN)
		{
			(gameRecord.winTime) ++;
			gameRecord.latestResult[curGame% GameRecord.LAST_N ] = 1;
			
			out.println("Player 1 wins ~~~~~~!!!");
		}
		else if (gameStatus== GameParams.PLAYER_2_WIN)
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
			moneyToPlay = Bs2Params.moneyPerRound;
			resultOut.println("Change (moneyToPlay) to: " + Bs2Params.moneyPerRound);
		}
		if ( moneyToPlay.equals(Bs2Params.moneyPerRound) && (gameRecord.latestScore <= GameRecord.CHANGE_WHEN_SMALLEQ) )
		{
			moneyToPlay = "1";
			resultOut.println("Change (moneyToPlay) to: " + "1");
		}
		
	}
	
	int testEnd() // for Bs2 only
	{
		char c,c2;
		c = screen.getValueAt( 24 , 2 );
		
		if (c =='¡»')  // c !=' '
		{
			System.out.println("¡» appeared, curGame ending determine.");
			while (true)
			{
				c = screen.getValueAt( 24 , 5 );
				if (c==Screen.BLANK)
					continue;
				
				if (c=='¬y')
					return GameParams.NO_ONE_WIN;
				else 
				{
					c2 = screen.getValueAt( 24 , 7 );
					if (c=='¹q' && c2=='¸£')
						return GameParams.PLAYER_2_WIN;
					return GameParams.PLAYER_1_WIN;
				}
			}
		}
		else
			return GameParams.STILL_PLAYING;
	}
	
	void endSetting()
	{
		statistics();
		out.close();
		
		if (system.type()==BS2) // exit game
		{
			socketOut.print("\r");
			MainControl.backToMain(socketOut);
		}
	}
	
	void waitCharAt(char waitChar,boolean detMethod, int col, int row)
	{
		// wait for period of time.
		Calendar startTime = Calendar.getInstance();
		while ( true )
		{
			// Timeout check.
			Calendar nowTime = Calendar.getInstance();
			long x = nowTime.getTimeInMillis() - startTime.getTimeInMillis();
			if ( x > SystemParams.TIMEOUT_TIME )
			{
				String errMsg = "Round:"+ChessMajian.curGame+" waitForRefresh : Timeout!";
				ChessMajian.errOut.println(errMsg);
				break;
			}
			
			if ( detMethod==EQUAL && screen.getValueAt(col,row)==waitChar )
				break;
			else if ( detMethod==NOTEQ && screen.getValueAt(col,row)!=waitChar )
				break;
			
			try { Thread.sleep( 0 ); }  // give CPU to context-switch
			catch(InterruptedException e)
			{ System.out.println("!!! Thread Interrupted !!!"); }
			
		}//while
	}
	
	void waitPacket()
	{
		// wait for period of time.
		Calendar startTime = Calendar.getInstance();
		while ( true )
		{
			// Timeout check.
			Calendar nowTime = Calendar.getInstance();
			long x = nowTime.getTimeInMillis() - startTime.getTimeInMillis();
			if ( x > SystemParams.TIMEOUT_TIME )
			{
				String errMsg = "Round:"+ChessMajian.curGame+" waitForRefresh : Timeout!";
				ChessMajian.errOut.println(errMsg);
				break;
			}
			
			//main check : packet arrived
			int stb = screen.getStability();
			if ( stb == Screen.STABLE ) // no new packet arrive
				continue;
			
			// Unstable
			// Delay for a short period to prevent packet been divided.
			try { Thread.sleep( SystemParams.WAIT_TIME ); } 
			catch(InterruptedException e)
			{ System.out.println("!!! Thread Interrupted !!!"); }

			break;
		}		
	}
	
}
