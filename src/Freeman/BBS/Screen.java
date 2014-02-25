package Freeman.BBS;
import java.io.*;
import java.net.Socket;
import java.util.*;
import java.io.PushbackInputStream;

class User
{
	String id;
	String nickname;
	String loginTime;
	
	User(String a,String b,String c)
	{
		id =a;
		nickname = b;
		loginTime = c;
	}
}

public class Screen 
{
	public static final int  BBS_ROW_NUM = 24;
	public static final int  WORD_PER_LINE = 80;
	public static final int  COMMON_PARAM_MAXNUM = 3;
	public static final int  SCREEN_REAL_WIDTH = WORD_PER_LINE *10;
	
	public static final int INIT = 0;
	public static final int CTRL_SIG = 1;
	public static final int PAR_LIST = 5;
	public static final int SEMICOLON = 9;
	public static final int ERROR = 999;
	public static final char BLANK = ' ';
	
	public static final int STABLE = 1;
	public static final int UNSTABLE = 2;
	
	public static final String CLEAR_LINE="                        ";
	
	private Socket socket;
	//private BufferedReader socketIn;
	
	int refresh_state= STABLE; // to determine whether screen refreshed.
	String line = new String(); 
	StringBuffer[] scrBuf = new StringBuffer[SCREEN_REAL_WIDTH];
	
	int x,lineNum=1;
	
	Screen(Socket socket)
	{
		this.socket = socket;
		for (int i=1; i<=BBS_ROW_NUM; i++)
		{
			scrBuf[i] = new StringBuffer(WORD_PER_LINE*2); //init capacity
			scrBuf[i].setLength(WORD_PER_LINE*10);
		}
		
		new ScreenInput().start();  
		//catch (IOException e) { System.out.println("!!! Screen Construct Wrong !!!"); }
	}
	
	public void setStateToStable () { refresh_state = STABLE; }
	public int getStability() { return refresh_state; }
	public char getValueAt(int a,int b) { return scrBuf[ a ].charAt( b ); }
	public StringBuffer getRow(int row) { return scrBuf[row]; }
	
	public void clear() // how to clear ????????
	{
		lineNum = 1;
		for (int i=1 ;i<= BBS_ROW_NUM ; i++)
		{
			//scrBuf[i] = new StringBuffer(CLEAR_LINE);
			scrBuf[i].setLength(0);  	// clear line
			scrBuf[i].setLength(SCREEN_REAL_WIDTH);
		}
		
		//System.out.print("---");
	}
	public void print()
	{
		int i,k;
		System.out.println("--------------------");
		for (i=1;i<=BBS_ROW_NUM;i++)
		{
			System.out.print(i +":_");
			if (i<10)
				System.out.print('_'); // to align
			
			for(k=1; k<= WORD_PER_LINE ; k++) // scrBuf[i].length()
				if (scrBuf[i].charAt(k) != 0 )
					System.out.print( scrBuf[i].charAt(k) );
				else	
					System.out.print (BLANK);
			
			System.out.println("");
		}
		System.out.println("--------------------");
	}
	
	class ScreenInput extends Thread
	{
		int ptr_x, ptr_y;
		int state,par_num,par[]=new int[6] ; 
		// 3 is enough in theory? but I've saw 4  *[0;1;37;44m
		
		PushbackInputStream pbIn;
		InputStreamReader isrIn;
		BufferedReader bufPbIn,fileIn;
		Scanner scanner;
		
		ScreenInput()
		{
			ptr_x=1;   ptr_y=1; 
			backToInitState(); // state and it's setting
			
			try {
				pbIn = new PushbackInputStream( socket.getInputStream() );
				isrIn = new InputStreamReader (pbIn);
				//fileIn = new BufferedReader(new FileReader("haha.txt") );
				//bufPbIn = new BufferedReader ( new InputStreamReader (pbIn) );
				//scanner = new Scanner(pbIn);
			}
			catch (IOException e) { System.out.println("!!! Pushback Reader Wrong !!!"); }
		}
		
		@Override
		public void run()
		{
			try {	
				int x;
				while (  ( x = isrIn.read() ) !=  -1) // EOF
				{
					refresh_state = UNSTABLE;
					System.out.print((char)x); // for user to know where he is = = 
					putToScreen(x);
				}
				socket.close();
			} 
			catch (IOException e)
			{
				System.out.println(e.toString());
			}
		}
		
		public static final int NOT_SET = -1;
		
		// this is a State Machine
		void putToScreen(int x) 
		{
			//while (true)
			//{
				switch (state)
				{
					case INIT:
						state_INIT(x);
						break;
						
					case CTRL_SIG:
						if (x=='[')
							state = PAR_LIST;
						//else if (x=='*') // **A
							//state = STAR_CTRL;
						else
						{
							System.out.print("!!! CTRL_SIG wrong !!!");
							backToInitState();
						}
						break;
					
					case PAR_LIST:
						state_PAR_LIST(x);
						break;
					
					default:
						System.out.println("!!! State machine wrong !!!");
						backToInitState();
				} // switch
				//break;
			//} //while(true)
		} // putToScreen()
		
