package com.client;

import com.messages.Message;
import com.messages.Status;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * @author xuwang < xuwang2@student.unimelb.edu.au >
 * @id 979895
 * @date 2018/8/29 17:31
 */
public class Controller implements Initializable {
    @FXML private TextField wordTextfield;
    @FXML private TextField addTextfield;
    @FXML private TextField definitionField;
    @FXML private BorderPane borderPane;
    @FXML private GridPane addingArea;
    private double xOffset;
    private double yOffset;
    private static final int Dic_Width = 700;
    private static final int Dic_Height = 600;
    private static Controller instance;
    private static Logger logger = LoggerFactory.getLogger(Controller.class);



    public Controller() {
        instance = this;
    }

    public static Controller getInstance() {
        return instance;
    }

    //<editor-fold defaultstate="collapsed" desc="//Search Action">
    // Action when press down the search button
    public void SearchAction() throws IOException {
        addingArea.setVisible(false);
        definitionField.setVisible(true);

        String word = wordTextfield.getText().trim();
        if (wordTextfield.getText().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("ERROR");
            alert.setContentText("Please enter a word !");
            alert.showAndWait();
            definitionField.clear();
        } else {
            MessageListener.search(word);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="//Add Action">
    // Action when press down the add button
    public void AddAction(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("CONFIRMATION");
        alert.setContentText("Do you want to add the word to the dictionary?");
        Optional result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            addingArea.setVisible(true);
            addTextfield.clear();
            definitionField.setVisible(false);
        }
    }
    //add word to the dictionary
    public void add() throws IOException {
        String word = wordTextfield.getText().trim();
        String definition = addTextfield.getText().trim();
        List<String> def = new ArrayList<>();
        def.add(definition);
        if ( word.isEmpty() || definition.isEmpty() ){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("ERROR");
            alert.setContentText("What are you trying to add ?");
            alert.showAndWait();
        }else {
            MessageListener.add(word,def);
        }
    }

    // Add definition to the definition textField
    public synchronized void addToDef(Message msg){
        if (msg.getStatus() == Status.DONT_EXIST) {
            Platform.runLater(()->{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("ERROR");
                alert.setContentText("Word \'"+ msg.getMsg() + "\' doesn't exist.");
                alert.showAndWait();
                definitionField.clear();
            });
        }else {
            String definition = String.join(", ",msg.getDef());
            definitionField.setText(definition);
        }
    }

    // Added new word successful
    public void addWord(Message msg){
        if (msg.getStatus() == Status.EXIST) {
            Platform.runLater(()->{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("ERROR");
                alert.setContentText("Word \'"+ msg.getMsg() + "\' already exist, adding failure...");
                alert.showAndWait();
                addTextfield.clear();
            });
        } else {
            Platform.runLater(()->{
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("SUCCESS!");
                alert.setContentText("Adding word \'"+msg.getMsg()+"\' success.");
                alert.showAndWait();
                wordTextfield.clear();
                addTextfield.clear();
            });
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="//Delete Action">
    // Action when press down the delete button
    public void DelAction() throws IOException {
        String word = wordTextfield.getText().trim();
        addingArea.setVisible(false);
        definitionField.setVisible(true);
        definitionField.clear();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("CONFIRMATION");
        alert.setContentText("Do you want to delete the word \'"+word+"' ?");
        Optional result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            if (wordTextfield.getText().trim().isEmpty()) {
                Alert alert1 = new Alert(Alert.AlertType.ERROR);
                alert1.setTitle("Error");
                alert1.setHeaderText("ERROR");
                alert1.setContentText("What are you trying to delete ?");
                alert1.showAndWait();

            } else {
                MessageListener.delete(word);
            }
        }
    }


    public void deleteWord(Message msg){
        if (msg.getStatus() == Status.DONT_EXIST){
            Platform.runLater(()->{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("ERROR");
                alert.setContentText("Word \'"+ msg.getMsg() + "\' doesn't exist, deleting failure...");
                alert.showAndWait();
                definitionField.clear();
            });
        } else {
            Platform.runLater(()->{
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("SUCCESS!");
                alert.setContentText("Deleting word \'"+ msg.getMsg() + "\' success!");
                alert.showAndWait();
                definitionField.clear();
            });
        }
    }
    //</editor-fold>

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //Socket per Connection
        MessageListener messageListener = new MessageListener(this);
        Thread x = new Thread(messageListener);
        x.start();

        //Drag and Drop animation
        //<editor-fold defaultstate="collapsed" desc=" Drag and Drop">
        borderPane.setOnMousePressed(event -> {
            xOffset = Client.getPrimaryStage().getX() - event.getScreenX();
            yOffset = Client.getPrimaryStage().getY() - event.getScreenY();
            borderPane.setCursor(Cursor.CLOSED_HAND);
        });

        borderPane.setOnMouseDragged(event -> {
            Client.getPrimaryStage().setX(event.getScreenX() + xOffset);
            Client.getPrimaryStage().setY(event.getScreenY() + yOffset);

        });

        borderPane.setOnMouseReleased(event -> {
            borderPane.setCursor(Cursor.DEFAULT);
        });
        //</editor-fold>

        //Background square number settings
        int numberOfSquares = 30;
        while (numberOfSquares > 0){
            generateAnimation();
            numberOfSquares--;
        }
    }

    // Background square animation
    //<editor-fold defaultstate="collapsed" desc=" Background animation">
    /* This method is used to generate the animation on the main window.
     * It will generate random ints to determine
     * the size, speed, starting points and direction of each square.
     */
    public void generateAnimation(){
        Random rand = new Random();
        int sizeOfSqaure = rand.nextInt(50) + 1;
        int speedOfSqaure = rand.nextInt(10) + 5;
        int startXPoint = rand.nextInt(Dic_Height);
        int startYPoint = rand.nextInt(Dic_Width);
        int direction = rand.nextInt(5) + 1;

        KeyValue moveXAxis = null;
        KeyValue moveYAxis = null;
        Rectangle r1 = null;

        switch (direction){
            case 1 :
                // MOVE LEFT TO RIGHT
                r1 = new Rectangle(0,startYPoint,sizeOfSqaure,sizeOfSqaure);
                moveXAxis = new KeyValue(r1.xProperty(), Dic_Width -  sizeOfSqaure);
                break;
            case 2 :
                // MOVE TOP TO BOTTOM
                r1 = new Rectangle(startXPoint,0,sizeOfSqaure,sizeOfSqaure);
                moveYAxis = new KeyValue(r1.yProperty(), Dic_Height - sizeOfSqaure);
                break;
            case 3 :
                // MOVE LEFT TO RIGHT, TOP TO BOTTOM
                r1 = new Rectangle(startXPoint,0,sizeOfSqaure,sizeOfSqaure);
                moveXAxis = new KeyValue(r1.xProperty(), Dic_Width -  sizeOfSqaure);
                moveYAxis = new KeyValue(r1.yProperty(), Dic_Height - sizeOfSqaure);
                break;
            case 4 :
                // MOVE BOTTOM TO TOP
                r1 = new Rectangle(startXPoint,Dic_Width-sizeOfSqaure ,sizeOfSqaure,sizeOfSqaure);
                moveYAxis = new KeyValue(r1.xProperty(), 0);
                break;
            case 5 :
                // MOVE RIGHT TO LEFT
                r1 = new Rectangle(Dic_Height-sizeOfSqaure,startYPoint,sizeOfSqaure,sizeOfSqaure);
                moveXAxis = new KeyValue(r1.xProperty(), 0);
                break;
            case 6 :
                //MOVE RIGHT TO LEFT, BOTTOM TO TOP
                r1 = new Rectangle(startXPoint,0,sizeOfSqaure,sizeOfSqaure);
                moveXAxis = new KeyValue(r1.xProperty(), Dic_Width -  sizeOfSqaure);
                moveYAxis = new KeyValue(r1.yProperty(), Dic_Height - sizeOfSqaure);
                break;

            default:
                System.out.println("default");
        }

        r1.setFill(Color.web("#F89406"));
        r1.setOpacity(0.1);

        KeyFrame keyFrame = new KeyFrame(Duration.millis(speedOfSqaure * 1000), moveXAxis, moveYAxis);
        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setAutoReverse(true);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
        borderPane.getChildren().add(borderPane.getChildren().size()-1,r1);
    }
    //</editor-fold>

    // Terminates Application
    public void closeSystem(){
        logger.info("Client GUI closed by the user.");
        Platform.exit();
        System.exit(0);
    }

    // Minimize Window
    public void minimizeWindow(){
        logger.info("Client GUI minimized by the user");
        Client.getPrimaryStage().setIconified(true);
    }
}