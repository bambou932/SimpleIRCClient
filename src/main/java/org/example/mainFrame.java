package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public class mainFrame extends JFrame implements ActionListener, Observer {

    private JPanel loginPanel;
    private JButton addButton;
    private JLabel label1;
    private JTextField idInput;     //not use

    private JPanel logoutPanel;     //not use
    private JLabel label2;
    private JButton logoutButton;

    private JPanel msgPanel;
    private JTextField chatField;
    private JButton exitButton;
    private JButton submitButton;

    private JFrame jframe;
    private JTextArea chatArea;

    private JPanel chatpListPanel;
    private JLabel label3;
    private JTextArea serverListArea;

    private Container tab;
    private CardLayout clayout;

    private ArrayList<IRCClient> ClientList;
    private int selectedIndex;

    String serverName;
    String portNum = "6667";
    String nickName;
    String channelName;


    mainFrame() {
        onShowing();
        addButton.addActionListener(this);
        submitButton.addActionListener(this);
        exitButton.addActionListener(this);
        ClientList = new ArrayList<>();
    }

    public void onShowing() {

        //longin
        loginPanel = new JPanel();
        loginPanel.setLayout(new BorderLayout());
        idInput = new JTextField(15);
        addButton = new JButton("addButton");
        label1 = new JLabel("대화명");
        loginPanel.add(addButton, BorderLayout.CENTER);

        //logout
        logoutPanel = new JPanel();
        logoutPanel.setLayout(new BorderLayout());
        label2 = new JLabel();
        logoutButton = new JButton("로그아웃");
        logoutPanel.add(label2, BorderLayout.CENTER);
        logoutPanel.add(logoutButton, BorderLayout.EAST);

        //chatField, exit, submit
        msgPanel = new JPanel();
        msgPanel.setLayout(new BorderLayout());
        chatField = new JTextField(30);
        chatField.setEditable(false); //로그인 하기 전에는 채팅입력 불가
        exitButton = new JButton("종료");
        submitButton = new JButton("submit");
        msgPanel.add(chatField, BorderLayout.CENTER);
        msgPanel.add(exitButton, BorderLayout.WEST);
        msgPanel.add(submitButton, BorderLayout.EAST);

        tab = new JPanel();
        clayout = new CardLayout();
        tab.setLayout(clayout);
        tab.add(loginPanel, "login");
        tab.add(logoutPanel, "logout");


        jframe = new JFrame("SimpleIRCClient");

        //chatArea
        chatArea = new JTextArea("", 10, 30);
        chatArea.setEditable(false);
        JScrollPane jsp = new JScrollPane(chatArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);


        chatpListPanel = new JPanel(); //채팅 참자가 리스트가 붙을 패널
        chatpListPanel.setLayout(new BorderLayout());
/*

        label3 = new JLabel("채팅 참가자"); // 라벨
        serverListArea =new JTextArea("",10,10); //채팅참가자를 나타낼 영역
        serverListArea.setEditable(false); //편집불가
        JScrollPane jsp2 = new JScrollPane(serverListArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        chatpListPanel.add(label3,BorderLayout.NORTH); //패널에 라벨과 스크롤을 갖다 붙임
        chatpListPanel.add(jsp2,BorderLayout.CENTER);

*/



        jframe.add(tab, BorderLayout.NORTH);
        jframe.add(jsp, BorderLayout.CENTER);
        jframe.add(chatpListPanel,BorderLayout.WEST);
        jframe.add(msgPanel, BorderLayout.SOUTH);

        clayout.show(tab, "login");

        jframe.pack();

        jframe.setResizable(false);
        jframe.setVisible(true);


        jframe.setSize(600,600);
        jframe.setLocation(100,120);
    }

    public void addClient(String _serverName, String _portNum, String nickName, String userName) {
        System.out.println(_portNum);
        if(_portNum.equals("")) ClientList.add(new IRCClient(_serverName, 6667, nickName, userName));
        else ClientList.add(new IRCClient(_serverName, Integer.parseInt(_portNum), nickName, userName));

        selectedIndex = ClientList.size() - 1;

        System.out.println(selectedIndex);
        System.out.println(ClientList);

        ClientList.get(selectedIndex).addObserver(this);
    }
    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource() == addButton){
            addServerFrame addServer = new addServerFrame();

            addServer.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    if(e.getSource() == addServer.acceptButton) {
                        serverName = addServer.serverNameText.getText();
                        portNum = addServer.portText.getText();
                        nickName = addServer.nickText.getText();
                        channelName = addServer.userText.getText();

                        addServer.dialog.dispose();

                        addClient(serverName, portNum, nickName, channelName);

                        chatField.setEditable(true);
                    }
                }
            });


        }

        if(e.getSource() == submitButton){
            String[] message = chatField.getText().split(" ");

            if(message[0].equals("/channel")){
                String result = "";
                for(int i=1;i< message.length;i++) result += message[i];

                HashMap<String, String> channellist = ClientList.get(selectedIndex).getChannel();
                if(channellist.get(result) == null){ //channel이 없으면 생성
                    channellist.put(result,"");
                    ClientList.get(selectedIndex).joinGroup(message[1]);
                }
                else{ //있으면 그 채널 선택
                    ClientList.get(selectedIndex).setwheretoSend(result);
                }

                System.out.println("Channel selected : " + result);
            }

            else if(message[0].equals("/user")){
                String result = "";
                for(int i=1;i< message.length;i++) result += message[i];

                System.out.println(result);

                HashMap<String, String> userlist = ClientList.get(selectedIndex).getUser();

                if(userlist.get(result) == null) {
                    ClientList.get(selectedIndex).setUserMessage(result);
                    chatArea.setText("");
                }

                else {
                    ClientList.get(selectedIndex).setwheretoSend("@" + result);
                }

            }

            else if(message[0].equals("/command")){
                String result = "";
                for(int i=1;i< message.length;i++) result += message[i];

                System.out.println(result);
                ClientList.get(selectedIndex).joinGroup(result);
            }

            else if(message[0].equals("/exit")){
                ClientList.get(selectedIndex).wheretoSend = null;
                chatArea.setText("");

            }
            else{ //명령어 체계가 아닐 때
                try {
                    ClientList.get(selectedIndex).sendMessage(chatField.getText());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

            chatField.setText("");
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        IRCClient client = (IRCClient) o;


        String s;
        String str ="";

        if(client.wheretoSend == null) {
            s = client.defaultchat;
            str += s;
        }

        else if(client.wheretoSend.charAt(0) == '@'){
            s = client.getUser().get(client.wheretoSend.substring(1));

            String[] messages = s.split("\n");

            for(int j=0;j< messages.length;j++) {
                String[] trim = messages[j].split("PRIVMSG");

                for (int i = 0; i < trim.length; i++) {
                    if (trim[i].equals("")) {
                        str += client.nick + "=> ";

                    } else {
                        if (trim[i].contains("!")) {
                            int idx = trim[i].indexOf("!");
                            String sub = trim[i].substring(0, idx);
                            sub += "=> ";
                            str += sub;
                        } else
                            str += trim[i] + "\n";
                    }
                }
            }


        }

        else{
            s = client.getChannel().get(client.wheretoSend);

            String[] trim = s.split("PRIVMSG");
            for (int i = 0; i < trim.length; i++) {
                if (trim[i].contains("!")) {
                    int idx = trim[i].indexOf("!");
                    String sub = trim[i].substring(0, idx);
                    sub += "=> ";
                    str += sub;
                }
                else
                    str += trim[i];
            }
        }

        chatArea.setText(str);
    }
}
