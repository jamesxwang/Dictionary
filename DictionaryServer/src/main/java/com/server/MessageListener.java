package com.server;

import com.messages.Message;
import com.messages.MessageType;
import com.messages.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.lang.Thread;

/**
 * @author xuwang < xuwang2@student.unimelb.edu.au >
 * @id 979895
 * @date 2018/9/1 19:40
 */
public class MessageListener extends Thread {

    private Socket serverSocket;
    private Logger logger = LoggerFactory.getLogger(MessageListener.class);
    private ObjectInputStream input;
    private OutputStream os;
    private ObjectOutputStream output;
    private InputStream is;
    private Dictionary dictionary;
    private String filePath;
    private ServerController controller;

    MessageListener(Socket socket, String filePath, ServerController controller) {
        this.serverSocket=socket;
        this.filePath = filePath;
        this.dictionary =new Dictionary(filePath);
        this.controller = controller;
    }

    @Override
    public void run() {
        try {
            //IO Stream
            is = serverSocket.getInputStream();
            input = new ObjectInputStream(is);
            os = serverSocket.getOutputStream();
            output = new ObjectOutputStream(os);

            // Listen from the input stream
            while(serverSocket.isConnected()) {
                Message msg = (Message) input.readObject();
                if (msg != null){
                    switch (msg.getType()) {
                        case SEARCH:
                            logger.info("Client on port " + serverSocket.getPort()+" Searching for: "+ msg.getMsg());
                            search(msg);
                            Information searchInfo;
                            if (msg.getStatus() == Status.DONT_EXIST){
                                searchInfo = new Information(serverSocket.getInetAddress().getHostAddress() + ":" + serverSocket.getPort(),"Search",msg.getMsg(),"Failure, no definition");
                            }else {
                                searchInfo = new Information(serverSocket.getInetAddress().getHostAddress() + ":" + serverSocket.getPort(),"Search",msg.getMsg(),"Success");
                            }
                            controller.setClient(searchInfo);
                            break;
                        case ADD:
                            logger.info("Client on port " + serverSocket.getPort()+" Attempting to add: "+ msg.getMsg());
                            add(msg);
                            Information addInfo;
                            if (msg.getStatus() == Status.EXIST){
                                addInfo = new Information(serverSocket.getInetAddress().getHostAddress() + ":" + serverSocket.getPort(),"Add",msg.getMsg(),"Failure, word already exist!");
                            }else {
                                addInfo = new Information(serverSocket.getInetAddress().getHostAddress() + ":" + serverSocket.getPort(),"Add",msg.getMsg(),"Success");
                            }
                            controller.setClient(addInfo);
                            break;
                        case DELETE:
                            logger.info("Client on port " + serverSocket.getPort()+" Attempting to delete: "+ msg.getMsg());
                            delete(msg);
                            Information deleteInfo;
                            if (msg.getStatus() == Status.DONT_EXIST){
                                deleteInfo = new Information(serverSocket.getInetAddress().getHostAddress() + ":" + serverSocket.getPort(),"Delete",msg.getMsg(),"Failure, word doesn't exist!");
                            }else {
                                deleteInfo = new Information(serverSocket.getInetAddress().getHostAddress() + ":" + serverSocket.getPort(), "Delete", msg.getMsg(), "Success");
                            }
                            controller.setClient(deleteInfo);
                            break;
                    }
                }
            }
        } catch (IOException e) {
            Information connectInfo = new Information(serverSocket.getInetAddress().getHostAddress() + ":" + serverSocket.getPort(),"Close UI","","Disconnected");
            controller.setClient(connectInfo);
            logger.info("Connection on port " + serverSocket.getPort() + " closed by the client.");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
                os.close();
                input.close();
                is.close();
                serverSocket.close();
            } catch (IOException e) {
                logger.error("Exception in MessageListener: "+ e );
            }
        }
    }

    private void search(Message msg){
        List<String> def;
        dictionary.readJSONFile(filePath);
        if (dictionary.hasWord(msg.getMsg())){
            msg.setType(MessageType.SEARCH);
            def = dictionary.getDefinitions(msg.getMsg());
            String definition = String.join(", ",def);
            msg.setDef(definition);
            write(msg);
        } else {
            msg.setType(MessageType.SEARCH);
            msg.setStatus(Status.DONT_EXIST);
            write(msg);
        }
    }

    private void add(Message msg) {
        dictionary.readJSONFile(filePath);
        String word = msg.getMsg();
        String definition = msg.getDef();
        if (dictionary.hasWord(msg.getMsg())){
            msg.setStatus(Status.EXIST);
            msg.setType(MessageType.ADD);
            write(msg);
        } else {
            msg.setType(MessageType.ADD);
            List<String> def = new ArrayList<>();
            def.add(definition);
            dictionary.addWord(word,def);
            write(msg);
        }
    }

    private void delete(Message msg) {
        dictionary.readJSONFile(filePath);
        String word = msg.getMsg();
        if (dictionary.hasWord(msg.getMsg())){
            msg.setType(MessageType.DELETE);
            dictionary.removeWord(word);
            write(msg);
        } else {
            msg.setType(MessageType.DELETE);
            msg.setStatus(Status.DONT_EXIST);
            write(msg);
        }
    }

    // Needs to be synchronized because multiple threads can be invoking this method at the same time
    private synchronized void write(Message msg)  {
        try {
            output.writeObject(msg);
            output.flush();
            output.reset();
            logger.info("Respond to client on port "+ serverSocket.getPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
