/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dad3;
import java.net.*;
import java.util.HashSet;
import java.io.*;
import java.util.*;
public class ServerThread extends Thread
{
    private String username;
//    private String ipAddress;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private static ArrayList<String> usernames = new ArrayList<String>();
    //private static ArrayList<Socket> currentUsers = new ArrayList<Socket>();
    private static ArrayList<PrintWriter> writers = new ArrayList<PrintWriter>();
    int groupNumber = 1;
    
    public ServerThread(Socket socket)
    {
        this.socket = socket;
    }

    public void run()
    {
        try
        {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            
            while (true)
            {
            	Server.status.append("Incoming connection.\n");
                out.println("SUBMITNAME"); //ask user to enter username 
                username = in.readLine();
                if (username == null)
                {
                    return;
                }
                synchronized (usernames) //make sure username is not repeated 
                {
                    if (!usernames.contains(username))
                    {
                        usernames.add(username); // if not repeated, add in username 
                        break;
                    }
                }
            }
            //out.println("NAMEACCEPTED");
            //currentUsers.add(socket);
            writers.add(out);
            Server.status.append(username + " has connected to the chat via IP address of " + socket.getRemoteSocketAddress() + "\n");
            
            for (PrintWriter writer : writers) //send usernames to clients 
            {
                System.out.println(usernames); //send whole arraylist of username to clients 
                writer.println("###" + usernames); //send ### as identifier to process usernames 
                writer.flush();
            }
            
            while (true) //keep looping to check for inputs 
            {
                String input = in.readLine(); //read content that is send in from clients 
                if (input == null)
                {
                    return;
                }            
                
                else if (input.startsWith("NEWCHAT")) //if start a chat 
                {
                    ArrayList<String> chatUsers = new ArrayList<String>(); //array list to store usernames 
                    ArrayList<PrintWriter> chatUsersWriters = new ArrayList<PrintWriter>(); //arraylist to store print writers for users 
                    while (true)
                    {
                        input = in.readLine();
                        if (input.startsWith("END"))
                        {
                            break;
                        }

                        for(int i =0; i<usernames.size();i++) //loop all usersnames in server 
                        {
                            if(input.equals(usernames.get(i))) //if matching username is found 
                            {
                                chatUsers.add(usernames.get(i)); //add usernames to another arraylist
                                chatUsersWriters.add(writers.get(i)); //get the print writer for the username also 
                            }
                        }
                    }

                    for(int i=0;i <chatUsersWriters.size();i++) //loop through the selected printwriters of people in the chat 
                    {
                        PrintWriter tempWriter = chatUsersWriters.get(i); //get printwriter of a user
                        tempWriter.println("GETGROUPNUMBER"); //send GETGROUPNUMBER identifier to all clients 
                        tempWriter.println(groupNumber); //send group number 
                    }
                    groupNumber++;
                }
                
                else if (input.startsWith("MESSAGE"))
                {
                    input = in.readLine(); //receive group number 
                    String chatMessage = in.readLine(); //receive message from client
                    
                    for (PrintWriter writer : writers) //loop through all print writers 
                    {
                        writer.println("MESSAGE"); //send MESSAGE identifier 
                        writer.println(input);  //print group number 
                        writer.println(chatMessage); //send actual chat message 
                    }
                }
                
            }
        } 
        catch (IOException e)
        {
            System.out.println(e);
        }
        finally 
        {                
            if (username != null)
            {
                usernames.remove(username);
            }
            if (out != null)
            {
                writers.remove(out);
            }
            try
            {
                socket.close();
            }
            catch (IOException e)
            {
            	e.printStackTrace();
            }
        }
    }
}