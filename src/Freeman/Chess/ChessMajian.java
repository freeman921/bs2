/*
 * 象棋麻將 人工智慧   ver 3.2  by Freeman 
 * 
 * 本程式任務是 : 打敗BBS(BS2)上的電腦AI並開始賺錢 !!!
 * 
 * 1.5 what's new :	翻新架構, 使AI,Bs2等皆繼承自Player
 * 2.0 what's new :	新增 NormalStrategy (中等強度的AI,依照pattern給定的策略分數去決定牌型優劣)
 * 
 * 3.0 what's new :	1. 	原本BS2判斷換頁  仍需以 Sleep() 作為等待, 現在已大翻修架構, 可加入每頁面check
 * 						目前以  "收到第一個packet -> Delay一小段" 做基礎 , test可跑一千場  
 * 					2.	將專案整理 , 整理進入 Freeman.Chess 中
 * 
 * 3.2 what's new : 開始賺錢了!  此版突破性的功能乃是 : 1. BS2System.detMoneyToPlay()
 * 					因為觀察到BS2象棋麻將系統常有連勝、連敗的現象，以此動態調整賭金   預定金額 & 1元  之間切換
 * 					2.  調整pattern.txt 以吃兵卒為主   ,   並新增一些小功能  ex. 從外部可控制  /stop
 * 
 */

package Freeman.Chess;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

import Freeman.Bs2.MainControl;
import Freeman.Bs2.Screen;
import Freeman.Chess.Structure.*;
import Freeman.Chess.Systems.*;
import Freeman.Chess.Utility.*;


public class ChessMajian extends Thread
{
	public static void main(String arg[]) throws IOException
	{
		new ChessMajian(1000).start(); 
	}
	
	BufferedReader in;
	Screen screen;
	PrintStream out,socketOut,resultOut;
	public static PrintStream errOut;
	
	
	int gameTime;
	public static int demoFlag;
	public static int curGame;
	public static int delayTime;

	
	public Deck deck;
	ChessSystem system;
	Player player1,player2;
	
	//String moneyToPlay;
	String moneyShouldPlay;
	GameRecord gameRecord;
	Printer printer;
	
	
	
	public static final int DRAW=0;
	public static final int EAT=1;
	public static final int THROW=2;
	
	public static final int HUMAN=0;
	public static final int BS2=1;
	public static final int AI=2;
	
	//BS2=1
	
	public static final int EASY_AI=1;
	public static final int NORMAL_AI=2;
	
	public static final int BAIMU =99;
	
	public static final int EARN_MONEY = 0;
	public static final int DEMONSTATE = 1;
	
	public static String RECORD_FILE="record";
	
	ChessMajian(int times) throws IOException
	{
		//system = new LocalSystem(player1,player2);
		gameTime = times;
		
		localInitialize();  // must be done after commonInitialize()
	}
	
	public ChessMajian(	PrintStream socketOut,Screen screen, 
						int times,String money, int demo, int delay) // for Bs2
	{
		system = new Bs2System(player1,player2,money);
		gameTime = times;
		moneyShouldPlay = money;
		demoFlag = demo;
		delayTime = delay;
		
		this.socketOut = socketOut;
		this.screen = screen;
		
		bs2Initialize(screen, this.socketOut); 
	}
	
	/////////////////// initialize  ///////////////////
	
	void commonInitialize()
	{
		Tools.initialize();
		gameRecord = new GameRecord(gameTime);
		
		in = new BufferedReader(new InputStreamReader(System.in) );
		
		
		Date date = new Date();
		SimpleDateFormat dateFm = new SimpleDateFormat("MM-dd__kk-mm-ss");
		String dateStr = (dateFm.format(date)).toString();
		
		try { 
			out = new PrintStream( "Record/Rec_" + dateStr + ".txt" ); 
			errOut = new PrintStream( "Log/Log_" + dateStr + ".txt" );
			resultOut = new PrintStream("Result/Result_"+dateStr+".txt");
			
			if (demoFlag==EARN_MONEY)
			{
				out.close();
				errOut.close();
			}
		}
		catch (FileNotFoundException e) { 
			System.err.println ("RECORD_FILE open wrong");
			return;
		}
		
		system.sendVar(gameRecord,socketOut,resultOut,out);
		printer = new Printer(out);
	}
	
