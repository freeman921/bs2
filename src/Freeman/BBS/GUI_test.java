////// 此程式可用來做聊天室的 GUI介面, 可與之前公佈的 HaLa.java
//////// 搭配測試, 也可以與前面 ChatServer.java 搭配測試
//////// 多開幾個窗, 或用 javaw Client2 port_number 多跑幾份,
/// 甚至可多找幾個同學連到同一 Server 測試 (當然要先把 ChatServer 跑起來)
/////////
////// http://www.csie.nctu.edu.tw/~tsaiwn/course/java/examples/network/
//Client2.java -- a Window client to communicate with a Server
//Original From: Lin Se-Ying <sylin@csie.nctu.edu.tw>
//         Date: Sat, 9 Jun 2001 14:32:40 +0000 (UTC)
//> (5) 因為用 telnet 直接連此server 很難用, 打字時隨時會被message打斷行
//>      如果寫一個 Client 就會覺得很好用
//>  用Java 寫 application program, 使得你打字的部份和大家聊天部份分開
//>     可以使用 TextArea, 當然是要能夠捲回去看的,
//>     且執行時可讓使用者指定 server 和 port
// original by 8917008 林仕盈
// modified by tsaiwn@csie.nctu.edu.tw
//
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
//import javax.swing.*;       // no Swing component used now

public class GUI_test {
   private final static int DEFAULT_PORT = 6789;
   private static Frame f;  // the only one "static" Object access by main
   private Panel pan;  // pan is the Panel to host all components
   private TextArea display;
   private TextField keyPad;
   private Label label;
   private Panel pSouth;  // South Panel to host label + keyPad (TextField)
   private Panel helpPanel = new Panel(new BorderLayout( ) );  //help msg

 // variables for the Socket connection
   private  Socket sock;
   private  InetAddress host;
   private  int port;
   private  BufferedReader in;
   private  PrintWriter out;

 // some Flags for thread communication
   private volatile boolean quit = false; 
   private volatile boolean sockFail = false;
   private volatile boolean readyToReConnect = true;
   private volatile boolean helpMode = false;
   private String message = "";

   public GUI_test(InetAddress host, int port)
   {
       this( );   // call the other Constructor with no argument
       this.host = host;    // from main
       this.port = port;
   }

   public GUI_test( )
   {
      pan = new Panel( );   // to hold all components, simulate Applet
      keyPad = new TextField();
      keyPad.setEnabled( false );
      keyPad.setBackground(new Color(238,238,250));
      keyPad.setFont(new Font("細明體", Font.BOLD, 32) );
      keyPad.addActionListener(
         new ActionListener() {
            public void actionPerformed( ActionEvent e )
            {
               String msg = e.getActionCommand();
               if(msg.equals("/quit") ) {
                   if(quit == true) System.exit(0);  // quit twice
                   quit = true;   // mark as finish
                   showHelp( );
               }
               sendData( msg );
               if( (quit == true) && msg.startsWith("/") ) {
                   tryToConnect(msg);  // try to obtain host and port
                   if(readyToReConnect) {
                       label.setBackground(Color.white);
                       label.setText("輸入:");
                       pan.validate( );  // tell LayoutManager
                       //init( );   //start( );  // see tail in main( )
                   }
               } // if
               keyPad.setText("");   // clear the TextField
               //keyPad.selectAll(); //keyPad.cut();   // JTextField
            } // actionPerformed
         }
      );
    //// TextArea for display the dialog
      display = new TextArea();  display.setEditable(false);
      display.setFont(new Font("細明體", Font.BOLD, 18) );
      display.setBackground(new Color(198,198,238));
      ScrollPane sc = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
      sc.add(display);   // Scroll when necessary
      pan.setLayout(new BorderLayout( ) );
      pan.add( sc, BorderLayout.CENTER );
     /// prepare the input TextField
      Panel pSouth = new Panel(new BorderLayout() );
      label = new Label("輸入:");
      label.setFont(new Font("標楷體", Font.BOLD, 24) );
      pSouth.add(BorderLayout.WEST, label);
      pSouth.add(keyPad,BorderLayout.CENTER);
      pan.add( pSouth, BorderLayout.SOUTH );
      f.add(pan, "Center");  // a Frame (FORM) from main( )
   } // Client2 Constructor

   public void init( ) 
   {
     //////  try to connect to the server
      try {

         // Step 1: Create a Socket to make connection.
         display.setText("Attempting connection to "+host+
                 " at port "+port+"\n");
         sockFail = false;
         sock = new Socket( host, port );
         display.append( "Connected to: " +
             sock.getInetAddress().getHostName()+":"+ sock.getPort() );

         // Step 2: Get the input and output streams from the Socket.
         in = new BufferedReader(
                  new InputStreamReader(sock.getInputStream()) );
         out = new PrintWriter(
                  new OutputStreamWriter(sock.getOutputStream()), true );
         display.append( "\nGot I/O streams\n" );
      } catch ( IOException e ) {
         e.printStackTrace();
         quit=true;  warnUser( ); showHelp( );
         sockFail = true;   // fail to build the connection
      }
   } // init

