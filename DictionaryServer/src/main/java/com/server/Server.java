package com.server;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;

/**
 * @author xuwang < xuwang2@student.unimelb.edu.au >
 * @id 979895
 * @date 2018/9/1 14:40
 */
public class Server extends Application {

    private static Stage primaryStageObj;
    private static Logger logger = LoggerFactory.getLogger(Server.class);

    public static Stage getPrimaryStage() {
        return primaryStageObj;
    }

    // Java FX
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStageObj = primaryStage;
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/server.fxml"));
        primaryStage.initStyle(StageStyle.UNDECORATED);
        Scene scene = new Scene(root, 700, 600);
        primaryStage.setTitle("Author : Xu Wang <xuwang2@student.unimelb.edu.au>");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> Platform.exit());
    }

    public static void main(String[] args) {
        launch(args);
        logger.info("Server GUI launched");
    }
}

