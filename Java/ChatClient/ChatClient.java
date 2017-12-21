/* Go group
 * CS 408 - Team Project
 *
 * ChatClient.java
 * Last Edited: March 5, 2017
 * A client implementation for our chat application. Provides a graphical
 * user interface to allow the user to communicate with the server more
 * clearly. Constants are currently set to connect to a server running
 * on localhost for testing and demonstration purposes.
 */

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import javax.swing.*;

public final class ChatClient extends JFrame {
    
    public static final String HOST_NAME = "localhost";
    public static final int PORT_NO = 40800;
    
    private Socket socket;
    private PrintStream ps;
    
    private JPanel panel;
    private JTextField userName;
    private JButton conButton;
    private JTextArea serverOut;
    private String output;
    private JTextField userIn;
    private JButton sendButton;
    
    public static void main(String[] args) {
        ChatClient frame = new ChatClient();
        frame.initComponents();
        frame.setVisible(true);
    }
    
    public ChatClient() {
        setTitle("CS380-P1 Chat Client");
        //this.setPreferredSize(new Dimension(500, 650));
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    private void initComponents() {
        panel = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        Font font = new Font("Arial", Font.BOLD, 20);
        
        JLabel conInfo = new JLabel("Connect to " + HOST_NAME + ":" + PORT_NO);
        conInfo.setFont(font);
        conInfo.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 50));
        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridheight = 1;
        gc.gridwidth = 2;
        gc.anchor = GridBagConstraints.WEST;
        panel.add(conInfo, gc);
        
        JLabel userPrompt = new JLabel("User Name: ");
        userPrompt.setFont(font);
        gc.gridx = 0;
        gc.gridy = 1;
        gc.gridheight = 1;
        gc.gridwidth = 1;
        gc.anchor = GridBagConstraints.WEST;
        panel.add(userPrompt, gc);
        
        this.userName = new JTextField("", 9);
        this.userName.setFont(font);
        gc.gridx = 1;
        gc.gridy = 1;
        gc.gridheight = 1;
        gc.gridwidth = 1;
        gc.anchor = GridBagConstraints.WEST;
        panel.add(userName, gc);
        
        this.conButton = new JButton("CONNECT");
        this.conButton.setFont(font);
        gc.gridx = 2;
        gc.gridy = 0;
        gc.gridheight = 2;
        gc.gridwidth = 1;
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.VERTICAL;
        panel.add(conButton, gc);
               
        JLabel server = new JLabel("Server Output:");
        server.setFont(font);
        server.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        gc.gridx = 0;
        gc.gridy = 2;
        gc.gridheight = 1;
        gc.gridwidth = 1;
        gc.anchor = GridBagConstraints.WEST;
        panel.add(server, gc);
        
        this.output = "";
        this.serverOut = new JTextArea(this.output, 20, 50);
        this.serverOut.setFont(font);
        this.serverOut.setLineWrap(true);
        this.serverOut.setEditable(false);
        JScrollPane scroll = new JScrollPane (serverOut);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        gc.gridx = 0;
        gc.gridy = 3;
        gc.gridheight = 1;
        gc.gridwidth = 4;
        gc.anchor = GridBagConstraints.CENTER;
        panel.add(scroll, gc);
        
        this.userIn = new JTextField("", 40);
        this.userIn.setFont(font);
        this.userIn.setEditable(false);
        gc.gridx = 0;
        gc.gridy = 4;
        gc.gridheight = 1;
        gc.gridwidth = 3;
        gc.anchor = GridBagConstraints.WEST;
        panel.add(userIn, gc);
        
        this.sendButton = new JButton("SEND");
        this.sendButton.setFont(font);
        this.sendButton.setEnabled(false);
        gc.gridx = 3;
        gc.gridy = 4;
        gc.gridheight = 1;
        gc.gridwidth = 1;
        gc.anchor = GridBagConstraints.EAST;
        panel.add(sendButton, gc);
        
        this.add(panel);
        this.applyListeners();
        this.pack();
    }
    
    private void applyListeners() {
        
        userName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getExtendedKeyCode() == KeyEvent.VK_ENTER) {
                    conButton.doClick();
                }
            }
        });
        
        userIn.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getExtendedKeyCode() == KeyEvent.VK_ENTER) {
                    sendButton.doClick();
                }
            }
        });
        
        conButton.addActionListener((ActionEvent ae) -> {
            try {
                if (!userName.getText().equals("")) {
                    socket = new Socket(HOST_NAME, PORT_NO);
                    OutputStream os = socket.getOutputStream();
                    ps = new PrintStream(os, true, "UTF-8");
                    ps.print(userName.getText());
                    userName.setEditable(false);
                    conButton.setEnabled(false);
                    userIn.setEditable(true);
                    sendButton.setEnabled(true);

                    (new Thread(new OutputThread())).start();
                    //output += "Connected.\n";
                    //serverOut.setText(output);
                }
            } catch (Exception ex) {
                output += "Unable to connect.\n";
                serverOut.setText(output);
            }
        });
        
        sendButton.addActionListener((ActionEvent ae) -> {
            if (!userIn.getText().equals("")) {
                ps.print(userIn.getText());
                userIn.setText("");
            }
        });
        
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e)
            {
                try {
                    socket.close();
                } catch (Exception ex) {}
                super.windowClosing(e);
            }
        });
    }
    
    private final class OutputThread implements Runnable {

        @Override
        public void run() {
            try {
                InputStream is = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                BufferedReader br = new BufferedReader(isr);
                String buffer;
                while(true) {
                    buffer = br.readLine();
                    if (buffer == null) {
                        break;
                    }
                    output += buffer + "\n";
                    serverOut.setText(output);
                }
            } catch (Exception ex) {}
        }
    }
}