/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dad3;
import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
 
public class Client {
    BufferedReader in;
    static PrintWriter out;
    public static boolean RIGHT_TO_LEFT = false;
    static JTextArea textArea = new JTextArea(25,50);
    static JTextField textField = new JTextField();
	private Socket socket;
    
    public Client()
    {
        textField.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                out.println(textField.getText());
                textField.setText("");
            }
        });
    }
    
    public static void addComponentsToPane(Container pane)
    {
    	pane.add(new JScrollPane(textArea), BorderLayout.CENTER);
        textArea.setEditable(false);
   
        pane.add(textField, BorderLayout.PAGE_END);
        textField.setEditable(false);
    }
    
    private String askServerIPAddress()
    {
        return JOptionPane.showInputDialog(null, "Enter IP Address of the Server:", "Welcome to the Chat Messenger", JOptionPane.QUESTION_MESSAGE);
    }

    private String askName()
    {
        return JOptionPane.showInputDialog(null, "Choose a screen name:", "Screen name selection", JOptionPane.PLAIN_MESSAGE);
    }
    
    private String getIP()
    {
        return socket.getRemoteSocketAddress().toString();
    }
    
 
    private static void createAndShowGUI() 
    {
        //Create and set up the window.
        JFrame frame = new JFrame("Chat Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Set up the content pane.
        addComponentsToPane(frame.getContentPane());
        frame.pack();
        frame.setVisible(true);
    }
    
    private void run() throws Exception
    {
        String IPAddress = askServerIPAddress();
        socket = new Socket(IPAddress, 1234);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        while (true)
        {
            String input = in.readLine();
            if (input.startsWith("SUBMITNAME"))
            {
            	out.println(askName());
            }
            else if (input.startsWith("SELECTCONTACT"))
            {
//                out.println(procesContact());
            }
            else if (input.startsWith("RETURNIP"))
            {
                out.println(getIP());
            }
            else if (input.startsWith("NAMEACCEPTED"))
            {
                textField.setEditable(true);
            }
            else if (input.startsWith("MESSAGE"))
            {
            	textArea.append(input.substring(8) + "\n");
            	textArea.setCaretPosition(textArea.getDocument().getLength());
            }
        }
    }
    
    public static void main(String[] args)
    {
        Client client = new Client();
        Client.createAndShowGUI();
        try
        {
			client.run();
		} 
        catch (Exception e)
        {
			e.printStackTrace();
		}
    }
}
