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
                out.println("SUBMITNAME");
                username = in.readLine();
                if (username == null)
                {
                    return;
                }
                synchronized (usernames)
                {
                    if (!usernames.contains(username))
                    {
                        usernames.add(username);
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
                System.out.println(usernames);
                writer.println("###" + usernames);
                writer.flush();
            }
            
            while (true)
            {
                String input = in.readLine();
                if (input == null)
                {
                    return;
                }            
                
                else if (input.startsWith("NEWCHAT"))
                {
                    ArrayList<String> chatUsers = new ArrayList<String>();
                    ArrayList<PrintWriter> chatUsersWriters = new ArrayList<PrintWriter>();
                    while (true)
                    {
                        input = in.readLine();
                        if (input.startsWith("ENDSEND"))
                        {
                            break;
                        }

                        for(int i =0; i<usernames.size();i++)
                        {
                            if(input.equals(usernames.get(i)))
                            {
                                chatUsers.add(usernames.get(i)); //add usernames to another arraylist
                                chatUsersWriters.add(writers.get(i));
                            }
                        }
                    }

                    for(int i=0;i <chatUsersWriters.size();i++)
                    {
                        PrintWriter tempWriter = chatUsersWriters.get(i);
                        tempWriter.println("GETGROUPNUMBER");
                        tempWriter.println(groupNumber);
                    }
                    groupNumber++;
                }
                
                else if (input.startsWith("MESSAGE"))
                {
                    input = in.readLine(); //receive group number 
                    String chatMessage = in.readLine(); //receive message from client
                    
                    for (PrintWriter writer : writers)
                    {
                        writer.println("MESSAGE");
                        writer.println(input);  //print group number 
                        writer.println(chatMessage);
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