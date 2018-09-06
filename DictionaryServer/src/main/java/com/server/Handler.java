package com.server;

import com.messages.Message;
import com.messages.Status;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.Socket;

/**
 * @author xuwang < xuwang2@student.unimelb.edu.au >
 * @id 979895
 * @date 2018/9/1 0:37
 */
public class Handler extends Thread {
    private static Logger logger = LoggerFactory.getLogger(Handler.class);
    private ServerController controller;

    public Handler(ServerController controller){
        this.controller = controller;
    }

    @Override
    public void run() {
        // 3. While loop - listen for Client connection
        try{
            Socket socket;
            while (true) {
                // 4. Accept an incoming client connection request
                if (ServerController.on){
                    socket = ServerController.server.accept();
                    Information information = new Information(socket.getInetAddress().getHostAddress()+":"+socket.getPort(),"Connected","","Success");
                    controller.setClient(information);
                    logger.info("Connected to a socket: "+socket.getInetAddress().getHostAddress()+":"+socket.getPort());
                    // Read dictionary file path
                    String filePath = System.getProperty("user.dir") + File.separator + "dictionary.json";
                    // 5. Create a client connection to listen for and process all the messages sent by the client
                    MessageListener thread = new MessageListener(socket, filePath,controller);
                    thread.start();
                }

            }
        } catch (Exception e) {
            //todo write
            logger.error("ServerSocket closed:" + e);
        }
    }
}
