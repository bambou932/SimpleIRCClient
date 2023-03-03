package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class addServerFrame extends JFrame{
    public JDialog dialog = new JDialog();
    private JPanel panel1;
    public JTextField serverNameText;
    public JTextField portText;
    public JTextField nickText;
    public JTextField userText;
    public JButton acceptButton;


    private JTextArea acceptTextArea;
    private JLabel serverNameLabel;
    private JLabel portLabel;
    private JLabel nickLabel;

    /*String serverName;
    String portNum = "6667";
    String nickName;
    String channelName;*/

    public Boolean valueEntered = false;

   /* private String serverName = null;
    private String portNum = null;
    private String nickName = null;
    private String channelName = null;*/

    addServerFrame(){
        this.onShowing();
        serverNameText.setText("irc.overthewire.org");
        portText.setText("6667");
        nickText.setText("user1234");
        userText.setText("user123");
    }

    public void onShowing() {
        dialog.add(panel1, BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocation(200, 200);
        dialog.setVisible(true);
    }

/*
    public String[] getValue(){
        return new String[] {serverName, portNum, nickName, channelName};
    }
*/

    /*@Override
    public void actionPerformed(ActionEvent e) {
        if(acceptButton == e.getSource()){
                serverName = serverNameText.getText();
                if (portNum != null) portNum = portText.getText();
                nickName = nickText.getText();
                channelName = channelText.getText();

                System.out.println(serverName + " " + portNum + " " + nickName + " " + channelName);
                //Main.getValue(serverName, portNum, nickName, channelName);

                valueEntered = true;
                dialog.dispose();

        }
    }*/

    public void addActionListener(ActionListener listener) {
        acceptButton.addActionListener(listener);
    }
}
