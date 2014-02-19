package Freeman.BBS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Random;
import java.util.StringTokenizer;

public class MainControl extends Thread
{
	private Screen screen; 
	private Socket socket;
	private PrintStream socketOut,fileOut;
	private BufferedReader in;

	public MainControl(Socket socket) throws IOException
	{
		screen = new Screen(socket); 
		this.socket = socket;
		socketOut = new PrintStream(socket.getOutputStream(),true);
		in = new BufferedReader(new InputStreamReader(System.in));
	}

	@Override
	public void run()
	{
		try
		{
			while (! socket.isClosed() )
			{
				int input = in.read(); // read byte
				//screen.clear();
				
				if (input == '/')  // user give command
				{
					String inputLine = in.readLine();
					StringTokenizer st = new StringTokenizer(inputLine," \t\n\r");
					String tok = st.nextToken().toUpperCase();
					
					if (tok.equals("ASCII"))
						ascii(st);
					else if (tok.equals("PRINTSCREEN") || tok.equals("SCREEN"))
						screen.print();
					else if ( tok.equals("AUTOPOST") ) 
							autoPost();
					//else if ( tok.equals("FISHING") ) 
						//fishingControl();
					//else if ( tok.equals("CHESSGAME") ) 
						//chessGameControl();
					else if ( tok.equals("QUIT") || tok.equals("EXIT")) // end.
							socket.close();
					else if ( tok.equals("LEFT") )
						left(socketOut);
					else if ( tok.equals("UP") )
						up(socketOut);
					else if ( tok.equals("DOWN") )
						down(socketOut);
					else if (tok.equals("MAIN"))
						backToMain(socketOut);
					else
						list();
				}
				else // common char input
					socketOut.write(input); // char
				
				//in.read(); // eats enter
			} // while
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	} // method : rin
	
	
	public static void backToMain(PrintStream socketOut)
	{
		int i;
		
		for (i=0;i<6;i++)
			left(socketOut);
	}
	
	public static void left (PrintStream socketOut)
	{
		socketOut.write(27);
		socketOut.print("OD");
		socketOut.flush();
	}
	public static void up (PrintStream socketOut)
	{
		socketOut.write(27);
		socketOut.print("OA");
		socketOut.flush();
	}
	public static void down (PrintStream socketOut)
	{
		socketOut.write(27);
		socketOut.print("OB");
		socketOut.flush();
	}
	
	void list()
	{
		System.out.println("These commands are available..");
		System.out.println("-- /main : backward to main screen");
		System.out.println("-- /left,up,down : press those keys");
		System.out.println("-- /ascii X : write a single char  -->  X=char | X=^@ (@=a~z) | X=int");
		System.out.println("-- /exit (/quit) : stop the program");
		
		/*
		System.out.println("Special function..\n");
		System.out.println("-- /autopost : auto post a fucking article (random char) on the board where you are");
		*/
		/*
		System.out.println("-- /fishing  : fish users on board with ^u for a period of time");
		System.out.println("		-- /stop  : stop fishing and record to file");
		System.out.println("		-- /write  : write the latest record now , but continue fishing");
		 */
	}
	
	////////////////////////////////////////////////////////////////////////////////////
	
	void ascii(StringTokenizer st)
	{
		String tok;
		if (st.hasMoreTokens())
		{
			tok = st.nextToken();
			if (tok.length()==1 && Character.isLetter(tok.charAt(0)) ) // is an alphabet.
				socketOut.write( (int)tok.charAt(0) );
			else if (tok.length()==2 && tok.charAt(0)=='^')
				socketOut.write( Character.toUpperCase(tok.charAt(1)) -64 );
			else 														// is ascii number
				try { socketOut.write(Integer.parseInt(tok) ); }
				catch (Exception e) 
					{ System.out.print("parameter must be an integer of ASCII Range.."); }
		}
		else
			System.out.print("ASCII wrong..");
		socketOut.flush();
	}
	
	void autoPost() throws IOException
	{
		Random ran = new Random();
		int i, n, x;

		System.out.print("U need to start at the board's homepage,");
		System.out.print(" Are you?  y/n \n");
		x = in.readLine().charAt(0);
		if (x == 'n' || x == 'N')
		{
			System.out.println("Resume to surf the BBS");
			return;
		}

		System.out.print("How many words u wanna print? : ");
		n = Integer.parseInt(in.readLine());

		// 
		socketOut.write(16); // ^P
		socketOut.print("\nThis is 廢文  很長很可怕\n");

		for (i = 0; i < n; i++)
		{
			x = Math.abs(ran.nextInt()) % 25 + 'A';
			socketOut.write(x);
			if (i % 77 == 0)
				socketOut.write('\n');

			try
			{
				if (i % 1000 == 0)
					Thread.sleep(2000);
				else
					Thread.sleep(100);
			} catch (InterruptedException e){}
		}

		socketOut.write(24); // ^X
		socketOut.print("L\n");

		// under.. is for temp use
		try { Thread.sleep(10000); } 
		catch (InterruptedException e){}

		socketOut.write(24); // ^X
		socketOut.print("L\n\n");
		socketOut.write(25); // ^Y

		System.out.println("Printing Accomplished..");

	} // autoPost
} // class MainControl
