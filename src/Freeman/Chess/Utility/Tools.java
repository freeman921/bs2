package Freeman.Chess.Utility;
import java.util.*;
import java.io.*;

import Freeman.Bs2.Screen;
import Freeman.Chess.ChessMajian;
import Freeman.Chess.Structure.Pattern;
import Freeman.Chess.Structure.PatternList;
import Freeman.Chess.Structure.Piece;

public class Tools
{
	// HashTables saves only Pointers, 
	// any modifications will directly change the main value
	
	static Hashtable<Integer,String> hash_findString;
	static Hashtable<String,Integer> hash_findValue;
	static Hashtable<Piece,PatternList> hash_findPattern;
	
	PrintStream errOut;
	public static final String PATTERN_FILENAME = "pattern.txt";
	
	public static int WAIT_TIME=5;
	public static final int TIMEOUT_TIME = 3000;
	public static final int BASIC_SCORE_PER_MATCH = 100;
	public static final int WIN = 500;  
	//static final int FOUR_MATCHED = 3000;
	//static final int THREE_MATCHED = 1000;
	//static final int TWO_MATCHED = 100; 
	
	public static final int ERROR_CODE = 999999;
	
	static final int BLK_JIANG 	= 1;
	static final int BLK_SHI 	= 6;
	static final int BLK_SHIAN 	= 8;
	static final int RED_SHUAI 	= 2;
	static final int RED_SHI	= 3;
	static final int RED_SHIAN	= 5;
	static final int BLK_CHE	= 11;
	static final int BLK_MA		= 16;
	static final int BLK_BAO	= 18;
	static final int RED_JU		= 12;
	static final int RED_MA		= 13;	
	static final int RED_PAO	= 15;
	static final int BLK_ZU		= 101;
	static final int RED_BING	= 102;
	
	public static final int UNKNOWN = 999;
	static final int END_GAME	= 9;
	
	public static void initialize()
	{
		if (ChessMajian.delayTime != 0  )
			WAIT_TIME = ChessMajian.delayTime;

		findStringSetup();
		findValueSetup();
		
		findPatternSetup();
		
	}
	
	static void findStringSetup()
	{
		hash_findString = new Hashtable<Integer,String>();
		
		hash_findString.put(BLK_JIANG,"�N");
		hash_findString.put(BLK_SHI,"�h");
		hash_findString.put(BLK_SHIAN,"�H");
		hash_findString.put(BLK_CHE,"��");
		hash_findString.put(BLK_MA,"��");
		hash_findString.put(BLK_BAO,"�]");
		hash_findString.put(BLK_ZU,"��");
		hash_findString.put(RED_SHUAI,"��");
		hash_findString.put(RED_SHI,"�K");
		hash_findString.put(RED_SHIAN,"��");
		hash_findString.put(RED_JU,"��");
		hash_findString.put(RED_MA,"�X");
		hash_findString.put(RED_PAO,"��");
		hash_findString.put(RED_BING,"�L");
		
		hash_findString.put(UNKNOWN,"x");
	}
	static void findValueSetup()
	{
		hash_findValue  = new Hashtable<String,Integer>();
		
		hash_findValue.put("�N",BLK_JIANG);
		hash_findValue.put("�h",BLK_SHI);
		hash_findValue.put("�H",BLK_SHIAN);
		hash_findValue.put("��",BLK_CHE);
		hash_findValue.put("��",BLK_MA);
		hash_findValue.put("�]",BLK_BAO);
		hash_findValue.put("��",BLK_ZU);
		hash_findValue.put("��",RED_SHUAI);
		hash_findValue.put("�K",RED_SHI);
		hash_findValue.put("��",RED_SHIAN);
		hash_findValue.put("��",RED_JU);
		hash_findValue.put("�X",RED_MA);
		hash_findValue.put("��",RED_PAO);
		hash_findValue.put("�L",RED_BING);
		
		hash_findValue.put("x",UNKNOWN);
	}
	
	static void errorLog()
	{
		
	}
	
