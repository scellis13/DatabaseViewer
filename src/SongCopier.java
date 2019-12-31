/*
    Last Updated: 12/21/2019
    Updated On: Sean's Computer
*/
package grdb;

import com.jcraft.jsch.*;

import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.Time;
import java.util.*;
import javafx.application.Platform;
import javafx.concurrent.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;

public class SongCopier {
    private final SelectionDisplay selectionDisplay;
    //SongCopier Variables
    private Task<Void> copyTasker;
    private Thread copyThread;
    private BorderPane pane;
    private HashMap<String, ProgressIndicator> progressMap = new HashMap<>();
    private ArrayList<String> songList = new ArrayList<>();
    private ArrayList<String> completedCopies = new ArrayList<>();
    private ArrayList<String> failedCopies = new ArrayList<>();
    private boolean copying = false;
    private int copiedCount, missingCount;
    private static final Image CLEAR_BUTTON_IMG = new Image(GRDB.class.getResourceAsStream("/resources/pics/clear_button.jpg"));
    private static final Image CLEAR_BUTTON_IMG_PRESSED = new Image(GRDB.class.getResourceAsStream("/resources/pics/clear_button_pressed.png"));
    private static final Image CLOSE_BUTTON_IMG_NORMAL = new Image(GRDB.class.getResourceAsStream("/resources/pics/closeButton_normal.png"));
    private static final Image CLOSE_BUTTON_IMG_HIGHLIGHT = new Image(GRDB.class.getResourceAsStream("/resources/pics/closeButton_highlight.png"));
    private static final Image CLOSE_BUTTON_IMG_PRESSED = new Image(GRDB.class.getResourceAsStream("/resources/pics/closeButton_pressed.png"));
    
    //Top Pane Variables
        private Label copierLabel, copierLabelStatus, fileQueueLabel, fileQueueLabelCount;
        private HBox topContentContainer;
        private Button closePaneButton;
    //Left Pane Variables
        private VBox leftPaneContent;
        //Header Section Variables
        private Label leftListLabel;
        //LeftPaneContent Variables
        private Button startCopy, cancelCopy, clearQueue;
    //Right Pane Variables
        private VBox rightPaneContent;
        //History and Folder Section Variables
        private Label rightListLabel;
    
    public SongCopier(SelectionDisplay selectionDisplay){
        this.selectionDisplay = selectionDisplay;
        initializePane();
    }
    
    private void initializePane(){
        pane = new BorderPane();
        pane.setStyle("-fx-border-color: black; -fx-font-family: Times New Roman; "
                + "-fx-font-size: 14px;");
    }
    
    public BorderPane getPane(){
        return this.pane;
    }
    
    public void setContent(){
        pane.setTop(createTopPane());
        pane.setCenter(createCenterPane());
    }
    
    public int getCopyListCount(){
        return progressMap.size();
    }
    
