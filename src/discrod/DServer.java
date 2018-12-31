package discrod;

import processing.core.PApplet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import processing.net.Client;
import processing.net.Server;

public class DServer extends PApplet {

	public int port = 10002;
	  public boolean myServerRunning = true;
	  public int bgColor = 0;
	  public int direction = 1;
	  public int textLine = 95;
	  public int refreshCount = 0;

	  public Server myServer;

	  public String allData = "[DATA START]";

	  public String permanentMsg;

	  public String ipf;

	  public void setup()
	  {
	    myServer = new Server(this, port); // Starts a myServer on port 10002
	    background(0);

	    try {
	    URL whatismyip = new URL("http://checkip.amazonaws.com");
	      BufferedReader in = new BufferedReader(new InputStreamReader(
	                      whatismyip.openStream()));
	      ipf = in.readLine(); //you get the IP as a String
	    } catch (Exception exception) {
	      System.out.println(exception.getMessage());
	    }
	  }

	  public void settings() {
	    size(500, 500);
	  }

	  public void draw()
	  {
	    if (myServerRunning == true)
	    {
	      background(0);
	      text("[SERVER RUNNING]", 15, 45);
	      text("Connect to " + Server.ip() + " if you want to use LAN.", 15, 65);
	      text("Otherwise, connect to " + ipf + " for a public internet connection.", 15, 85);
	      text("Forward port 10002 for this to function.", 15, 105);
	      int tCl = myServer.clientCount;
	      Client thisClient = myServer.available();
	      if (tCl > 0) {
	        if (thisClient != null) {
	            if (thisClient.available() > 0) {
	            try {
	              String Amsg = null;
	              String name = null;
	              String Bmsg = null;
	              String msg = null;

	              Amsg = new String(thisClient.readBytes(), "UTF-8");
	              name = Amsg.split(";")[0].toString();
	              

	              if (Amsg.split(";")[1].equals("[LEAVE_REQUEST/492/USER-INITIATED]")) {
	                allData = allData + "\n" + name + " has left the server.";
	              } else if (Amsg.split(";")[1].equals("[JOIN_REQUEST/491/USER-INITIATED]")) {
	                  allData = allData + "\n" + name + " has joined the server.";
	              } else if (Amsg.split(";")[0].toString().equals("[BROADCAST]")) {
	                  System.out.println(Amsg.split(";")[0].toString());
	                  System.out.println(Amsg.split(";")[1].toString());
	                  allData = allData + "\n" + "[BROADCAST] " + Amsg.split(";")[1].toString();
	              } else {
	                Bmsg = name + " says: " + Amsg.substring(name.length() + 1);
	                msg = Bmsg;
	                allData = allData + "\n" + msg;
	              }

	              try {
	                String[] lines = allData.split("\r\n|\r|\n");
	                    if (lines.length >= 25) {
	                        allData = "";
	                    }
	              myServer.write(allData.getBytes("UTF-8"));
	            } catch (UnsupportedEncodingException e) {
	              e.printStackTrace();
	            }
	            } catch (Exception exception) {
	              exception.printStackTrace();
	            }
	            }
	          }
	      }
	      if (refreshCount >= 30) {
	        try {
	      myServer.write(allData.getBytes("UTF-8"));
	    } catch (UnsupportedEncodingException e) {
	      e.printStackTrace();
	    }
	    refreshCount = 0;
	      } else if (refreshCount < 30) {
	        refreshCount += 1;
	      }
	      text(allData, 15, 135);
	    }
	    else
	    {
	    background(0);
	      text("[SERVER STOPPED]", 15, 45);
	    }
	  }

	  public void keyPressed() {
	    if (key == 's') {
	      try {
	        myServer.write("[SERVER] YOU HAVE BEEN DISCONNECTED. Please exit the application.".getBytes("UTF-8"));
	      } catch (Exception e) {
	        e.printStackTrace();
	      }
	      myServer.stop();
	      myServerRunning = false;
	    }
	  }
	
	public static void main(String _args[]) {
		PApplet.main(new String[] { discrod.DServer.class.getName() });
	}
}