   public void start( )
   {
      try {
         // Step 3: Process connection.
         keyPad.setEnabled( true );
         keyPad.requestFocusInWindow( );   // focus on the input TextField
         //keyPad.requestFocus( );  //use this if no requestFocusInWindow
         quit=false;
         try {
            while( (message = in.readLine())!=null ) {
               display.append( message+"\n" );
               display.setCaretPosition(
                  display.getText().length() );
               if(quit) {
                  showHelp( );    // tell him that he can re-connect
                  break;  // user wants to quit
               }
               // if(message.equals("tiuq/")) break;  // "/quit"
            } // while
         } catch ( IOException e ) {
            display.append("\nIOException received\n ---"+e);
         }
         //keyPad.setEnabled( false );
         display.append( "Connection closed.\n" );
         out.close(); in.close();
         sock.close();
      } catch ( EOFException e ) {
         System.out.println( "Server terminated connection.");
         display.append("\noohp -- Server terminated connection!");
      } catch ( IOException e ) {
         e.printStackTrace();
      }
   } // start

   private void sendData( String s )
   {
      //if( s.length() == 0) return ;    // Blank Line
      out.println( s );
      display.append( s + "\n");
      out.flush();   // ensure nothing remain in the output Buffer
   } // sendData

   void removeHelp( ) 
   {
      if(helpMode == false) return;   // there is no help panel
      helpMode = false;
      f.remove(helpPanel);
      f.validate( );
   }

   void showHelp( ) 
   {
      if(helpMode == true) return;   // already in help mode
      Label nLeft =
       new Label("/quit to quit, or to re-connect to host at port, use: ");
      nLeft.setForeground(Color.blue);
      nLeft.setFont(new Font("New Roman", Font.BOLD, 20) );
      Label note = new Label(" /connect host port");
      note.setForeground(Color.red);
      note.setFont(new Font("Ludica Console", Font.BOLD, 24) );
      helpPanel.add(nLeft, BorderLayout.WEST);
      helpPanel.add(note, BorderLayout.CENTER);
      display.append( "\nTo reconnect, use the following command:\n");
      display.append( "\n/connect host port\n");
      f.add(helpPanel, BorderLayout.SOUTH);    // add the help Panel
      helpPanel.setVisible(true);
      helpMode = true;
      f.validate( );  // tell the LayoutManager to doLayout now
   }

   void warnUser( ) 
   {
      label.setText("斷線中");  label.setBackground(Color.red);
      pan.validate( );   // pSouth contains label and keyPad
   }

   private void tryToConnect(String cmd) 
   {
      // parse the command to find out the host and port
      // then try to connect it
      readyToReConnect = false;  // assume wrong hostname or port#
      warnUser( );
      cmd = cmd.toLowerCase( ); 
      if(! cmd.startsWith("/conn") ) return; // not connect cmd
      java.util.StringTokenizer stk=new java.util.StringTokenizer(cmd,"\t ");
      if(stk.countTokens( ) != 3 ) return;   //format: /connect host port
     /// assume Locahost port 6789
      String msga = "127.0.0.1";
      String msgp = "0";
      msga = stk.nextToken( );   // first token, no use now
      msga = stk.nextToken( );   // second token is the hostname
      msgp = stk.nextToken( );    // third token is the port
      try {
         host = InetAddress.getByName( msga );
         port = Integer.parseInt( msgp );
         if(port == 0) port = DEFAULT_PORT;
         readyToReConnect = true;   // mark for re-connection
      } catch ( Exception e ) {
         display.append( "\nIllegale host / port.\n" );
      }
      return;
   } //tryToConnect

   public static void main( String args[] )
   {
      InetAddress host=null;
      int port = 0;
      if ( args.length == 0 ) {
          System.out.println("Usage: java Client2 hostname port");
          System.exit(0);
      }
      try {
         host = InetAddress.getByName( args[0] );
         if(args.length > 1) port = Integer.parseInt( args[1] );
         if(port == 0) port = DEFAULT_PORT;
      } catch ( UnknownHostException e ) {
          System.err.println("Invalid hostname");
          System.exit(1);     
      } catch ( Exception e ) {
          System.err.println("Invalid port number");
          System.exit(1);
      }
      System.out.println("Connecting to "+host +" at port "+port);
      f = new Frame("Client Window");
      GUI_test session = new GUI_test(host, port);
      f.addWindowListener(
         new WindowAdapter( ) {
            public void windowClosing( WindowEvent e )
            {
               System.exit( 0 );
            }
         } // new WindowAdapter()
      );
      f.setSize( 780, 580 );
      f.setVisible(true);
      session.init( );
      session.start( );
      while(true) {   // give chance to re-connect to the server
          /// sleep a while so that the "/connect" command be checked
         try{ Thread.sleep(568); }catch(InterruptedException e){;}
         if( session.readyToReConnect == false) continue;  // not ready
        ///get hostname and port, try to connect to it
         session.init( );
         if(session.sockFail) {
            session.warnUser( );
            session.readyToReConnect = false;
            continue;
         }
         session.removeHelp( );
         session.start( );
      } // while(true)
   } // main
}
