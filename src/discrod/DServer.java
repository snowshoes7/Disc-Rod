package discrod;

import processing.core.PApplet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
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
	  public String title = "main-channel";
	  public String allDataNoTitle;
	  
	  public boolean titleAdded = false;
	  
	  public boolean colorsAdded = false;

	  public String permanentMsg; //Currently not used
	  
	  public List<String> adminnames = new ArrayList<String>();
	  
	  public List<String> usernames = new ArrayList<String>();
	  
	  public List<String> channels = new ArrayList<String>();
	  
	  public List<User> users = new ArrayList<User>(); //TODO This is still not great
	  
	  public String toKick;
	  
	  public String ipToFind;

	  public String ipf;
	  
	  public int[] bgColors = new int[3];
	  
	  public class User {
		  public int[] nameColors = new int[3];
		  public String name;
		  public String ip;
		  
		  public User(String gname, String gip) {
			  this.name = gname;
			  this.ip = gip;
		  }
		  
		  public String toString() {
			  return this.name + " " + this.ip;
		  }
	  }

	  public void setup()
	  {
	    myServer = new Server(this, port); // Starts a myServer on port 10002
	    bgColors = new int[]{0, 0, 0};
	    background(bgColors[0], bgColors[1], bgColors[2]);

	    try {
	    URL whatismyip = new URL("http://checkip.amazonaws.com");
	      BufferedReader in = new BufferedReader(new InputStreamReader(
	                      whatismyip.openStream()));
	      ipf = in.readLine(); //you get the IP as a String
	    } catch (Exception exception) {
	      System.out.println(exception.getMessage());
	    }
	    
	    //TODO: TAKE THIS OUT AT SOME POINT AND REPLACE IT WITH A PROPER SYSTEM FOR ADDING ADMINS
	    //RIGHT NOW ALL IT DOES IS MAKE ANY USER WITH NAME "owen" AN ADMIN
	    adminnames.add("owen");
	    channels.add("main-channel");
	  }

	  public void settings() {
	    size(500, 500);
	  }

	  public void draw()
	  {
	    if (myServerRunning == true)
	    {
	      background(bgColors[0], bgColors[1], bgColors[2]);
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
	              
	              /*if (name.equals(ipToFind)) {
	            	  allData = allData + "\n" + "[SYSTEM] THE IP OF " + ipToFind + " IS " + thisClient.ip();
	            	  ipToFind = "";
	              }*/
	              
	              if (name.equals(toKick)) {
	            	  thisClient.write(new String("[SYS_KICK_501]").getBytes("UTF-8"));
	            	  myServer.disconnect(thisClient);
	            	  //TODO This needs to remove from users and usernames
	            	  toKick = ""; //Clear the toKick String so that users can join with the username of the person just kicked (a kick is temporary, bans will be a more permanent solution)
	              } else if (!(name.equals(toKick))) {
	            	  //Do the following if and only if the user has NOT been kicked, otherwise give the kicked user no privileges
		              if (Amsg.split(";")[1].equals("[LEAVE_REQUEST/492/USER-INITIATED]")) {
		                allData = allData + "\n" + name + " has left the server.";
		                usernames.remove(usernames.indexOf(name));
		                users.remove(users.indexOf(new User(name, thisClient.ip()))); //TODO Verify that this works
		              } else if (Amsg.split(";")[1].equals("[JOIN_REQUEST/491/USER-INITIATED]")) {
		            	  if (!(usernames.contains(name))) {
		            		  usernames.add(name);
		            		  users.add(new User(name, thisClient.ip()));
		            		  System.out.println(new User(name, thisClient.ip())); //TODO needs further testing
			                  allData = allData + "\n" + name + " has joined the server.";
		            	  } else {
		            		  allData = allData + "\n" + "[DSERVER]: Another user attempted to join with the username " + name + " and was kicked.";
		            		  thisClient.write("[JOIN_REJECT/491E/NAME-CONFLICT]".getBytes("UTF-8"));
		            		  myServer.disconnect(thisClient);
		            	  }
		              } else if (Amsg.split(";")[0].toString().equals("[BROADCAST]")) {
		                  allData = allData + "\n" + "[BROADCAST] " + Amsg.split(";")[1].toString();
		              } else if ((Amsg.split(";")[1].toString().equals("[KICK_REQUEST/501/USER-INITIATED]")) && adminnames.contains(name.toString())) {
		            	  toKick = Amsg.split(";")[2].toString();
		            	  allData = allData + "\n" + name + " HAS KICKED " + toKick;
		              } else if ((Amsg.split(";")[1].toString().equals("[KICK_REQUEST/501/USER-INITIATED]")) && !adminnames.contains(name.toString())) {
		            	  //Do nothing
		              } else if ((Amsg.split(";")[1].toString().equals("[COLOR_REQUEST/300/USER-INITIATED]"))) {
		            	  bgColors[0] = Integer.parseInt(Amsg.split(";")[2].split(" ")[0]);
		            	  bgColors[1] = Integer.parseInt(Amsg.split(";")[2].split(" ")[1]);
		            	  bgColors[2] = Integer.parseInt(Amsg.split(";")[2].split(" ")[2]);
		            	  background(bgColors[0], bgColors[1], bgColors[2]);
		            	  allData = allData + "\n" + name + " HAS CHANGED THE BACKGROUND COLORS TO " + bgColors[0] + " " + bgColors[1] + " " + bgColors[2] + " ";
		            	  colorsAdded = false;
		              } else if ((Amsg.split(";")[1].toString().equals("[IP_REQUEST/502/USER-INITIATED]"))) {
		            	  if (adminnames.contains(name.toString()) /*TODO This needs to be changed to user objects, not just names*/) {
		            		  ipToFind = Amsg.split(";")[2].toString();
		            		  String givenIP = "not available";
		            		  for (User x : users) {
		            			  if (x.name.equals(ipToFind)) {
		            				  givenIP = x.ip;
		            				  break;
		            			  }
		            		  }
			            	  allData = allData + "\n" + "[DSERVER to ADMINS]: THE IP OF " + ipToFind + " IS " + givenIP; //TODO This needs to write ONLY to admins, not to the entire server.
			            	  ipToFind = "";
		            	  }
		            	  else if (!(adminnames.contains(name.toString()))) {
		            		  //Do nothing
		            	  }
		              } else if ((Amsg.split(";")[1].toString().equals("[CHANNEL_CHANGE_REQUEST/401/USER-INITIATED]"))) {//TODO uh oh
		            	  
		              } else {
		                Bmsg = name + " says: " + Amsg.substring(name.length() + 1);
		                msg = Bmsg;
		                allData = allData + "\n" + msg;
		              }
	              }
	              
	              if (!titleAdded) {
	            	  allData = title + ";;;;" + allData;
	            	  titleAdded = true;
	              }
	              
	              if (!colorsAdded) {
	            	  allData = allData + "\n" + "[SYS_COLOR_300] " + bgColors[0] + " " + bgColors[1] + " " + bgColors[2] + " ";
	            	  colorsAdded = true;
	              }
	              
	              try {
	                String[] lines = allData.split("\r\n|\r|\n");
	                    if (lines.length >= 25) {
	                        allData = title + ";;;;";
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
	      String[] tempremovetitle = Arrays.copyOfRange(allData.split(";;;;"), 1, allData.split(";;;;").length);
	      allDataNoTitle = String.join("", tempremovetitle);
	      
	      String[] liness = allDataNoTitle.split("\r\n|\r|\n");
	      ArrayList<String> lines = new ArrayList<String>(Arrays.asList(liness));
	      
	      for (Iterator<String> it = lines.iterator(); it.hasNext();) {
	    	  String x = it.next();
	    	  if (x.startsWith("[SYS_COLOR_300]")) {
//	    		  bgColor[0] = Integer.parseInt(x.split(" ")[1]);
//	    		  bgColor[1] = Integer.parseInt(x.split(" ")[2]);
//	    		  bgColor[2] = Integer.parseInt(x.split(" ")[3]);
	    		  it.remove();
	    	  }
	      }
	      
	      StringBuilder sb = new StringBuilder();
	      for (String s : lines)
	      {
	          sb.append(s);
	          sb.append("\n");
	      }
	      
	      String adntnew = sb.toString();
	      
	      text(adntnew, 15, 135);
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
	    //The following is supposed to be a method to restart the server with a slight delay, but I have not gotten it to work in testing and so I am leaving it alone for now.
//	    if (key == 'r') {
//		      try {
//		        myServer.write("[SERVER] YOU HAVE BEEN TEMPORARILY DISCONNECTED. Please wait...".getBytes("UTF-8"));
//		      } catch (Exception e) {
//		        e.printStackTrace();
//		      }
//		      myServer.stop();
//		      myServerRunning = false;
//		      
//		      myServer = new Server(this, port);
//		      
//		      try {
//		  	    URL whatismyip = new URL("http://checkip.amazonaws.com");
//		  	      BufferedReader in = new BufferedReader(new InputStreamReader(
//		  	                      whatismyip.openStream()));
//		  	      ipf = in.readLine(); //you get the IP as a String
//		  	    } catch (Exception exception) {
//		  	      System.out.println(exception.getMessage());
//		  	    }
//		      
//		      myServerRunning = true;
//		    }
	  }
	
	public static void main(String _args[]) {
		PApplet.main(new String[] { discrod.DServer.class.getName() });
	}
}