package com.lance.chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

public class ServerChatMain extends JFrame implements ActionListener, KeyListener {
    private final JTextArea jta;
    private final JScrollPane jsp;
    private final JPanel jp;
    private final JTextField jtf;
    private final JButton jb;
    private BufferedWriter bufferedWriter;

    public ServerChatMain() {
        jta = new JTextArea();
        jsp = new JScrollPane(jta);
        jp = new JPanel();
        jtf = new JTextField(10);
        jb = new JButton("Send");

        jp.add(jtf);
        jp.add(jb);

        this.add(jsp, BorderLayout.CENTER);
        this.add(jp, BorderLayout.SOUTH);

        this.setTitle("Chat Room - Server");
        this.setSize(300, 300);
        this.setLocation(300, 300);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);

        this.jta.setEditable(false);

        jb.addActionListener(this);
        jtf.addKeyListener(this);

        /*************** TCP - Server ***************/
        try {
            ServerSocket serverSocket = new ServerSocket(0);
            /** Properties **/
            Properties properties = new Properties();
            properties.load(new FileReader("./src/com/lance/chat/chat.properties"));
            properties.setProperty("serverPort", String.valueOf(serverSocket.getLocalPort()));
            properties.store(new FileWriter("./src/com/lance/chat/chat.properties"), "Update Server Port");
            /** Properties **/
            Socket socket = serverSocket.accept();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            String currLine = null;
            while ((currLine = bufferedReader.readLine()) != null) jta.append(currLine + System.lineSeparator());

            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*************** TCP - Server ***************/
    }

    public static void main(String[] args) {
        new ServerChatMain();
    }

    private boolean sendText() {
        String text = jtf.getText();
        text = "Server tells client: " + text;
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
        if (e.getKeyCode() == KeyEvent.VK_ENTER) this.sendText();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
