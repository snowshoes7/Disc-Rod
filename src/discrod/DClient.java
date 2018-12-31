package discrod;

import processing.core.PApplet;

import processing.net.*;

public class DClient extends PApplet {

	public String myText = ""; //Text input by the user.

	  public Client client; //The Processing Clientside Object.

	  public String receivedText; //Text received from the server.
	  public boolean canSend = true; //Whether or not the user can send a message.
	  public boolean ipEstablished = false; //Whether or not the user is connected to a server.
	  public boolean nameEstablished = false; //Whether or not the user has specified a screen name.
	  public String name;
	  public int sendCount = 0;
	  public int blinkVar = 0;
	  //public int mtcount = 0; Explained below.

	  public String host; //The (local) host IP the user will connect to. Currently null, as the user must specify an IP to connect to at first.

	  public void setup() { //Run on the program's start.
	    ipEstablished = false; //Re-establish variables, as a failsafe.
	    nameEstablished = false; //"
	    sendCount = 0; //"
	    name = "";
	    //mtcount = 0; I meant for this to do something, but I'm not entirely sure what.
	  }

	  public void draw() { //Run every frame.
	    background(0);

	    fill(255);

	    if (sendCount >= 10) {
	      canSend = true;
	      sendCount = 0;
	    }
	    else {
	      sendCount += 1;
	    }

	    if (ipEstablished && nameEstablished) { //If connected to a server,
	      if (client.available() > 0) { //If new data has been received from the server,
	        try {
	          receivedText = new String(client.readBytes(), "UTF-8"); //Set receivedText to received data (equal to received bytes encoded in UTF-8).
	          //canSend = true; //Enable message sending, if it is disabled.
	        } catch (Exception exception) {
	          exception.printStackTrace();
	        }
	      }
	    }
	    if (blinkVar >= 10 && blinkVar < 20) {
	      text(myText + "|", 0, height - 15); //Render user-input text at 0, window height - 15.
	      blinkVar++;
	    } else if (blinkVar < 10) {
	      text(myText, 0, height - 15); //Render user-input text at 0, window height - 15.
	      blinkVar++;
	      //System.out.println(blinkVar);
	    } else if (blinkVar >= 20) {
	      text(myText, 0, height - 15); //Render user-input text at 0, window height - 15.
	      blinkVar = 0;
	    }

	    if (!ipEstablished && !nameEstablished) { //Display a message if not connected to a server.
	      text("Enter the IP you wish to connect to:", 0, height - 30);
	    }
	    if (ipEstablished && !nameEstablished) { //Display a message if not connected to a server.
	      text("What is your name?", 0, height - 30);
	    }
	    if (receivedText != null && ipEstablished && nameEstablished) { //If text has been received and this client is connected to a server,
	      //receivedText = "";
	      text(receivedText, 0, 15); //Render all received text at 0, 15.
	      String[] lines = receivedText.split("\r\n|\r|\n");
	      if (lines.length >= 25) {
	          receivedText = "";
	      }
	    }
	  }

	  public void keyPressed() { //Handles all key presses.
	      if (ipEstablished && nameEstablished) { //If connected,
	        if (keyCode == BACKSPACE) { //Backspace functionality.
	            if (myText.length() > 0) { //If any text exists,
	              myText = myText.substring(0, myText.length()-1); //Changes string to a substring of itself that excludes the last character.
	            }
	          } else if (keyCode == DELETE) { //Delete functionality.
	            myText = ""; //Nullify string.
	          } else if (keyCode != SHIFT && keyCode != CONTROL && keyCode != ALT && keyCode != 20 && keyCode != ENTER && keyCode != WINDOWS) { //Text input functionality, does not input Shift, Ctrl, Alt, Caps Lock (keyCode 20), Enter, or the Windows key.
	            myText = myText + key; //Add key to string.
	          }
	          if (keyCode == ENTER && canSend) { //If the user sends a message, and they can send it,
	            if (myText.equals("/exit") || myText.equals("/e")) {
	              try {
	                client.write(new String(name + ";" + "[LEAVE_REQUEST/492/USER-INITIATED]").getBytes("UTF-8"));
	              } catch (Exception e) {
	                e.printStackTrace();
	              }
	              System.exit(0);
	            }
	            else if (myText.startsWith("/msg ")) {
	              try {
	                //System.out.println(new String("[BROADCAST] " + myText.substring(5, myText.length())).getBytes("UTF-8"));
	                client.write(new String("[BROADCAST];" + myText.substring(5, myText.length())).getBytes("UTF-8"));
	                myText = ""; //Nullify string myText.
	              } catch (Exception e) {
	                e.printStackTrace();
	              }
	            }
	            else {
	              try {
	                //System.out.println(new String(name + ";" + myText).getBytes("UTF-8"));
	                client.write(new String(name + ";" + myText).getBytes("UTF-8"));
	                myText = ""; //Nullify string myText.
	              } catch (Exception e) {
	                e.printStackTrace();
	              } //Write name and myText to the connected server through the client object.
	            }
	            canSend = false; //Prevent spam by disallowing sending.
	            sendCount = 0;
	          }
	      } else if (!ipEstablished && !nameEstablished) { //If not connected (i.e. the user is clarifying the IP they want to connect to),
	        //The following are explained above.
	        if (keyCode == BACKSPACE) {
	            if (myText.length() > 0) {
	              myText = myText.substring(0, myText.length()-1);
	            }
	          } else if (keyCode == DELETE) {
	            myText = "";
	          } else if (keyCode != SHIFT && keyCode != CONTROL && keyCode != ALT && keyCode != 20 && keyCode != ENTER) {
	            myText = myText + key;
	          }
	          if (keyCode == ENTER) { //If the user connects,
	            //TODO: Check if IP is valid.
	            host = myText; //Set host string to myText.
	            myText = ""; //Nullify myText.
	            ipEstablished = true;
	            nameEstablished = false; //Just re-establishing this, for my own sanity.
	          }
	      } else if (ipEstablished && !nameEstablished) {
	        if (keyCode == BACKSPACE) {
	            if (myText.length() > 0) {
	              myText = myText.substring(0, myText.length()-1);
	            }
	          } else if (keyCode == DELETE) {
	            myText = "";
	          } else if (keyCode != SHIFT && keyCode != CONTROL && keyCode != ALT && keyCode != 20 && keyCode != ENTER) {
	            myText = myText + key;
	          }
	          if (keyCode == ENTER) { //If the user specifies a name,
	            //TODO: Check if name is valid. (?)
	            name = myText; //Set name string to myText.
	            myText = ""; //Nullify myText.
	            ipEstablished = true; //Setting this variable once more.
	            nameEstablished = true; //A name has now been established.
	            try {
	              client = new Client(this, host, 10002); //Establish the clientside through Processing.
	            } catch (Exception exceptionn) {
	              exceptionn.printStackTrace();
	              System.out.println("Error. You may have used an invalid address.");
	              //System.exit(0);
	            }
	            try {
	              client.write(new String(name + ";" + "[JOIN_REQUEST/491/USER-INITIATED]").getBytes("UTF-8"));
	            } catch (Exception en) {
	              en.printStackTrace();
	              System.out.println("Joining the server has failed.");
	              //System.exit(0);
	            }
	          }
	      }
	  }

	  public void settings() {
	    size(700, 500); //Establish window size as 700x500.
	  }
	
	public static void main(String _args[]) {
		PApplet.main(new String[] { discrod.DClient.class.getName() });
	}
}