	void bs2Initialize(Screen screen, PrintStream socketOut)
	{
		player1 = new AIPlayer("AI",NORMAL_AI);
		player2 = new Bs2Player("Bs2",screen);
		deck = new Bs2Deck(screen,socketOut);
		commonInitialize(); // must after player type
	}
	
	void localInitialize()
	{
		//out = new PrintStream( System.out );  ???
		
		player1 = new AIPlayer("player1",NORMAL_AI);
		player2 = new AIPlayer("player2",EASY_AI);
		//player2 = new HumanPlayer("Freeman",in,out);
		deck = new LocalDeck();
		commonInitialize(); // must after player type
	}
	
	@Override
	public void run() 
		{ main(); }
	
	public static final int INPUT_MONEY=1;
	public static final int PLAYER1_GET=11;
	public static final int PLAYER1_THROW=12;
	public static final int PLAYER2_GET=13;
	public static final int PLAYER2_THROW=14;
	public static final int STILL_PLAYING=50;
	
	// state > 100  -->  Game ends.
	public static final int END_GAME_BENCHMARK = 100;
	public static final int PLAYER_1_WIN=101;
	public static final int PLAYER_2_WIN=102;
	public static final int NO_ONE_WIN	=103;
	public static final int SOMETHING_WRONG=999;
	
	String choosenMove;
	int throwPos;
	Piece choosenThrowPiece;
	
	boolean stopFlag = false;
	public void stopTheThread() { stopFlag=true; }
	
	void main() 
	{
		for (curGame=1; curGame<=gameTime && stopFlag!=true ; curGame++)
		{
			printer.gameNum(curGame);

			int round=0, state = INPUT_MONEY ;
			int newState=SOMETHING_WRONG ;
			
			// Main state changing Finite State Machine (FSM).
			while (true)
			{
				if (state == PLAYER1_GET )
				{
					round++ ;
					printer.roundInfo(round,player1,player2);
				}
				
				screen.setStateToStable();
				
				/* choose and do movement : will let screen become unstable*/
				/**  determine next newState, and decide (choosenMove)**/ 
				newState = chooseMoveAndSend(state,round);
				
				/* check the environment and screen , whether stable */
				newState = bs2CheckAndWait( state, newState, round );
				if ( newState > END_GAME_BENCHMARK) break; // endGame
				
				if (demoFlag==DEMONSTATE)
					screen.print(); // temp
				
				/* System Process : Change and Set */
				newState = sytemProcess(state,newState);
				
				
				state = newState;
			}
			
			endGame(curGame, newState);
			
		} // for: gameTime
		
		systemEnds();
	} // main() ends.
	
	int chooseMoveAndSend(int state,int round)
	{
		int newState =state;
		
		switch(state)
		{
			case INPUT_MONEY:
				system.roundStart();
				return PLAYER1_GET;
				
			case PLAYER1_GET:
				choosenMove = player1.chooseMove( player2.trash ,round ); // polymorphism 
				if ( (system.type()==BS2) && (player1.type() !=BS2) )
				{
					if (choosenMove=="draw")
						socketOut.write(' ');
					else if (choosenMove=="eat")
						MainControl.down(socketOut);
				}
				return PLAYER1_THROW;
				
			case PLAYER1_THROW:
				chooseThrow(player1, player2.trash, round);
				return PLAYER2_GET;
				
			case PLAYER2_GET:
				// do nothing.
				return PLAYER2_THROW;
			case PLAYER2_THROW:
				// do nothing.
				return PLAYER1_GET;
				
			default:
				break;
		}
		
		return newState;
	}
	
	
	int bs2CheckAndWait( int lastState, int newState, int round )
	{
		String result;
		int endState = SOMETHING_WRONG;
		int endResult;
		/*
		try { Thread.sleep( 20 ); } 
		catch(InterruptedException e)
		{ System.out.println("!!! Thread Interrupted !!!"); }
		*/
		
		switch(lastState)
		{
			case INPUT_MONEY:
				//waitCharAt('│',EQUAL,17,23);
				waitPacket();
				break;
			
			case PLAYER1_GET:
				//waitCharAt('│',EQUAL,17,27);
				waitPacket();
				if ( (endResult=testEnd()) > END_GAME_BENCHMARK )
					endState = endResult;
				break;
				
			case PLAYER1_THROW: 
				//waitCharAt(Screen.BLANK,EQUAL,17,27);
				waitPacket();
				if ( (endResult=testEnd()) > END_GAME_BENCHMARK )
					endState = endResult;
				break; // check if win.
				
			case PLAYER2_GET: 
				choosenMove = player2.chooseMove( player1.trash ,round );
				if ( (endResult=testEnd()) > END_GAME_BENCHMARK )
					endState = endResult;
				break;
				
			case PLAYER2_THROW: 
				if ( (endResult=testEnd()) > END_GAME_BENCHMARK )
				{
					endState = PLAYER_2_WIN;
					break;
				}
				chooseThrow(player2, player1.trash, round);
				break;	
				
			default:
				break;
		}
		
		if ( endState != SOMETHING_WRONG )
			return endState;
		return newState;
	}
	
