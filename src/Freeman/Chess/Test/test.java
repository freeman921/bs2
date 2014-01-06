package Freeman.Chess.Test;

import java.io.*;
import java.util.*;
import java.text.*;

class A
{
	A() { System.out.println("A space"); }
	A(String s) { System.out.println("A string"); }
}
class B extends A
{
	B(String s){ System.out.println("B string"); }
}

public class test
{
	public static void main(String arg[]) throws IOException {
		new test().main();
	}

	void main() throws IOException 
	{ 
		new B("B");
	}

	void pushBack()
	{
		PushbackInputStream pbIn = new PushbackInputStream( System.in );
		InputStreamReader isIn = new InputStreamReader (pbIn);
		PrintStream out=null ;//= new PrintStream("haha.txt");
		
		char x=0;
		
		out.write(27);
		out.print( "[1;25;33m" );
		out.write(27);
		out.print( "[15;50Hx" );
		out.write(27);
		out.print( "[15;19H " );
		
		int sum=0;
		//while (true)
		{
			x = (char)isIn.read();
			System.out.print( x );
		}
			//pbIn.unread(x);
		//System.out.println( sum );
		/*
		int choice[]= {0,3,0};
		
		choice = {0,2,0};
		System.out.print(choice[1]);
		
		Date date = new Date();
		SimpleDateFormat dateFm = new SimpleDateFormat("EEEE/MMMM/dd/kk:mm:ss");
		System.out.println(dateFm.format(date));
	}
	
	void backUpState()
	{
		/*
		switch(state)
		{
			case INPUT_MONEY:
				
				break;
				
			case PLAYER1_GET:
				
				break;
				
			case PLAYER1_THROW:
				break;
			case PLAYER2_GET:
				break;
			case PLAYER2_THROW:
				break;
			default:
				break;
		}
		*/
	}
	
}
