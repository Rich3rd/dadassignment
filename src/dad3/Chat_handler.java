/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dad3;
import javax.swing.*;
import java.net.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class Chat_handler implements Runnable{
    
    public static boolean RIGHT_TO_LEFT = false;
    private Socket socket;
    BufferedReader in;
    PrintWriter out;
    
    static JTextArea textArea = new JTextArea(25,50);
    static JTextField textField = new JTextField();
 
    public static void addComponentsToPane(Container pane)
    {
    	pane.add(new JScrollPane(textArea), BorderLayout.CENTER);
        textArea.setEditable(false);
   
        pane.add(textField, BorderLayout.PAGE_END);
        textField.setEditable(false);
    }
        
    public Chat_handler(Socket socket) 
    {
        this.socket = socket;
    }
    
    public Chat_handler()
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
    void createAndShowGUI() 
    {
        //Create and set up the window.
        JFrame frame = new JFrame("Chat Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Set up the content pane.
        addComponentsToPane(frame.getContentPane());
        frame.pack();
        frame.setVisible(true);
        textField.setEditable(true);
    }
    
    
    
    public void run()
    {
        try{
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        
        
        if(in.readLine() != null)
        {
            String MESSAGE = in.readLine();

            textArea.append(MESSAGE.substring(8) + "\n");
            textArea.setCaretPosition(textArea.getDocument().getLength());
        }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}