	public static final boolean EQUAL = true;
	public static final boolean NOTEQ = false;
	
	void waitCharAt(char waitChar,boolean detMethod, int col, int row)
	{
		// wait for period of time.
		Calendar startTime = Calendar.getInstance();
		while ( true )
		{
			// Timeout check.
			Calendar nowTime = Calendar.getInstance();
			long x = nowTime.getTimeInMillis() - startTime.getTimeInMillis();
			if ( x > Tools.TIMEOUT_TIME )
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
			if ( x > Tools.TIMEOUT_TIME )
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
			try { Thread.sleep( Tools.WAIT_TIME ); } 
			catch(InterruptedException e)
			{ System.out.println("!!! Thread Interrupted !!!"); }

			break;
		}		
	}
	
	int sytemProcess(int curState, int newState)
	{
		switch(curState)
		{			
			case INPUT_MONEY: 
				clearAllPiles();
				giveFirstHand();
				break;
				
			case PLAYER1_GET:
				getPieceOperation(choosenMove,player1, player2.trash );
				break;
				
			case PLAYER1_THROW:
				throwPieceOperation(player1);
				getHand4PieceFromBs2();
				break;
				
			case PLAYER2_GET:
				getPieceOperation(choosenMove,player2, player1.trash);
				break;
				
			case PLAYER2_THROW:
				throwPieceOperation(player2);
				break;
			default:
				break;
		}
		
		return newState;
	}
	
	void moneyAndInit()
	{
		if (system.type() ==BS2) // input money
			socketOut.print("1\r");
			//socketOut.print(moneyToPlay + "\r");
		else
			((LocalDeck)deck).reGenerate();
	}
	
	
	int getPieceOperation(String choosenMove, Player player,ChessPile enemytrash ) 
	{
		
		Hand hand = player.hand;
		//String move=null; // draw/eat
			
		if (choosenMove.equals("draw") ) // whether Bs2 or Local
		{
			out.println(player.name + " Draws" );
			Piece p = deck.draw( player.type() );
			/*
			if ( p==null ) // should no need.
			{
				System.out.println("Deck empty !!!!!!!");
				return NO_ONE_WIN;
			}*/
				
			//if ( player.type != BS2 )
			hand.add( p );
		}
		else if ( choosenMove.equals("eat") )
		{
			if (enemytrash.pile.isEmpty() )
				System.err.println("trash Empty !!!");
				
			Piece p = enemytrash.removeByPos( enemytrash.pile.size()-1 );
			if (player.type != BS2 ) 
				hand.add( p );
			out.println(player.name + " Eats " + p.toString() );
		}
		else
		{
			System.err.println("!!! Choice Error !!!");
		}
		
		player.hand.print(out);
		return STILL_PLAYING;
		
	}// getPiece()
	

	void chooseThrow(Player player,ChessPile enemytrash, int round )
	{
		String choice = player.chooseToThrow(enemytrash,round);
		Hand hand = player.hand;
		
		if ( choice.equals("win")  ) // announce winning
		{
			/*
			if ( checkWin(hand)==false )
			{
				System.out.println("!! Fuck you, you didn't win !!");
				return BAIMU;
			}
			*/
			
			if ( hand==player1.hand )
			{
				if (system.type()==BS2)
					socketOut.write('\r'); // notice win.
			}
		
		}
		else
		{
			choosenThrowPiece = new Piece(choice);
			// output movement to Bs2
			if ( system.type()==BS2 && player.type()!=BS2 )
			{
				throwPos= hand.findPiece( choosenThrowPiece.value() );
				if ( throwPos == -1 )
					System.out.println("!! Wrong Input for throw choice !!");
				
				int cursorMove = 5-(throwPos+1);
				for (int i=0;i<cursorMove;i++)
					MainControl.left(socketOut);
				MainControl.up(socketOut);	
			}
			
			out.println(player.name+ " throws out " + choosenThrowPiece );
		}
	}

