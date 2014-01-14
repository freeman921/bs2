package Freeman.Chess;

import java.util.Scanner;

import Freeman.Bs2.MainControl;
import Freeman.Chess.Parameters.GameParams;
import Freeman.Chess.Structure.Piece;
import Freeman.Chess.Systems.ChessSystem;

public class MainProc 
{
	public static final int INPUT_MONEY=1;
	public static final int PLAYER1_GET=11;
	public static final int PLAYER1_THROW=12;
	public static final int PLAYER2_GET=13;
	public static final int PLAYER2_THROW=14;
	public static final int STILL_PLAYING=50;
	
	ChessSystem system;
	
	String choosenMove;
	int throwPos;
	Piece choosenThrowPiece;
	
	public static int curGame;
	
	boolean stopFlag = false;
	public void stopTheThread() { stopFlag=true; }
	
	public MainProc(ChessSystem system)
	{
		this.system = system;
	}
	
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
		
		system.ends();
	} // main() ends.
	
//-----------------------------------------------------------//
//                  The 3 main functions                     //
//-----------------------------------------------------------//
	
	int chooseMoveAndSend(int state,int round)
	{
		int newState =state;
		
		switch(state)
		{
			case INPUT_MONEY:
				system.roundStart();
				return PLAYER1_GET;
				
			case PLAYER1_GET:
				choosenMove = player1.chooseMove( player2.getTrashPile() ,round ); // polymorphism 
				if ( (system.type()==BS2) && (player1.type() !=BS2) )
				{
					if (choosenMove=="draw")
						socketOut.write(' ');
					else if (choosenMove=="eat")
						MainControl.down(socketOut);
				}
				return PLAYER1_THROW;
				
			case PLAYER1_THROW:
				chooseThrow(player1, player2.getTrashPile(), round);
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
		
	} // chooseMoveAndSend() ends
	
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
				//waitCharAt('¢x',EQUAL,17,23);
				waitPacket();
				break;
			
			case PLAYER1_GET:
				//waitCharAt('¢x',EQUAL,17,27);
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
				choosenMove = player2.chooseMove( player1.getTrashPile() ,round );
				if ( (endResult=testEnd()) > END_GAME_BENCHMARK )
					endState = endResult;
				break;
				
			case PLAYER2_THROW: 
				if ( (endResult=testEnd()) > END_GAME_BENCHMARK )
				{
					endState = PLAYER_2_WIN;
					break;
				}
				chooseThrow(player2, player1.getTrashPile(), round);
				break;	
				
			default:
				break;
		}
		
		if ( endState != SOMETHING_WRONG )
			return endState;
		return newState;
		
	} // bs2CheckAndWait() ends
	
	int sytemProcess(int curState, int newState)
	{
		switch(curState)
		{			
			case INPUT_MONEY: 
				clearAllPiles();
				system.giveFirstHand();
				break;
				
			case PLAYER1_GET:
				getPieceOperation(choosenMove,player1, player2.getTrashPile() );
				break;
				
			case PLAYER1_THROW:
				throwPieceOperation(player1);
				getHand4PieceFromBs2();
				break;
				
			case PLAYER2_GET:
				getPieceOperation(choosenMove,player2, player1.getTrashPile());
				break;
				
			case PLAYER2_THROW:
				throwPieceOperation(player2);
				break;
			default:
				break;
		}
		
		return newState;
	}
	
//-----------------------------------------------------------//
//  					Sub functions                    	 //
//-----------------------------------------------------------//
	
	void clearAllPiles()
	{
		player1.clear();
		player2.clear();
	}
	
	void endGame(int curGame, int gameStatus)
	{
		//Tools.waitForRefresh(Tools.DET_END_GAME, screen);
		
		if ( gameStatus==GameParams.SOMETHING_WRONG ) // not Written Good Enough
			errOut.println("Game" +curGame+": SOMETHING_WRONG");
		else if (gameStatus==GameParams.NO_ONE_WIN)
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
	
} // class MainProc
