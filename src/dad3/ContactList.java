/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dad3;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.event.*;
 
/* ListDemo.java requires no other files. */
public class ContactList extends JPanel
{
    static JList list;
    static Socket socket;
    private DefaultListModel listModel;
    
    public BufferedReader in;
    public static PrintWriter out;
 
    private static final String addString = "Chat";
    static String username;
    static int groupNumber = 1;
 
    public ContactList() {
        super(new BorderLayout());
 
        //
        listModel = new DefaultListModel();      
 
        //Create the list and put it in a scroll pane.
        list = new JList(listModel);
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.setSelectedIndex(0);
        list.setVisibleRowCount(5);
        JScrollPane listScrollPane = new JScrollPane(list);
 
        //create buttons 
        JButton addButton = new JButton(addString);
        addButton.setEnabled(true);
  
        //Create a panel that uses BoxLayout.
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane,BoxLayout.LINE_AXIS));
        buttonPane.add(addButton);
        buttonPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        add(listScrollPane, BorderLayout.CENTER);
        add(buttonPane, BorderLayout.PAGE_END);
        
        addButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (list.isSelectionEmpty() == true)
                {
                    return;
                }

                out.println("NEWCHAT");
                for (int i=1; i<list.getSelectedIndices().length; i++)
                {
                    out.println(list.getSelectedValue());
                }
                out.println("ENDSEND");

                out.println()
                Chat_handler chat_handler = new Chat_handler(out, username, groupNumber);
                chat_handler.start();
            }
        });
    }
 
    private String askName()
        {
            return JOptionPane.showInputDialog(null, "Choose a screen name:", "Screen name selection", JOptionPane.PLAIN_MESSAGE);
        }
    
    private String askServerIPAddress()
        {
            return JOptionPane.showInputDialog(null, "Enter IP Address of the Server:", "Welcome to the Chat Messenger", JOptionPane.QUESTION_MESSAGE);
        }


    static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Contact list");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        //Create and set up the content pane.
        JComponent newContentPane = new ContactList();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
    
    void run() throws Exception
    {

        String IPAddress = askServerIPAddress();
        socket = new Socket(IPAddress, 1234);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        while(true)
        {
            String MESSAGE = in.readLine();
            if (MESSAGE.startsWith("SUBMITNAME"))
            {
                username = askName(); //send username to server
                out.println(username);
            }
            
            else if(MESSAGE.startsWith("###"))      //set usernames to display on JList
            {
                String TEMP1 = MESSAGE.substring(3);
                TEMP1 = TEMP1.replace("[", "");
                TEMP1 = TEMP1.replace("]", "");
                
                String[] CurrentUsers = TEMP1.split(", ");
                list.setListData(CurrentUsers);
            }
        }         
    }

    
    
    public static void main (String[] args)
    {
        ContactList contactlist = new ContactList();
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
        
        try{
        contactlist.run();
        }
        catch (Exception e)
        {
        e.printStackTrace();
        }
    }
}

