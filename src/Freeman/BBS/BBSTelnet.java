/*	
 *  BBS Telnet Program   ver 2.1    by Freeman
 *  Modified from TonyTony Jian's(NCTU) Basic telnet Program
 *  
 *  the project is mainly for "fisihng" , 
 *  therefore the interfaces are not really user-friendly.
 *  I've planned to make the GUI , but gave up at last cuz too lazy XD
 *  
 *  現在則做為象棋麻將的入口程式
 *  
 *  
 *  Program Introduction:
 *  
 *  - class "MainControl" is the main control unit by the user
 *  
 *  - class "Screen" is to divide strings received from server  
 *    into tokens of screens , makes it easy to determine the scope of one screen. 
 *  
 *  - use "Screen.ScreenInput" Thread to receive BBS messages from server simultaneously
 *  
 *  
 *  --------- Version info ---------
 *  
 *  2.0 ---->  change the fishing structure to  ctrl(main) <-> fish(thread)
 *  
 *  2.1 ---->  let Screen be the inner of MainControl
 *  	名詞更改 :
 *  	1. class SocketInput -> Screen.ScreenInput
 *  	2. class SocketOuput -> MainControl
 *  
 *  --------- problems --------
 *  
 *  This version did not support the fishing function yet.
 *  
 */

package Freeman.BBS;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;

public class BBSTelnet
{
	
	public static void main(String[] args) throws IOException
	{
		String hostName;
		int port;
		InetAddress address;
			//BufferedReader in;
		
			//in = new BufferedReader(new InputStreamReader(System.in));
		if (args.length == 2)
		{
			hostName = args[0];
			port = Integer.parseInt(args[1]);
		}
		else
		{
			hostName = "ptt.cc";// in.readLine();
			port = 23; // Integer.parseInt(in.readLine());
		}

		try
		{
			address = InetAddress.getByName(hostName);
			try
			{
				Socket socket = new Socket(address, port);
				//new SocketInput(socket).start();
				new MainControl(socket).start();
			} catch (IOException e)
			{
				System.err.println("Connection failed");
			}
		} catch (UnknownHostException e)
		{
			System.err.println("Unknown host");
		}
	}
}





