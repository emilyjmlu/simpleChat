// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  ServerConsole serverUI; // for server user
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port) 
  {
    super(port);
    serverUI = new ServerConsole(port); // new server
    serverUI.accept();
  }
  
  public EchoServer(int port, ServerConsole serverUI) throws IOException {
	  super(port);
	  this.serverUI = serverUI; 
	  listen();
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
	  String message = msg.toString();
	  serverUI.display("Message received: " + msg  + " from " + client.getInfo("loginID")); // msg received including logged on
	  
	  if (message.startsWith("#login") && (client.getInfo("loginID") == null)) { // if message starts with login and loginID is null (meaning new client)
		  client.setInfo("loginID", message.split(" ")[1]);
		  System.out.println(client.getInfo("loginID") + " logged on.");
		  this.sendToAllClients(client.getInfo("loginID") + " has logged on.");
	  }
	  else if (message.startsWith("#login")) { // if client disconnected
		  try {
			  client.sendToClient("ERROR - already connected.");
			  close();
		  }
		  catch (Exception e) {System.out.println("thrown");}
		  
	  }
	  else { // doesn't start with login
		  	
		    this.sendToAllClients(client.getInfo("loginID") + ": " + msg);
	  }
	  
    
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println("Server has stopped listening for connections.");
  }
  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  public static void main(String[] args) 
  {
    int port = 0; //Port to listen on

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }
	
    EchoServer sv = new EchoServer(port);
    
    try 
    {
      sv.listen(); //Start listening for connections
    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
  }
  
  /**
   * Hook method called each time a new client connection is
   * accepted. The default implementation does nothing.
   * @param client the connection connected to the client.
   */
  protected void clientConnected(ConnectionToClient client) {
	  System.out.println("A client is connecting.");
  }

  /**
   * Hook method called each time a client disconnects.
   * The default implementation does nothing. The method
   * may be overridden by subclasses but should remains synchronized.
   *
   * @param client the connection with the client.
   */
  synchronized protected void clientDisconnected(ConnectionToClient client) {
	  System.out.println(client.getInfo("loginID") + " has disconnected.");
	  this.sendToAllClients(client.getInfo("loginID") + " has disconnected.");
  }
  
  /**
   * Hook method called each time an exception is thrown in a
   * ConnectionToClient thread.
   * The method may be overridden by subclasses but should remains
   * synchronized.
   *
   * @param client the client that raised the exception.
   * @param Throwable the exception thrown.
   */
  synchronized protected void clientException(ConnectionToClient client, Throwable exception) {
	  System.out.println(client.getInfo("loginID") + " has disconnected.");
	  this.sendToAllClients(client.getInfo("loginID") + " has disconnected.");
  }
  

  /**
   * Hook method called when the server is closed.
   * The default implementation does nothing. This method may be
   * overriden by subclasses. When the server is closed while still
   * listening, serverStopped() will also be called.
   */
  protected void serverClosed() {
	  System.out.println("Server closed.");
  }
}
//End of EchoServer class