    /*Start of Top Pane Section
    
    */
    private HBox createTopPane(){
        topContentContainer = new HBox();
        //topContentContainer.setStyle("-fx-border-color: black;");  
        topContentContainer.setMaxWidth(pane.getMaxWidth()*.5);
        topContentContainer.setPadding(new Insets(20, 20, 20, 20));
        topContentContainer.setStyle("-fx-font-size: 16px;");
        
            VBox topLeftContentContainer = new VBox();
            //topLeftContentContainer.setStyle("-fx-border-color: black;");
            topLeftContentContainer.setMinWidth(topContentContainer.getMaxWidth()*.5);
            topLeftContentContainer.setMaxWidth(topContentContainer.getMaxWidth()*.5);
                HBox topLabelContainer = new HBox();
                //topLabelContainer.setStyle("-fx-border-color: black;");
                topLabelContainer.setPrefWidth(topLeftContentContainer.getMaxWidth()*.5);
                topLabelContainer.setAlignment(Pos.CENTER_LEFT);
                topLabelContainer.setSpacing(10);
                    copierLabel = new Label("File Copier:");
                    copierLabelStatus = new Label(CopierLabelEnum.Pending.toString());
                topLabelContainer.getChildren().addAll(copierLabel, copierLabelStatus);
                
                HBox bottomLabelContainer = new HBox();
                //bottomLabelContainer.setStyle("-fx-border-color: black;");
                bottomLabelContainer.setPrefWidth(topLeftContentContainer.getMaxWidth()*.5);
                bottomLabelContainer.setAlignment(Pos.CENTER_LEFT);
                bottomLabelContainer.setSpacing(10);
                    fileQueueLabel = new Label("Files Queued:");
                    fileQueueLabelCount = new Label("" + progressMap.size());
                bottomLabelContainer.getChildren().addAll(fileQueueLabel, fileQueueLabelCount);
            topLeftContentContainer.getChildren().addAll(topLabelContainer, bottomLabelContainer);
            
            
            VBox topRightContentContainer = new VBox();
            //topRightContentContainer.setStyle("-fx-border-color: black;");
            topRightContentContainer.setAlignment(Pos.TOP_RIGHT);
            topRightContentContainer.setMinWidth(topContentContainer.getMaxWidth()*.5);
            topRightContentContainer.setMaxWidth(topContentContainer.getMaxWidth()*.5);
                closePaneButton = new Button();
                closePaneButton.setMinSize(30,30);
                closePaneButton.setMaxSize(30,30);
                    BackgroundImage backgroundImageCloseNormal = new BackgroundImage(
                        CLOSE_BUTTON_IMG_NORMAL,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundPosition.DEFAULT,
                        BackgroundSize.DEFAULT);
                    Background backgroundCloseNormal = new Background(backgroundImageCloseNormal);

                    BackgroundImage backgroundImageCloseHighlight = new BackgroundImage(
                        CLOSE_BUTTON_IMG_HIGHLIGHT,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundPosition.DEFAULT,
                        BackgroundSize.DEFAULT);
                    Background backgroundCloseHighlight = new Background(backgroundImageCloseHighlight);

                    BackgroundImage backgroundImageClosePressed = new BackgroundImage(
                        CLOSE_BUTTON_IMG_PRESSED,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundPosition.DEFAULT,
                        BackgroundSize.DEFAULT);
                    Background backgroundClosePressed = new Background(backgroundImageClosePressed);

                closePaneButton.setBackground(backgroundCloseNormal);
                closePaneButton.setOnMousePressed(event -> {
                    closePaneButton.setBackground(backgroundClosePressed);
                });
                closePaneButton.setOnMouseReleased(event -> {
                    closePaneButton.setBackground(backgroundCloseNormal);
                });
                closePaneButton.setOnMouseEntered(event ->{
                    closePaneButton.setBackground(backgroundCloseHighlight);
                });
                closePaneButton.setOnMouseExited(event ->{
                    closePaneButton.setBackground(backgroundCloseNormal);
                });
                
                
            topRightContentContainer.getChildren().addAll(closePaneButton);
            
            
        topContentContainer.getChildren().addAll(topLeftContentContainer, topRightContentContainer);
        
        return topContentContainer;
    }
    
    public void setCopierLabelStatus(SongCopier.CopierLabelEnum e){
        copierLabelStatus.setText(e.toString());
    }
    
    public void updateFileQueueLabelCount(){
        fileQueueLabelCount.setText("" + progressMap.size());
    }
    
    public void setClosePaneButtonAction(EventHandler<ActionEvent> event){
        closePaneButton.setOnAction(event);
    }
    
    public void setCopyingStatus(boolean copying){
        this.copying = copying;
    }
    
    public boolean getCopyingStatus(){
        return this.copying;
    }
    
    public enum CopierLabelEnum{
        Pending, Queued, Copying, Canceled, Completed;
    }
    /*End of Top Pane Section
    */
    
