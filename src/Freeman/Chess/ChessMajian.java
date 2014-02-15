/**

 * 1.5 new : ½�s�[�c, ��AI,Bs2�����~�Ӧ�Player
 * 2.0 new : �s�W NormalStrategy (�����j�ת�AI,�̷�pattern���w���������ƥh�M�w�P���u�H)
 * 
 * 3.0 new : 
 * 		1. 	�쥻BS2�P�_����  ���ݥH Sleep() �@������, �{�b�w�j½�׬[�c, �i�[�J�C����check
 * 			�ثe�H  "����Ĥ@��packet -> Delay�@�p�q" ����¦ , test�i�]�@�d��  
 * 		2.	�N�M�׾�z , ��z�i�J Freeman.Chess ��
 *  
 * 3.2 new : 
 * 		�}�l�ȿ��F!  ������}�ʪ��\��D�O : 1. BS2System.detMoneyToPlay()
 * 		�]���[���BS2�H�ѳ±N�t�α`���s�ӡB�s�Ѫ��{�H�A�H���ʺA�վ���   �w�w���B & 1��  ��������
 * 		2.  �վ�pattern.txt �H�Y�L�򬰥D   ,   �÷s�W�@�Ǥp�\��  ex. �q�~���i����  /stop
 * 
 * 3.3 new :
 * 		�s�W�p�\��: �b�}�Y�]�w�����ʥ]���������: delayTime
 * 		�̾ڶZ��Bs2�����񩵿�, �U���ƭȷU�p, �i��ping����
 **/

package Freeman.Chess;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

import Freeman.Bs2.MainControl;
import Freeman.Bs2.Screen;
import Freeman.Chess.Parameters.*;
import Freeman.Chess.PlayerType.AIPlayer;
import Freeman.Chess.PlayerType.Player;
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
	
	int totalRounds;
	public static int delayTime;
	
	MainProc mainProc;
	ChessSystem system;
	//Player player1,player2;
	
		//String moneyToPlay;
		//String moneyShouldPlay;
	GameRecord gameRecord;
	
	public static final int DRAW=0;
	public static final int EAT=1;
	public static final int THROW=2;
	
	public static final int HUMAN=0;
	public static final int BS2=1;
	public static final int AI=2;
	
		//public static final int BAIMU =99;
		//public static String RECORD_FILE="record";
	
	ChessMajian(int times) throws IOException
	{
		/* For Local
		//system = new LocalSystem(player1,player2);
		gameTime = times;
		
		localInitialize();  // must be done after commonInitialize() ??????
		commonInitialize();
		*/
	}
	
	// Constructor for Bs2
	public ChessMajian(	PrintStream socketOut,Screen screen, 
						int times,String money, int demo, int delay) 
	{
		system = new Bs2System(screen);
		totalRounds = times;
		Bs2Params.moneyPerRound = money;
		delayTime = delay;
		Bs2Params.demoFlag = demo;
		
		this.socketOut = socketOut;
		this.screen = screen;
		
		system.init(this.socketOut);
		commonInitialize();
	}
	
	/////////////////// initialize  ///////////////////
	
	void commonInitialize()
	{
		Tools.initialize();
		gameRecord = new GameRecord(totalRounds);
		
		in = new BufferedReader(new InputStreamReader(System.in) );
		
		
		Date date = new Date();
		SimpleDateFormat dateFm = new SimpleDateFormat("MM-dd__kk-mm-ss");
		String dateStr = (dateFm.format(date)).toString();
		
			//system.sendVar(gameRecord,socketOut,resultOut,out);
		myIO = system.getIO();
	}
	
	@Override
	public void run()
	{ 
		mainProc = new MainProc(system,totalRounds);
		mainProc.main();
	}
	
	
	
	public static final boolean EQUAL = true;
	public static final boolean NOTEQ = false; // Not Equal
	
	public void stopTheThread() { mainProc.stopTheThread(); }
	
	///////////////////  Utilities  ///////////////////
	
	// others
	
	void test()
	{
		
		/*
		Piece p = new Piece("��");
		Pattern pat = Tools.getPtListByPiece(p).getLast();
		System.out.println( matches(new Piece("��"),pat) );
		*/
	}
	
} // ChessMajian


/*
int getPieceOperation(String choosenMove, Player player,ChessPile enemytrash ) 
{
	
	Hand hand = player.getHand();
	//String move=null; // draw/eat
		
	if (choosenMove.equals("draw") ) // whether Bs2 or Local
	{
		out.println(player.name + " Draws" );
		Piece p = system.getDeck().draw( player.type() );
		/--
		if ( p==null ) // should no need.
		{
			System.out.println("Deck empty !!!!!!!");
			return NO_ONE_WIN;
		}--/
			
		//if ( player.type != BS2 )
		hand.add( p );
	}
	else if ( choosenMove.equals("eat") )
	{
		if (enemytrash.pile.isEmpty() )
			System.err.println("trash Empty !!!");
			
		Piece p = enemytrash.removeByPos( enemytrash.pile.size()-1 );
		if (player.type() != BS2 ) 
			hand.add( p );
		out.println(player.name + " Eats " + p.toString() );
	}
	else
	{
		System.err.println("!!! Choice Error !!!");
	}
	
	player.getHand().print(out);
	return STILL_PLAYING;
	
}// getPiece()


void chooseThrow(Player player,ChessPile enemytrash, int round )
{
	String choice = player.chooseToThrow(enemytrash,round);
	Hand hand = player.getHand();
	
	if ( choice.equals("win")  ) // announce winning
	{
		/--
		if ( checkWin(hand)==false )
		{
			System.out.println("!! Fuck you, you didn't win !!");
			return BAIMU;
		}
		--/
		
		if ( hand==player1.getHand() )
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
	Piece p = player.getHand().removeByValue( choosenThrowPiece.value() );
	if ( p.value()==Tools.UNKNOWN )
		System.out.println("Bs2 throw operation.");
	
	player.getTrashPile().add( choosenThrowPiece );
	player.printHand(out);
}
*/	

