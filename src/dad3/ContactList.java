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
    Socket socket;
    private DefaultListModel listModel;
    
    BufferedReader in;
    PrintWriter out;
 
    private static final String addString = "Add";
 
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
        AddListener addListener = new AddListener(addButton);
        addButton.setActionCommand(addString);
        addButton.addActionListener(addListener);
        addButton.setEnabled(true);
  
        //Create a panel that uses BoxLayout.
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane,BoxLayout.LINE_AXIS));
        buttonPane.add(addButton);
        buttonPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        add(listScrollPane, BorderLayout.CENTER);
        add(buttonPane, BorderLayout.PAGE_END);
    }
 
    private String askName()
        {
            return JOptionPane.showInputDialog(null, "Choose a screen name:", "Screen name selection", JOptionPane.PLAIN_MESSAGE);
        }
    
    private String askServerIPAddress()
        {
            return JOptionPane.showInputDialog(null, "Enter IP Address of the Server:", "Welcome to the Chat Messenger", JOptionPane.QUESTION_MESSAGE);
        }
     
    //This listener is shared by the text field and the hire button.
    class AddListener implements ActionListener{
        private boolean alreadyEnabled = false;
        private JButton button;
 
        public AddListener(JButton button) {
            this.button = button;
        }
 
        
        //Required by ActionListener.
        public void actionPerformed(ActionEvent e) 
        {
         Chat_handler chat_handler = new Chat_handler(socket);

                try {
                    chat_handler.run();
                    }
                    catch(Exception s)
                    {
                        s.printStackTrace();
                    }
            
        
        }
    }


    void createAndShowGUI() {
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
        createAndShowGUI();
        
        while(true)
        {
            String MESSAGE = in.readLine();
            if (MESSAGE.startsWith("SUBMITNAME"))
            {
                out.println(askName()); //send username to server 
            }
            
            else if(MESSAGE.startsWith("###"))
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
        try{
        contactlist.run();
        }
        catch (Exception e)
        {
        e.printStackTrace();
        }
    }
}

