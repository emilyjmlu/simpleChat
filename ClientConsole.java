// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;
import java.util.Scanner;

import client.*;
import common.*;

/**
 * This class constructs the UI for a chat client.  It implements the
 * chat interface in order to activate the display() method.
 * Warning: Some of the code here is cloned in ServerConsole 
 *
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Dr Timothy C. Lethbridge  
 * @author Dr Robert Lagani&egrave;re
 * @version September 2020
 */
public class ClientConsole implements ChatIF 
{
  //Class variables *************************************************
  
  /**
   * The default port to connect on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Instance variables **********************************************
  
  /**
   * The instance of the client that created this ConsoleChat.
   */
  ChatClient client;
  
  /**
   * Scanner to read from the console
   */
  Scanner fromConsole; 
  
  String loginID;

  
  //Constructors ****************************************************

  /**
   * Constructs an instance of the ClientConsole UI.
   *
   * @param host The host to connect to.
   * @param port The port to connect on.
   */
  public ClientConsole(String loginID, String host, int port) 
  {
    try 
    {
      client= new ChatClient(loginID, host, port, this);
      client.sendToServer("#loginID " + loginID);
      
      
    } 
    catch(IOException exception) 
    {
      System.out.println("Error: Can't setup connection!"
                + " Terminating client.");
      System.exit(1);
    }
    
    // Create scanner object to read from console
    fromConsole = new Scanner(System.in); 
  }

  
  //Instance methods ************************************************
  
  /**
   * This method waits for input from the console.  Once it is 
   * received, it sends it to the client's message handler.
   */
  public void accept() 
  {
    try
    {

      String message;

      while (true) 
      {
        message = fromConsole.nextLine();
     
        if (message.toString().charAt(0) == '#') { // if the first character of the input is a #
        	
        	if (!message.contains(" ")) { // only one word input
        		String command = message.substring(1); // command is the second part of the input (after #)
            	
            	switch(command) {
            	
    	        	case "quit":
    	        		display("Closing program.");
    					client.quit(); // close client
    					break;
    				
    	        	case "logoff":
    	        		client.closeConnection(); // closeConnection
    	        		break;
    	        		
    	        	case "gethost":
    	        		display(client.getHost());
    	        		break;
    	        		
    	        	case "getport":
    	        		display(Integer.toString(client.getPort()));
    	        		break;
    	        		
    	        	case "login":
    	        		display("Error: missing login ID.");
    					
    	        	default:
    	        		display("Not a valid command.");
    	        		break;
           
        	}
            
        	
        }
        else if (message.contains(" ")) { // two word input
        	String command = message.split(" ")[0];
        	String para = message.split(" ")[1];
        	
        	switch (command) {
        	
        		case "#sethost":
        			if (client.isConnected()) { // error message if client is already connected
        				display("Error: Already connected to server.");
        			} else {
        				client.setHost(para);
        			}
        			break;
        			
        		case "#setport":
        			if (client.isConnected()) { // error message if client is already connected
        				display("Error: Already connected to server.");
        			} else {
        				client.setPort(Integer.parseInt(para));
        			}
        			break;
        			
	        	case "#login":
	        		if (client.isConnected()) {
	        			display("Error: Already connected to server."); 
	        		} else {
	        			client.setLoginID(message.split(" ")[1]);
	        		}
	        		client.openConnection();
	        		client.sendToServer("#loginID " + client.getLoginID());
	        		client.setLoginID(message.split(" ")[1]);
	        		break;
	        		
        		default: 
        			display("Not a valid command."); // if used # but not a valid command
	        		break;
        			
        	}
        		
        }
        	
        }
        else { // if the input isn't a command
        	client.handleMessageFromClientUI(message);
        }
      }
    } 
    catch (Exception ex) 
    {
      System.out.println
        ("Unexpected error while reading from console!");
    }
  }

  /**
   * This method overrides the method in the ChatIF interface.  It
   * displays a message onto the screen.
   *
   * @param message The string to be displayed.
   */
  public void display(String message) 
  {
    System.out.println("> " + message);
  }

  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of the Client UI.
   *
   * @param args[0] The host to connect to.
   */
  public static void main(String[] args) 
  {
	  
    String host = "localhost"; // initialize host
    int port = DEFAULT_PORT; // initialize port number to default
    String loginID = null;

    try
    {
    	loginID = (String)args[0]; 
    }
    catch(Throwable t) {
    	System.out.println("Error: No login ID. Terminating client.");
	  	  System.exit(1);
    }
    
    try
    {
    	port = Integer.parseInt(args[1]); //get port from command line
    }
    catch(Throwable t)
    {
    	System.out.println("Wrong input. Using default port.");
    }
    
    ClientConsole chat= new ClientConsole(loginID, host, port);
    
    chat.accept();  //Wait for console data
  }

}
//End of ConsoleChat class
