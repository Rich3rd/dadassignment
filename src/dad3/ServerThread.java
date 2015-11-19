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
    private static ArrayList<Socket> currentUsers = new ArrayList<Socket>();
    private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();
    
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
            currentUsers.add(socket);
            writers.add(out);
            Server.status.append(username + " has connected to the chat via IP address of " + socket.getRemoteSocketAddress() + "\n");
            
            for (PrintWriter writer : writers)
                {
//                    out.println("MESSAGE " + username + ": " + input);

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
            if (socket != null)
            {
                currentUsers.remove(socket);
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