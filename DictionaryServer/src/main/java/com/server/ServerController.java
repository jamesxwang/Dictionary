package com.server;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author xuwang < xuwang2@student.unimelb.edu.au >
 * @id 979895
 * @date 2018/9/1 14:43
 */
public class ServerController implements Initializable {

    @FXML private BorderPane borderPane;
    @FXML private TextField hostfield;
    @FXML private TextField portfield;
    @FXML private ImageView serverStatus;
    @FXML private Button statusButton;
    @FXML public TableView<Information> table;
    @FXML public TableColumn HostAddressCol;
    @FXML public TableColumn ActionCol;
    @FXML public TableColumn WordCol;
    @FXML public TableColumn ResponseCol;

    public static ServerSocket server;
    private double xOffset;
    private double yOffset;
    private static String HOST;
    private static int PORT;
    private static ServerController instance;
    private static Logger logger = LoggerFactory.getLogger(ServerController.class);
    public static boolean on;
    public static ObservableList<Information> data = FXCollections.observableArrayList();

    public ServerController(){
        instance = this;
    }

    public static ServerController getInstance() {
        return instance;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        on = false;
        serverStatus.setImage(new Image(getClass().getClassLoader().getResource("images/off.png").toString()));
        // Initial TableView
        Information information = new Information("","","","");
        HostAddressCol.setCellValueFactory(new PropertyValueFactory<>("HostAddress"));
        ActionCol.setCellValueFactory(new PropertyValueFactory<>("Action"));
        WordCol.setCellValueFactory(new PropertyValueFactory<>("Word"));
        ResponseCol.setCellValueFactory(new PropertyValueFactory<>("Response"));
        setClient(information);

        //Drag and Drop animation
        //<editor-fold defaultstate="collapsed" desc=" Drag and Drop">
        borderPane.setOnMousePressed(event -> {
            xOffset = Server.getPrimaryStage().getX() - event.getScreenX();
            yOffset = Server.getPrimaryStage().getY() - event.getScreenY();
            borderPane.setCursor(Cursor.CLOSED_HAND);
        });

        borderPane.setOnMouseDragged(event -> {
            Server.getPrimaryStage().setX(event.getScreenX() + xOffset);
            Server.getPrimaryStage().setY(event.getScreenY() + yOffset);

        });

        borderPane.setOnMouseReleased(event -> {
            borderPane.setCursor(Cursor.DEFAULT);
        });
        //</editor-fold>
    }

    public void setClient(Information information){
        data.add(information);
        table.setItems(data);
    }

    //Pressing Start button
    public void startServer(){
        //If ServerSocket is off - need to start
        if (!on) {
            HOST = hostfield.getText();
            PORT = Integer.parseInt(portfield.getText());

            //1. Open the server
            try {
                server = new ServerSocket(PORT);
                logger.info("Starting Server on port " + PORT);
                logger.info("Waiting for a connection...");
                serverStatus.setImage(new Image(getClass().getClassLoader().getResource("images/on.png").toString()));
                statusButton.setText("Stop");
                on = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // If the ServerSocket in on - need to stop
            try {
                server.close();
                on = false;
                serverStatus.setImage(new Image(getClass().getClassLoader().getResource("images/off.png").toString()));
                statusButton.setText("Start");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //2. Thread for listening for connection
        new Handler(this).start();
    }

    // Terminates Application
    public void closeSystem(){
        Platform.exit();
        System.exit(0);
    }
}
