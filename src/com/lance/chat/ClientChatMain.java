package com.lance.chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.Socket;
import java.util.Properties;

public class ClientChatMain extends JFrame implements ActionListener, KeyListener {
    static private int serverPort;
    static private String serverIp;

    static {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader("./src/com/lance/chat/chat.properties"));
            serverPort = Integer.parseInt(properties.getProperty("serverPort"));
            serverIp = properties.getProperty("serverIp");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final JTextArea jta;
    private final JScrollPane jsp;
    private final JPanel jp;
    private final JTextField jtf;
    private final JButton jb;
    private BufferedWriter bufferedWriter;

    public ClientChatMain() {
        jta = new JTextArea();
        jsp = new JScrollPane(jta);
        jp = new JPanel();
        jtf = new JTextField(10);
        jb = new JButton("Send");

        jp.add(jtf);
        jp.add(jb);

        this.add(jsp, BorderLayout.CENTER);
        this.add(jp, BorderLayout.SOUTH);

        this.setTitle("Chat Room - Client");
        this.setSize(300, 300);
        this.setLocation(300, 300);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);

        this.jta.setEditable(false);

        jb.addActionListener(this);
        jtf.addKeyListener(this);

        /************** TCP - Client ***************/
        try {
//            Socket socket = new Socket(serverIp, clientPort);
            Socket socket = new Socket(serverIp, serverPort);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            String currLine = null;
            while ((currLine = bufferedReader.readLine()) != null) jta.append(currLine + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*************** TCP - Client ***************/
    }

    public static void main(String[] args) {
        new ClientChatMain();
    }

    private boolean sendText() {
        String text = jtf.getText();
        text = "Client tells server: " + text;
        // Update self
        jta.append(text + System.lineSeparator());
        // Update other
        try {
            bufferedWriter.write(text);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            jtf.setText("");
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.sendText();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER)
            this.sendText();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
