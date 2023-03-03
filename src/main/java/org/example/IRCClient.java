package org.example;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Scanner;

public class IRCClient extends Observable {
    String serverName;
    public String nick;
    String user;

    private int port;
    private Socket ClientSocket;

    private Thread talk;
    private Thread cmd;

    /*private ArrayList<String> channelList = new ArrayList<>();
    public int channelIndex = -1;*/

    private HashMap<String, String> channelList = new HashMap<String, String>();
    private HashMap<String, String> userList = new HashMap<String, String>();
    public String wheretoSend = null;

    String defaultchat = "";


    BufferedWriter writer;
    BufferedReader reader;


    IRCClient(String _serverName, int _port, String _nickname, String _user){
        if(_serverName != null) this.serverName = _serverName;
        this.port = _port;
        this.nick = _nickname;
        this.user = _user;

        try {
            this.ClientSocket = new Socket(this.serverName, this.port);
            this.Buffer();

            login(this.writer);

            System.out.println("================ connect " + this.serverName + " success ================");

            Scanner sysScanner = new Scanner(System.in);

            talk = new Thread(() -> {
                try {
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        if(wheretoSend == null){
                            defaultchat += line + "\n";
                        }

                        else if(wheretoSend.charAt(0) == '@'){

                            String username = wheretoSend.substring(1);


                            String chat = userList.get(username);
                            chat += line + "\n";

                            userList.replace(username, chat);
                        }

                        else{
                            String chat = channelList.get(wheretoSend);
                            chat += line + "\n";

                            channelList.replace(wheretoSend, chat);
                        }
                        //System.out.println(chat);

                        this.setChanged();
                        this.notifyObservers();
                    }

                } catch (Exception e) {
                    System.err.println(e.getMessage());
                    login(writer);
                }
            });
            talk.start();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setwheretoSend(String channel){
        this.wheretoSend = channel;
    }
    public HashMap<String, String> getChannel(){
        return channelList;
    }
    public HashMap<String,String> getUser(){
        return userList;
    }

    public void joinGroup(String channelName){
        join(writer, channelName);
    }

    public void setUserMessage(String userName){
        userList.put(userName, "");
        setwheretoSend("@" + userName);
    }


    public void Buffer() throws IOException {
        this.writer = new BufferedWriter(new OutputStreamWriter(this.ClientSocket.getOutputStream()));
        this.reader = new BufferedReader(new InputStreamReader(this.ClientSocket.getInputStream()));
    }

    public void login(BufferedWriter writer) {
        try {
            writer.write("NICK " + this.nick + "\r\n");
            writer.write("USER " + this.user + " 0 * : teste\r\n");
            writer.flush();
        } catch (IOException e) {
            System.out.println("== login error ==" + e.getMessage());
            System.exit(0);
        }
    }
    public void join(BufferedWriter writer, String channelName) {
        try {
            writer.write("JOIN " + "#" + channelName + "\r\n");
            //writer.write("PRIVMSG " + "#" + channelName + " : Hello Everyone!\r\n");

            channelList.put(channelName, "");
            wheretoSend = channelName;

            writer.flush();

        } catch (IOException e) {
            System.err.println("================ join error " + e.getMessage());
            login(writer);
        }
    }

    public void sendMessage(String message) throws IOException {

        if(wheretoSend == null){
            writer.write(message + "\r\n");
            writer.flush();
        }

        else if(wheretoSend.charAt(0) == '@'){
            String username = wheretoSend.substring(1);

            String msg = "PRIVMSG " + username + " :" + message + "\r\n";

            writer.write(msg);

            String chat = userList.get(username);
            chat += msg;
            userList.replace(username, chat);

            writer.flush();

            this.setChanged();
            this.notifyObservers();
        }

        else{
            String msg = "PRIVMSG #" + wheretoSend + " :" + message + "\r\n";
            writer.write(msg);

            String chat = channelList.get(wheretoSend);
            chat += msg;
            channelList.replace(wheretoSend, chat);

            System.out.println(channelList.get(wheretoSend));
            writer.flush();

            this.setChanged();
            this.notifyObservers();
        }
    }


}