    /*
        Start of Center Pane Section
    */
    private BorderPane createCenterPane(){
        BorderPane centerPane = new BorderPane();
        centerPane.setLeft(createLeftPane());
        //centerPane.setRight(createRightPane());
        centerPane.setPadding(new Insets(20, 20, 20, 20));
        return centerPane;
    }
    /* 
        Start of Left-Center Pane Section
    */
    private VBox createLeftPane(){
        VBox leftPaneContainer = new VBox();
            BorderPane header = new BorderPane();
            header.setMinSize((pane.getMaxWidth()*.5)-15, (pane.getMaxHeight()*.10)-15);
            header.setPadding(new Insets(0,20,0,20));
                HBox headerButtonsLeftSide = new HBox();
                headerButtonsLeftSide.setAlignment(Pos.BOTTOM_LEFT);
                headerButtonsLeftSide.setSpacing(10);
                
                    startCopy = new Button("Start Copy Process");
                    startCopy.setMinWidth(100);
                    startCopy.setMaxWidth(100);
                    startCopy.setDisable(true);
                    cancelCopy = new Button("Cancel");
                    cancelCopy.setDisable(true);
                    cancelCopy.setMinWidth(100);
                    cancelCopy.setMaxWidth(100);
                headerButtonsLeftSide.getChildren().addAll(startCopy, cancelCopy);
                
                Label leftPaneLabel = new Label("Files Queued for Copying");
                leftPaneLabel.setFont(Font.font("Times New Roman", FontWeight.BOLD, 24));
                leftPaneLabel.setAlignment(Pos.BOTTOM_CENTER);
                
                HBox headerButtonsRightSide = new HBox();
                headerButtonsRightSide.setAlignment(Pos.BOTTOM_RIGHT);
                headerButtonsRightSide.setSpacing(10);

                clearQueue = new Button("Clear Queue");
                clearQueue.setMinWidth(100);
                clearQueue.setMaxWidth(100);

                headerButtonsRightSide.getChildren().addAll(clearQueue);
            header.setLeft(headerButtonsLeftSide);
            header.setCenter(leftPaneLabel);
            header.setRight(headerButtonsRightSide);
                
            leftPaneContent = new VBox();
            leftPaneContent.setMinSize((pane.getMaxWidth()*.5)-15, (pane.getMaxHeight()*.75)-15);
            //leftPane.setMaxSize((pane.getMaxWidth()*.5)-15, (pane.getMaxHeight()*.75)-15);
            //leftPane.setStyle("-fx-border-color: black;");
            leftPaneContent.setAlignment(Pos.TOP_CENTER);
            leftPaneContent.setSpacing(15);
            leftPaneContent.setPadding(new Insets(10,10,10,10));
        
        ScrollPane container = new ScrollPane(leftPaneContent);
        container.setMinSize(pane.getMaxWidth()*.5, pane.getMaxHeight()*.75);
        container.setMaxSize(pane.getMaxWidth()*.5, pane.getMaxHeight()*.75);
        container.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        leftPaneContainer.getChildren().addAll(header, container);
        return leftPaneContainer;
    }
    
    public void clearLeftPaneContent(){
        leftPaneContent.getChildren().clear();
        progressMap.clear();
        songList.clear();
        updateFileQueueLabelCount();
    }
    
    
    public void addSongBox(String fileName){
        if(!progressMap.containsKey(fileName)){
            songList.add(fileName);
            HBox songContainer = new HBox();
                //Clear Button
                Button clearButton = new Button();
                    clearButton.setMinSize(30, 30);

                        //Normal View of Clear Button
                        BackgroundImage backgroundImageClear = new BackgroundImage(
                            CLEAR_BUTTON_IMG, 
                            BackgroundRepeat.NO_REPEAT, 
                            BackgroundRepeat.NO_REPEAT, 
                            BackgroundPosition.DEFAULT, 
                            BackgroundSize.DEFAULT);
                        Background backgroundNormalClear = new Background(backgroundImageClear);

                        //Pressed View of Clear Button
                        BackgroundImage backgroundImageClearPressed = new BackgroundImage(
                            CLEAR_BUTTON_IMG_PRESSED, 
                            BackgroundRepeat.NO_REPEAT, 
                            BackgroundRepeat.NO_REPEAT, 
                            BackgroundPosition.DEFAULT, 
                            BackgroundSize.DEFAULT);
                        Background backgroundNormalClearPressed = new Background(backgroundImageClearPressed);
                        //Set Actions to change Button Background    
                        clearButton.setBackground(backgroundNormalClear);
                        clearButton.setOnMousePressed(event -> {
                            clearButton.setBackground(backgroundNormalClearPressed);
                        });
                        clearButton.setOnMouseReleased(event -> {
                            clearButton.setBackground(backgroundNormalClear);
                        });

                clearButton.setOnAction(e->{
                    progressMap.remove(fileName);
                    songList.remove(fileName);
                    leftPaneContent.getChildren().remove(songContainer);
                    updateFileQueueLabelCount();
                    if(songList.isEmpty() && progressMap.isEmpty()){
                        startCopy.setDisable(true);
                        setCopierLabelStatus(CopierLabelEnum.Pending);
                    }
                });
                //End of Creating Clear Button

                ProgressIndicator pi = new ProgressIndicator();
                pi.setProgress(0);
                pi.setMaxSize(50, 50);
                progressMap.put(fileName, pi);


            songContainer.setSpacing(20);
            songContainer.setAlignment(Pos.CENTER_LEFT);
            songContainer.setStyle("-fx-font-size: 16;");
            
            songContainer.getChildren().addAll(clearButton, pi, new Label(fileName));

            leftPaneContent.getChildren().add(songContainer);
        }
        updateFileQueueLabelCount();
    }
    
