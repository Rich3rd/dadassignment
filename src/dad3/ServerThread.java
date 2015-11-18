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
    private static Hashtable<String,String> usernames = new Hashtable<String,String>();
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
                //out.println("RETURNIP");
//                ipAddress = in.readLine();
                if (username == null)
                {
                    return;
                }
                synchronized (usernames)
                {
                    if (!usernames.contains(username))
                    {
                        usernames.put(username,socket.getRemoteSocketAddress().toString());
                        break;
                    }
                }
            }
            out.println("NAMEACCEPTED");
            writers.add(out);
            Server.status.append(username + " has connected to the chat via IP address of " + socket.getRemoteSocketAddress() + "\n");
            
            while (true)
            {
                String input = in.readLine();
                if (input == null)
                {
                    return;
                }
                for (PrintWriter writer : writers)
                {
                    writer.println("MESSAGE " + username + ": " + input);
                    System.out.println(input);
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