		void state_INIT(int x)
		{
			if ( x==27 ) // esc = *
				state = CTRL_SIG;
			else if (x=='\r')
				ptr_x = 1;
			else if (x=='\n')
			{
				if (ptr_y >=24)
					System.out.println("!!! ptr_y overwhelm !!!");
				else
					ptr_y ++;
			}
			else if (x==8) // back space with no delete
			{
				if (ptr_x >0) 
					ptr_x --;
				else
					System.out.println("!!! backspace go over ptr_x 1 !!!");
			}
			
			// all others input -> put to screen
			else 
			{
				if ( ptr_x < WORD_PER_LINE*10 )
				{
					scrBuf[ptr_y].setCharAt(ptr_x, (char)x );
					if (x<128) 
						ptr_x ++;
					else // not ascii  like Big5
					{
						scrBuf[ptr_y].setCharAt(ptr_x+1, (char)0 );
						ptr_x += 2;
					}
				}
				else
					System.out.print("!!! ptr_x too long !!!");	
			}
		}
		
		void state_PAR_LIST(int x)
		{
			if ( Character.isDigit( (char)x ) )
			{
				Int a = new Int(x);
				// ends and returns a as the next x (not a digit)
				par_num++;
				par[par_num] = inputInt(a); 
				
				// 2nd call
				putToScreen(a.value);
				
				//x = a.value;
					//continue;   ahh ! have to send x back to main func
			}
			else if (x==';') ;
				// do nothing
				//	org : par_num ++ ;
			
			// A telnet control code
			else if ( Character.isAlphabetic( (char)x) )
			{
				for (int i=COMMON_PARAM_MAXNUM; i>0; i--) // from big param to small
				{
					if ( par[i] != NOT_SET )
					{
						par_num = i;
						break;
					}
				}
				
				// CODE TYPES
				
				if (x=='K') // clear line code  //NOT DONE
				{
					if (par[1]==0) // *[0K
						clearCurLineFromCursor();
				}
				
				else if (x=='J') // clear screen code //NOT DONE
				{
					if (par[1]==2)
						clear();  // screen clear
				}
				
				else if (x=='m') ; // color code -> don't bother

				else if (x=='H') // move code
				{
					if ( par_num == 0 ) 
					{
						ptr_x = 1;
						ptr_y = 1;
					}
					else if ( par_num == 1 )
					{
						ptr_y = par[1];
						ptr_x = 1;
					}
					else if ( par_num == 2 )
					{
						ptr_y = par[1];
						ptr_x = par[2];
					}
					else
						System.err.println("!! Wrong : code 'H' param# !!");
				}
				else  // not a special
					;
				
				backToInitState();
			} // else : telnet control code
			
			else if ( x==27 ) // esc = *
				state = CTRL_SIG;
			else
			{
				System.err.print("!!! 有奇怪的東西喔 !!! : ");
				System.err.println( (x>0 & x<127)? (char)x:"" );
				
				backToInitState();
			}
		}
		
		int inputInt(Int num)
		{
			int sum = 0, x=num.value ;
			while ( Character.isDigit( (char)x ) )
			{
				sum *= 10;
				sum += x-'0';
				
				try { x = isrIn.read(); } 
				catch (IOException e) { e.printStackTrace(); }
				System.out.print((char)x);
			}
			num.value = x;
			return sum;
		}
		
		void backToInitState()
		{
			// default setting
			for(int i=1;i<=COMMON_PARAM_MAXNUM;i++)
				par[i]= NOT_SET ;
			par_num = 0;
			state = INIT;
		}
		void clearCurLineFromCursor()
		{
			for(int i=ptr_x ; i<=WORD_PER_LINE  ; i++)
				scrBuf[ptr_y].setCharAt(i, BLANK);
		}
		
	} // class ScreenInput
	
} // screen


class ScreenIterator
{
	Screen scr;
	int rowNum;
	int pos; 
	
	ScreenIterator()
	{
		scr = null;
		rowNum = 1;
		pos = 0;
	}
	ScreenIterator(Screen a,int x,int y)
	{
		scr = a;
		rowNum = x;
		pos = y;
	}
	
}

class ScreenTokenizer
{
	public static final int  BBS_ROW_NUM = 24;
	int i,k;
	String pattern;
	Screen sc;
	
	ScreenTokenizer(Screen sc , String pattern)
	{ 
		this.sc = sc;
		this.pattern = pattern; 
		i=1; k=0;
	}
	
	ScreenIterator findNextPattern()
	{

		for ( ;i<=BBS_ROW_NUM ; i++)
		{
			int index = sc.scrBuf[i].indexOf(pattern,k); // find pattern in String from k
			
			if (index != -1)
			{
				k = index+1;
				return new ScreenIterator(sc,i,index);
			}
			k=0;
		}
		// not find index==-1
		return new ScreenIterator(null,-1,-1);
	}
	
	String afterStrings(ScreenIterator si)
	{
		return sc.scrBuf[si.rowNum].substring(si.pos+pattern.length() );
	}
}

class UserComp implements Comparator <User>
{
    public int compare(User a, User b)
    {
    	int ans = a.id.compareTo(b.id);
        return ans;
    }
}

class Int
{
	int value;
	Int(int x) { value=x; }
}