    public Button getButton(ButtonSelection button){
        switch (button) {
            case START:
                return startCopy;
            case CANCEL:
                return cancelCopy;
            default:
                return clearQueue;
        }
    }
    
    public enum ButtonSelection{
        START, CANCEL, CLEAR;
    }
    
    /*
        End of Left Pane Section
    */
    
    /* 
        Start of Right-Center Pane Section
    */
    private VBox createRightPane(){
        VBox rightPaneContainer = new VBox();
            HBox rightPaneHeading = new HBox();
            rightPaneHeading.setStyle("-fx-border-color: black;");
            rightPaneHeading.setMinSize((pane.getMaxWidth()*.5)-15, (pane.getMaxHeight()*.10)-15);
        
            rightPaneContent = new VBox();
            rightPaneContent.setMinSize(pane.getMaxWidth()*.5, pane.getMaxHeight()*.75);
            rightPaneContent.setMaxSize(pane.getMaxWidth()*.5, pane.getMaxHeight()*.75);
            rightPaneContent.setStyle("-fx-border-color: black;");
        
        rightPaneContainer.getChildren().addAll(rightPaneHeading, rightPaneContent);
        
        return rightPaneContainer;
    }
    /*
        End of Left Pane Section
    */
    
    public void startCopy(DatabaseConnect databaseConnect){
        copyTasker = new Task<Void>(){
            @Override
            protected synchronized Void call() throws Exception{
                startCopyHelper(databaseConnect);
                return null;
            }
        };
        copyThread = new Thread(copyTasker);
        copyThread.setDaemon(true);
        copyThread.start();
    }
    
    public void startCopyHelper(DatabaseConnect databaseConnect){
        copiedCount = 0;
        missingCount = 0;

        try {
            int initialSize = songList.size();
                for(int i = 0; i < initialSize; i++){
                    if(!songList.isEmpty() && copying){
                        String copyStatus = "Failed";
                        String s = songList.get(0);
                        String command = "copy \"c:\\media\\" + s + "\" c:\\media\\new";
                        Channel channel = databaseConnect.getSession().openChannel("exec");
                        ((ChannelExec)channel).setCommand(command);
                        channel.setInputStream(null);
                        ((ChannelExec)channel).setErrStream(System.err);
                        InputStream in=channel.getInputStream();

                        try {
                        channel.connect();
                        byte[] tmp=new byte[1024];
                        while(true){
                          while(in.available()>0){
                            int j=in.read(tmp, 0, 1024);
                            if(j<0)break;
                          }    
                            if(channel.isClosed()){
                                if(in.available()>0) continue;
                                if(channel.getExitStatus()==0){
                                    progressMap.get(s).setProgress(100);                         
                                    copiedCount++;
                                    GRDB.totalCopies++;
                                    copyStatus = "Succeeded";
                                    Platform.runLater(()->{
                                        selectionDisplay.setCopyCount(GRDB.totalCopies);
                                    });
                                } else {
                                    missingCount++;
                                }
                                break;
                            }
                        }   
                        channel.disconnect();
                        } catch (Exception e){};
                        
                        progressMap.remove(s);
                        songList.remove(s);
                        
                        //Add Song Copy to MySQL History
                        Calendar calendar = Calendar.getInstance();
                        java.sql.Date date_added = new java.sql.Date(calendar.getTime().getTime());
                        java.sql.Time time_added = new java.sql.Time(calendar.getTime().getTime());
                        PreparedStatement preparedStmt;
                        try {
                            String query = "insert into gotradio_db.copy_history (date_added, time_added, file_name, programmer, status)"
                                + " values (?, ?, ?, ?, ?)";
                            preparedStmt = databaseConnect.getSQLConnection().prepareStatement(query);
                            preparedStmt.setDate(1, date_added);
                            preparedStmt.setTime(2, time_added);
                            preparedStmt.setString(3, s);
                            preparedStmt.setString(4, databaseConnect.getUser());
                            preparedStmt.setString(5, copyStatus);
                            preparedStmt.execute();
                        } catch (Exception e){
                            System.out.println(e.getMessage());
                        }
                        
                    } else {
                        break;
                    }
                }
                
                Platform.runLater(()->{
                    cancelCopy.setDisable(true);
                    clearQueue.setDisable(false);
                    setCopierLabelStatus(CopierLabelEnum.Completed);
                });
                
                copying = false;
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
