/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dad3;

import java.io.*;
import java.net.*;

import javax.swing.*;

public class Server 
{
    JFrame frame = new JFrame("Chat Server");
    static JTextArea status = new JTextArea(10, 35);
    
    public Server()
    {
    	status.setEditable(false);
    	frame.getContentPane().add(new JScrollPane(status), "Center");
    	frame.pack();
    }
    
    private void runGUI() throws IOException
    {
    	status.append("Chat server is now running.\n");
    }
    
    public static void main(String[] args) throws Exception
    {
    	Server server = new Server();
    	server.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        server.frame.setVisible(true);
        server.runGUI();
        ServerSocket ssock = new ServerSocket(1234);
        status.append("IP address of the server is " + InetAddress.getLocalHost().getHostAddress() + "\n");
        try
        {
            while (true)
            {
                new ServerThread(ssock.accept()).start();
            }
        } finally
        {
            ssock.close();
        }
    }
}
