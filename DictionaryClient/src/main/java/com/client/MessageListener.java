package com.client;

import com.messages.Message;
import com.messages.MessageType;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

/**
 * @author xuwang < xuwang2@student.unimelb.edu.au >
 * @id 979895
 * @date 2018/8/29 15:38
 */
public class MessageListener implements Runnable {

    private Socket socket;
    public static int PORT = Client.PORT;
    private static String HOST = Client.HOST;
    private Controller controller;
    private static ObjectOutputStream oos;
    private InputStream is;
    private ObjectInputStream input;
    private OutputStream outputStream;
    private static Logger logger = LoggerFactory.getLogger(MessageListener.class);

    public MessageListener(Controller controller){
        this.controller = controller;
    }

    @Override
    public void run() {
        try{
            // Create a stream socket and connect it to the server
            socket = new Socket(HOST, PORT);
            logger.info("Connected to socket: " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + "\n");

            //Get the input/output streams for reading/writing data from/to the socket
            outputStream = socket.getOutputStream();
            oos = new ObjectOutputStream(outputStream);
            is = socket.getInputStream();
            input = new ObjectInputStream(is);

        } catch (IOException e) {
            con_lost();
            logger.error("Could not connect to the server.");
            e.printStackTrace();
        }

        try {
            //Read definitions from the server
            while (socket.isConnected()){
                Message message = null;
                message = (Message) input.readObject();
                //todo
                if (message!=null) {
                    switch (message.getType()) {
                        case SEARCH:
                            controller.addToDef(message);
                            break;
                        case ADD:
                            controller.addWord(message);
                            break;
                        case DELETE:
                            controller.deleteWord(message);
                            break;
                    }
                }
            }
        } catch (SocketException e) {
            con_lost();
            logger.error("Connection lost.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public synchronized static void search(String word) throws IOException {
        Message message = new Message();
        message.setType(MessageType.SEARCH);
        message.setMsg(word);
        // Send the input string to the server by writing to the socket output stream
        oos.writeObject(message);
        oos.flush();
        logger.info("Searching for: "+ word);
    }
    public synchronized static void add(String word, List<String> def) throws IOException {
        Message message = new Message();
        message.setType(MessageType.ADD);
        message.setMsg(word);
        String definition = String.join(", ",def);
        message.setDef(definition);
        // Send the input string to the server by writing to the socket output stream
        oos.writeObject(message);
        oos.flush();
        logger.info("Adding word: "+ word + " with definition: " + definition);
    }
    public synchronized static void delete(String word) throws IOException {
        Message message = new Message();
        message.setType(MessageType.DELETE);
        message.setMsg(word);
        // Send the input string to the server by writing to the socket output stream
        oos.writeObject(message);
        oos.flush();
        logger.info("Deleting word: "+ word);
    }

    private void con_lost() {
        Platform.runLater(()->{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("CONNECTION LOST");
            alert.setContentText("Could not connect to the server on port " + PORT + ", please check the server...");
            alert.showAndWait();
            controller.closeSystem();
        });
    }
}