	void throwPieceOperation(Player player)
	{
		Piece p = player.hand.removeByValue( choosenThrowPiece.value() );
		if ( p.value()==Tools.UNKNOWN )
			System.out.println("Bs2 throw operation.");
		
		player.trash.add( choosenThrowPiece );
		player.printHand(out);
	}
	
	int testEnd() // for Bs2 only
	{
		char c,c2;
		c = screen.getValueAt( 24 , 2 );
		
		if (c =='◆')  // c !=' '
		{
			System.out.println("◆ appeared, curGame ending determine.");
			while (true)
			{
				c = screen.getValueAt( 24 , 5 );
				if (c==Screen.BLANK)
					continue;
				
				if (c=='流')
					return NO_ONE_WIN;
				else 
				{
					c2 = screen.getValueAt( 24 , 7 );
					if (c=='電' && c2=='腦')
						return PLAYER_2_WIN;
					return PLAYER_1_WIN;
				}
			}
		}
		else
			return STILL_PLAYING;
	}
	
	///////////////////  Utilities  ///////////////////
	
	void systemEnds()
	{
		statistics();
		out.close();
		
		if (system.type()==BS2) // exit game
		{
			socketOut.print("\r");
			MainControl.backToMain(socketOut);
		}
	}
	
	void statistics()
	{
		out.println("----------------------");
		/*if (system_type==BS2)
			for (int i=1;i<=gameTime;i++)
				out.println("Game "+i+": "+"money="+gameRecord.moneyGameN[i]);*/
		
		resultOut.println("\n|||||||||||||||||||||||||");
		resultOut.println( (curGame-1)+ " rounds.... ");
		resultOut.println( gameRecord.winTime + " Wins.");
		resultOut.println( gameRecord.drawTime + " Draws.");
		resultOut.println( gameRecord.loseTime + " Loses.");
		resultOut.println( "Money won: " + gameRecord.winMoney );
	}
	
	void endGame(int curGame, int gameStatus)
	{
		//Tools.waitForRefresh(Tools.DET_END_GAME, screen);
		
		if ( gameStatus==SOMETHING_WRONG ) // not Written Good Enough
			errOut.println("Game" +curGame+": SOMETHING_WRONG");
		else if (gameStatus==NO_ONE_WIN)
		{
			(gameRecord.drawTime) ++;
			out.println("Nobody wins , Draw ~~~~~~!!!");
		}
		else
		{
			int money=0;
			if (system.type()==BS2)
			{
				CharSequence cs = screen.getRow(24).subSequence(10,35);
				String moneyStr = cs.toString();
				Scanner sc = new Scanner(moneyStr).useDelimiter("\\D"); // \D=non-digit
				
				while (sc.hasNext() )
				{
					if (sc.hasNextInt())
					{
						money = sc.nextInt();
						break;
					}
					else
						System.out.println( sc.next() ) ;
				}
			}

			system.addMoneyToResult(gameStatus,money,curGame);

		}
		
		//clearAllPiles();
		
		if (system.type()==BS2)
			socketOut.write('\r');
	}
	
	void giveFirstHand()
	{
		if (system.type()==BS2)
			getHand4PieceFromBs2();
		else
		{
			for (int i=0;i<4;i++)
			{
				player1.hand.add( deck.draw(player1.type) );
				player2.hand.add( deck.draw(player2.type) );
			}
		}
	}
	
	void getHand4PieceFromBs2() // only player1 will use it
	{
		player1.clearHand();
		char c;
		int i;
		for (i=3;i<=21;i+=6) // 3 9 15 21
		{
			c = screen.getValueAt(17,i);
			player1.hand.add( new Piece( Character.toString(c) ));
		}
	}
	
	void clearAllPiles()
	{
		player1.clear();
		player2.clear();
	}
	
	// others
	
	void test()
	{
		
		/*
		Piece p = new Piece("車");
		Pattern pat = Tools.getPtListByPiece(p).getLast();
		System.out.println( matches(new Piece("馬"),pat) );
		*/
	}
	
} // ChessMajian






