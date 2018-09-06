package com.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xuwang < xuwang2@student.unimelb.edu.au >
 * @id 979895
 * @date 2018/8/19 19:34
 */

/**
 * Client Application using JavaFX
 */
public class Client extends Application {

    private static Logger logger = LoggerFactory.getLogger(Client.class);
    private static Stage primaryStageObj;
    protected static int PORT = 8081;
    protected static String HOST = "127.0.0.1";

    public static void main(String[] args) {
        if (args.length != 0) {
            try {
                HOST = args[0];
                PORT = Integer.parseInt(args[1]);
            } catch (Exception e){
                logger.error("Correct format: java -jar Client.jar <server-address> <server-port>");
            }
        } else {
            logger.info("Didn't initialize the port address... using default address: " +HOST+ ":" + PORT);
        }
        launch(args);
    }

    public static Stage getPrimaryStage() {
        return primaryStageObj;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStageObj = primaryStage;
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/ui.fxml"));
        primaryStage.initStyle(StageStyle.UNDECORATED);
        Scene scene = new Scene(root, 700, 600);
        primaryStage.setTitle("Author : Xu Wang <xuwang2@student.unimelb.edu.au>");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> Platform.exit());
    }
}