	static void findPatternSetup()
	{
		hash_findPattern = new Hashtable<Piece,PatternList>();
		
		BufferedReader filein=null ;
		String inputLine;
		
		try { filein= new BufferedReader ( new FileReader(PATTERN_FILENAME) ); }
		catch (FileNotFoundException e) 
		{ 
			ChessMajian.errOut.println("!! PATTERN_FILE not found !!"); 
			return;
		}
		
		// make table by file
		try {
		while ( (inputLine=filein.readLine() )!=null )
		{
			if (inputLine.length()==0)
				continue;
			
			Piece key;
			PatternList pl = new PatternList();
			
			StringTokenizer st = new StringTokenizer(inputLine, "*,");
			// get first token : key
			String ss = st.nextToken().trim();
			key = new Piece( ss ); 
	
			while ( st.hasMoreTokens() ) // med token like "�N �h �H.100 205 320"
			{
				Piece[] array = new Piece[5];
				int [] scoreMatchNum = new int[6];
				StringTokenizer st_med = new StringTokenizer( st.nextToken(), ".");
				
				StringTokenizer st_small = new StringTokenizer( st_med.nextToken(), " \t");
				
				// input piece array
				int ct=0;
				while ( st_small.hasMoreTokens() )
				{
					array[ct]= new Piece( st_small.nextToken() );
					ct++;
				}
				
				// input score for each match number 
				st_small = new StringTokenizer( st_med.nextToken(), " \t");
				for (int i=1;i<=ct;i++)
					scoreMatchNum[i] = Integer.parseInt( st_small.nextToken() );
				
				Pattern pat = new Pattern(array,scoreMatchNum,ct);
				pl.add( pat );
			}
			hash_findPattern.put(key, pl );
			
		}//while 
		inputLine="";
		}catch (IOException IOe) { System.out.println("!! findPatternSetup() wrong !!"); }
	}
	
	public static final int DET_START_MOVE = 1;
	public static final int DET_I_GOT = 2;
	public static final int DET_I_THROWED = 3;
	public static final int DET_END_GAME = 9;
	
	/*
	public static void waitForRefresh(int type,Screen screen) 
	{ waitForRefresh( type,screen,0); }
	
	public static void waitForRefresh(int type,Screen screen, int round) 
	{
		Calendar startTime = Calendar.getInstance();
		
		while ( ChessMajian.system_type == ChessMajian.BS2 )
		{
			Calendar nowTime = Calendar.getInstance();
			long x = nowTime.getTimeInMillis() - startTime.getTimeInMillis();
			
			if ( x > TIMEOUT_TIME )
			{
				String errMsg = "Round:"+ChessMajian.curGame+" waitForRefresh : Timeout!";
				ChessMajian.errOut.println(errMsg);
				break;
			}
			
			char c,c2;
			if ( type==DET_START_MOVE ) 
			{
				c = screen.getValueAt(15,27);   // "��'��'����N�P(�� �� �ߵP)"
				c2 = screen.getValueAt(17,23);  // 4th piece get.
				if ( (c != Screen.BLANK) && (c2 =='�x') )
					break;
				// end
				try { Thread.sleep(WAIT_TIME); } 
				catch(InterruptedException e)
				{ System.out.println("!!! Thread Interrupted !!!"); }
			}
			if ( type==DET_I_GOT ) // "disappers: ���ť���N�P(�� �� �ߵP)"
			{
				c = screen.getValueAt(15,27); 
				if ( c == Screen.BLANK ) 
					break;
			}
			if ( type==DET_I_THROWED ) 
			{
				try { Thread.sleep(WAIT_TIME); } 
				catch(InterruptedException e)
				{ System.out.println("!!! Thread Interrupted !!!"); }
				
				if (round==0)
					System.out.println("!!! round not given !!!");
				c = screen.getValueAt( 13 , 3+(round-1)*4 ); // throwed
				if ( c != Screen.BLANK ) 
					break;
			}
			
			if ( type==DET_END_GAME )
			{
				try { Thread.sleep(WAIT_TIME); } 
				catch(InterruptedException e)
				{ System.out.println("!!! Thread Interrupted !!!"); }
				break;
			}
			
		} // if
	} // waitForRefresh()
	*/
	
	public static PatternList getPtListByPiece(Piece p)
	{
		return hash_findPattern.get(p);
	}
	
	public static String getPieceStr(int v)
	{
		String s= hash_findString.get(v);
		if (s==null)
			System.out.println("hash_findString Error !\n");
		return s;
	}
	public static int getPieceValue(String s)
	{
		int v = ERROR_CODE;
		if (s != null)
			v = hash_findValue.get(s);
		if (v==ERROR_CODE)
			System.out.println("hash_findValue Error !\n");
		return v;
	}
} // Tools
