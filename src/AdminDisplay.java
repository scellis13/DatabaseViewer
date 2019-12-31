/*
    Last Updated: 12/21/2019
    Updated On: Sean's Computer
*/
package grdb;

import static grdb.GRDB.databaseConnect;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.Duration;
import java.util.*;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class AdminDisplay {
    private BorderPane mainPane;
    private StackPane stackPane;
    private ArrayList<User> userList;
    //Top Pane Variables
    private HBox topPane;
    private static final Image CREATEUSER_BUTTON_IMG_NORMAL = new Image(GRDB.class.getResourceAsStream("/resources/pics/createUserButton_normal.png"));
    private static final Image CREATEUSER_BUTTON_IMG_HIGHLIGHT = new Image(GRDB.class.getResourceAsStream("/resources/pics/createUserButton_highlight2.png"));
    private static final Image CREATEUSER_BUTTON_IMG_PRESSED = new Image(GRDB.class.getResourceAsStream("/resources/pics/createUserButton_pressed.png"));
    private Button createUserButton, closeAdminViewButton;
    private static final Image CLOSE_BUTTON_IMG_NORMAL = new Image(GRDB.class.getResourceAsStream("/resources/pics/closeButton_normal_admin.png"));
    private static final Image CLOSE_BUTTON_IMG_HIGHLIGHT = new Image(GRDB.class.getResourceAsStream("/resources/pics/closeButton_highlight.png"));
    private static final Image CLOSE_BUTTON_IMG_PRESSED = new Image(GRDB.class.getResourceAsStream("/resources/pics/closeButton_pressed.png"));
    private static final Image CLEAR_BUTTON_IMG = new Image(GRDB.class.getResourceAsStream("/resources/pics/adminClearButton_normal.png"));
    private static final Image CLEAR_BUTTON_IMG_PRESSED = new Image(GRDB.class.getResourceAsStream("/resources/pics/adminClearButton_pressed.png"));
    private static final Image CLEAR_BUTTON_IMG_HIGHLIGHT = new Image(GRDB.class.getResourceAsStream("/resources/pics/adminClearButton_highlight.png"));
    private static final Image SUBMIT_CHANGE_BUTTON_IMG = new Image(GRDB.class.getResourceAsStream("/resources/pics/submitChangeButton_normal.png"));
    private static final Image SUBMIT_CHANGE_BUTTON_IMG_PRESSED = new Image(GRDB.class.getResourceAsStream("/resources/pics/submitChangeButton_pressed.png"));
    private static final Image SUBMIT_CHANGE_BUTTON_IMG_HIGHLIGHT = new Image(GRDB.class.getResourceAsStream("/resources/pics/submitChangeButton_highlight.png"));
    
    //Center Pane Variables
    private VBox centerPane;
    //Create User Pane Variables
    private VBox createUserBox;
    private Button closeCreateUserPaneButton, createNewUserButton, clearFieldsButton;
    private ArrayList<TextField> textFields = new ArrayList<>();
    private ArrayList<PasswordField> passwordFields = new ArrayList<>();
    private CheckBox adminCheckBox;
    private TextField username_TextField, firstName_TextField, lastName_TextField, email_TextField;
    private PasswordField password_PasswordField, password_PasswordField2;
    private Label createUserStatusLabel = new Label("");
    
    public AdminDisplay(){
        stackPane = new StackPane();
        mainPane = new BorderPane(); 
        userList = new ArrayList<>();
    }
    
    public StackPane getPane(){
        return this.stackPane;
    }
    
    public BorderPane getMainBorderPane(){
        return this.mainPane;
    }
    
    public void setContent(){
        createTopPane();
        createCenterPane();
        createUserPane();
        
        mainPane.setTop(topPane);
        mainPane.setCenter(centerPane);
        
        stackPane.getChildren().add(mainPane);
    }
    
    public void setUserList(ArrayList<User> userList){
        this.userList = userList;
    }
    
    private void createTopPane(){
        topPane = new HBox();
        topPane.setPrefSize(mainPane.getMaxWidth()*.95, mainPane.getMaxHeight()*.1);
        topPane.setPadding(new Insets(0, 100, 10, 100));
        topPane.setAlignment(Pos.BOTTOM_LEFT);
        topPane.setStyle("-fx-background-color: white; -fx-font-size: 18px; -fx-font-family: Times New Roman;");
        topPane.setSpacing(10);
            
            //Create createUserButton
            createUserButton = new Button();
            createUserButton.setMinSize(30,30);
            createUserButton.setMaxSize(30,30);
            
                //Normal View of Refresh Button
                BackgroundImage backgroundImageNormal = new BackgroundImage(
                    CREATEUSER_BUTTON_IMG_NORMAL, 
                    BackgroundRepeat.NO_REPEAT, 
                    BackgroundRepeat.NO_REPEAT, 
                    BackgroundPosition.DEFAULT, 
                    BackgroundSize.DEFAULT);
                Background backgroundNormal = new Background(backgroundImageNormal);
                
                //Pressed View of Refresh Button
                BackgroundImage backgroundImageHighlight = new BackgroundImage(
                    CREATEUSER_BUTTON_IMG_HIGHLIGHT, 
                    BackgroundRepeat.NO_REPEAT, 
                    BackgroundRepeat.NO_REPEAT, 
                    BackgroundPosition.DEFAULT, 
                    BackgroundSize.DEFAULT);
                Background backgroundHighlight = new Background(backgroundImageHighlight);
                
                //Pressed View of Refresh Button
                BackgroundImage backgroundImagePressed = new BackgroundImage(
                    CREATEUSER_BUTTON_IMG_PRESSED, 
                    BackgroundRepeat.NO_REPEAT, 
                    BackgroundRepeat.NO_REPEAT, 
                    BackgroundPosition.DEFAULT, 
                    BackgroundSize.DEFAULT);
                Background backgroundPressed = new Background(backgroundImagePressed);
                
            //Set Actions to change Button Background    
            createUserButton.setBackground(backgroundNormal);
            createUserButton.setOnMousePressed(event -> {
                createUserButton.setBackground(backgroundPressed);
            });
            createUserButton.setOnMouseReleased(event -> {
                createUserButton.setBackground(backgroundNormal);
            });
            createUserButton.setOnMouseEntered(event ->{
                createUserButton.setBackground(backgroundHighlight);
            });
            createUserButton.setOnMouseExited(event ->{
                createUserButton.setBackground(backgroundNormal);
            });
            //End of Creating createUserButton
            
            //Create Label
            Label createUserLabel = new Label("Create New User");
            createUserLabel.setAlignment(Pos.CENTER);
            
            HBox closeAdminViewButtonBox = new HBox();
            closeAdminViewButtonBox.setMinSize(mainPane.getMaxWidth()*.6, mainPane.getMaxHeight()*.1);
            closeAdminViewButtonBox.setAlignment(Pos.CENTER_RIGHT);
                //Add CLose AdminView Button
                closeAdminViewButton = new Button();
                closeAdminViewButton.setMinSize(30,30);
                closeAdminViewButton.setMaxSize(30,30);
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

                closeAdminViewButton.setBackground(backgroundCloseNormal);
                closeAdminViewButton.setOnMousePressed(event -> {
                    closeAdminViewButton.setBackground(backgroundClosePressed);
                });
                closeAdminViewButton.setOnMouseReleased(event -> {
                    closeAdminViewButton.setBackground(backgroundCloseNormal);
                });
                closeAdminViewButton.setOnMouseEntered(event ->{
                    closeAdminViewButton.setBackground(backgroundCloseHighlight);
                });
                closeAdminViewButton.setOnMouseExited(event ->{
                    closeAdminViewButton.setBackground(backgroundCloseNormal);
                });
            
            closeAdminViewButtonBox.getChildren().addAll(closeAdminViewButton);
            
        topPane.getChildren().addAll(createUserButton, createUserLabel, closeAdminViewButtonBox);
    }
    
    private void createCenterPane(){
        centerPane = new VBox();
        centerPane.setPrefSize(mainPane.getMaxWidth()*.8, mainPane.getMaxHeight()*.9);
        centerPane.setPadding(new Insets(25, 100, 25, 100));
        centerPane.setAlignment(Pos.TOP_CENTER);
        centerPane.setStyle("-fx-background-color: gainsboro; -fx-font-size: 18px; -fx-font-family: Times New Roman;");
        centerPane.setSpacing(25);
        createUserBoxes();
    }
    
    private void createUserBoxes(){
        HBox labelBox = new HBox();
        labelBox.setAlignment(Pos.CENTER);
        labelBox.setSpacing(15);
        labelBox.setMinWidth(centerPane.getPrefWidth()*.75);
        final double ELEMENT_WIDTH = labelBox.getMinWidth()*.2;
        labelBox.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-align: CENTER;"
                + "-fx-border-width: 0 0 1 0; -fx-border-color: black;");

        //Remove Button Label - 30 width
        Label removeLabel = new Label();
        removeLabel.setMinWidth(30);
        removeLabel.setAlignment(Pos.CENTER);
        //Username Label - - ELEMENT_WIDTH
        Label userLabel = new Label("Username");
        userLabel.setMinWidth(ELEMENT_WIDTH);
        userLabel.setStyle("-fx-border-color: black;");
        userLabel.setAlignment(Pos.CENTER);
        //Change Password Label - ELEMENT_WIDTH
        Label changePWLabel = new Label("Change Password");
        changePWLabel.setStyle("-fx-border-color: black;");
        changePWLabel.setAlignment(Pos.CENTER);
        changePWLabel.setMinWidth(ELEMENT_WIDTH);
        //Change password Button - 30 width
        Label changePWButtonLabel = new Label();
        changePWButtonLabel.setMinWidth(30);
        changePWButtonLabel.setAlignment(Pos.CENTER);
        //Admin Status Label - ELEMENT_WIDTH
        Label adminLabel = new Label("Access Level");
        adminLabel.setStyle("-fx-border-color: black;");
        adminLabel.setAlignment(Pos.CENTER);
        adminLabel.setMinWidth(ELEMENT_WIDTH);
        //Last Logged In Label - ELEMENT_WIDTH
        Label lastLoggedLabel = new Label("Last Logged In");
        lastLoggedLabel.setStyle("-fx-border-color: black;");
        lastLoggedLabel.setAlignment(Pos.CENTER);
        lastLoggedLabel.setMinWidth(ELEMENT_WIDTH);
        //Duration Label - ELEMENT_WIDTH
        Label durationLabel = new Label("Total Time");
        durationLabel.setStyle("-fx-border-color: black;");
        durationLabel.setAlignment(Pos.CENTER);
        durationLabel.setMinWidth(ELEMENT_WIDTH);
        //Total Searches Label - ELEMENT_WIDTH
        Label searchLabel = new Label("Total Searches");
        searchLabel.setStyle("-fx-border-color: black;");
        searchLabel.setAlignment(Pos.CENTER);
        searchLabel.setMinWidth(ELEMENT_WIDTH*.75);
        //Total Copies Label - ELEMENT_WIDTH
        Label copyLabel = new Label("Total Copies");
        copyLabel.setStyle("-fx-border-color: black;");
        copyLabel.setAlignment(Pos.CENTER);
        copyLabel.setMinWidth(ELEMENT_WIDTH*.75);
        
        labelBox.getChildren().addAll(removeLabel,userLabel,changePWLabel,
                changePWButtonLabel,adminLabel,lastLoggedLabel,
                durationLabel,searchLabel,copyLabel);
        centerPane.getChildren().add(labelBox);
        
        for(User u : userList){
            System.out.println("Adding User to CenterPane: " + u.toString());
            
            HBox userBox = new HBox();
            userBox.setAlignment(Pos.CENTER);
            userBox.setSpacing(15);
            userBox.setMinWidth(centerPane.getPrefWidth()*.75);
            
                Button removeUserButton = new Button();
                removeUserButton.setMinSize(30,30);
                removeUserButton.setMaxSize(30,30);
                    BackgroundImage backgroundImageCloseNormal = new BackgroundImage(
                        CLEAR_BUTTON_IMG,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundPosition.DEFAULT,
                        BackgroundSize.DEFAULT);
                    Background backgroundCloseNormal = new Background(backgroundImageCloseNormal);

                    BackgroundImage backgroundImageCloseHighlight = new BackgroundImage(
                        CLEAR_BUTTON_IMG_HIGHLIGHT,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundPosition.DEFAULT,
                        BackgroundSize.DEFAULT);
                    Background backgroundCloseHighlight = new Background(backgroundImageCloseHighlight);

                    BackgroundImage backgroundImageClosePressed = new BackgroundImage(
                        CLEAR_BUTTON_IMG_PRESSED,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundPosition.DEFAULT,
                        BackgroundSize.DEFAULT);
                    Background backgroundClosePressed = new Background(backgroundImageClosePressed);

                removeUserButton.setBackground(backgroundCloseNormal);
                removeUserButton.setOnMousePressed(event -> {
                    removeUserButton.setBackground(backgroundClosePressed);
                });
                removeUserButton.setOnMouseReleased(event -> {
                    removeUserButton.setBackground(backgroundCloseNormal);
                });
                removeUserButton.setOnMouseEntered(event ->{
                    removeUserButton.setBackground(backgroundCloseHighlight);
                });
                removeUserButton.setOnMouseExited(event ->{
                    removeUserButton.setBackground(backgroundCloseNormal);
                });
                removeUserButton.setOnAction(e->{
                    GRDB.checkDatabaseConnection();
                    
                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setTitle("Confirm User Deletion.");
                    dialog.setHeaderText("Please Confirm deleting User '" + 
                            u.getUsername() + "' by Entering their username below.");
                    dialog.setGraphic(null);
                    dialog.setContentText("Username");
                    try{
                    Optional<String> result = dialog.showAndWait();
                    System.out.println(result.get() + " = " + u.getUsername());
                        if(result.isPresent() && result.get().equals(u.getUsername())){
                            PreparedStatement preparedStmt;
                            try {
                                String programmerDeletionQuery = "delete from gotradio_db.programmers" +
                                    " where username = \"" + u.getUsername() + "\";";
                                String userDropQuery = "drop user '" + u.getUsername() + "'@'%';";

                                preparedStmt = GRDB.databaseConnect.getSQLConnection().prepareStatement(programmerDeletionQuery);
                                preparedStmt.execute();

                                preparedStmt = GRDB.databaseConnect.getSQLConnection().prepareStatement(userDropQuery);
                                preparedStmt.execute();

                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Successfully Dropped User.");
                                alert.setGraphic(null);
                                alert.setHeaderText(null);
                                alert.setContentText(u.getUsername() + " was successfully dropped and removed from GotRadio Database Access.");
                                alert.show();
                                userList.remove(u);
                                refreshCenterPane();
                                
                                try{
                                    preparedStmt = GRDB.databaseConnect.getSQLConnection().prepareStatement("flush privileges;");
                                    preparedStmt.execute();
                                } catch (SQLException sql){System.out.println(sql.getMessage());}
                            } catch (SQLException dropUserException){
                                System.out.println(dropUserException.getMessage());
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Error Dropping User.");
                                alert.setGraphic(null);
                                alert.setHeaderText(null);
                                alert.setContentText(dropUserException.getMessage());
                                alert.show();
                            }

                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Error Dropping User.");
                                alert.setGraphic(null);
                                alert.setHeaderText(null);
                                alert.setContentText("Unable to drop user due to invalid confirmation. Please try again.");
                                alert.show();
                        }
                    } catch (NoSuchElementException dialogException){}
                });
            
                Label usernameLabel = new Label(u.getUsername());
                usernameLabel.setMinWidth(ELEMENT_WIDTH);
                usernameLabel.setStyle("-fx-font-weight: bold;");
                usernameLabel.setAlignment(Pos.CENTER);
                
                PasswordField changePasswordField = new PasswordField();
                changePasswordField.setMinWidth(ELEMENT_WIDTH);

                Button changePasswordButton = new Button();
                changePasswordButton.setMinSize(30,30);
                changePasswordButton.setMaxSize(30,30);
                    BackgroundImage backgroundImageChangePasswordNormal = new BackgroundImage(
                        SUBMIT_CHANGE_BUTTON_IMG,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundPosition.DEFAULT,
                        BackgroundSize.DEFAULT);
                    Background backgroundChangePasswordNormal = new Background(backgroundImageChangePasswordNormal);

                    BackgroundImage backgroundImageChangePasswordHighlight = new BackgroundImage(
                        SUBMIT_CHANGE_BUTTON_IMG_HIGHLIGHT,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundPosition.DEFAULT,
                        BackgroundSize.DEFAULT);
                    Background backgroundChangePasswordHighlight = new Background(backgroundImageChangePasswordHighlight);

                    BackgroundImage backgroundImageChangePasswordPressed = new BackgroundImage(
                        SUBMIT_CHANGE_BUTTON_IMG_PRESSED,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundPosition.DEFAULT,
                        BackgroundSize.DEFAULT);
                    Background backgroundChangePasswordPressed = new Background(backgroundImageChangePasswordPressed);

                changePasswordButton.setBackground(backgroundChangePasswordNormal);
                changePasswordButton.setOnMousePressed(event -> {
                    changePasswordButton.setBackground(backgroundChangePasswordPressed);
                });
                changePasswordButton.setOnMouseReleased(event -> {
                    changePasswordButton.setBackground(backgroundChangePasswordNormal);
                });
                changePasswordButton.setOnMouseEntered(event ->{
                    changePasswordButton.setBackground(backgroundChangePasswordHighlight);
                });
                changePasswordButton.setOnMouseExited(event ->{
                    changePasswordButton.setBackground(backgroundChangePasswordNormal);
                });
                changePasswordButton.setOnAction(e->{
                    GRDB.checkDatabaseConnection();
                    if(changePasswordField.getText().trim().length()!=0){
                        TextInputDialog dialog = new TextInputDialog();
                        dialog.setTitle("Confirm Password Change.");
                        dialog.setHeaderText("Please Confirm changing password for User '" + 
                                u.getUsername() + "' by re-entering their new password.");
                        dialog.setGraphic(null);
                        dialog.setContentText("Confirm Password");
                        try{
                        Optional<String> result = dialog.showAndWait();

                            if(result.isPresent() && result.get().equals(changePasswordField.getText())){
                                PreparedStatement preparedStmt;
                                try {
                                    String changePasswordQuery = "ALTER USER '" + u.getUsername() + "'@'%' IDENTIFIED BY '" + changePasswordField.getText() + "';";

                                    preparedStmt = GRDB.databaseConnect.getSQLConnection().prepareStatement(changePasswordQuery);
                                    preparedStmt.execute();

                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("Successfully Changed Password.");
                                    alert.setGraphic(null);
                                    alert.setHeaderText(null);
                                    alert.setContentText("Successfully changed the password for '" + u.getUsername() + "'.");
                                    alert.show();

                                    try{
                                        preparedStmt = GRDB.databaseConnect.getSQLConnection().prepareStatement("flush privileges;");
                                        preparedStmt.execute();
                                    } catch (SQLException sql){System.out.println(sql.getMessage());}
                                    
                                } catch (SQLException sqlException){System.out.println(sqlException.getMessage());}
                            } else {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("Password Confirmation Mismatch.");
                                    alert.setGraphic(null);
                                    alert.setHeaderText(null);
                                    alert.setContentText("Failed to change the password for '" + u.getUsername() + "'. Please try again.");
                                    alert.show();
                            }
                        } catch (NoSuchElementException dialogException){}
                        
                        changePasswordField.clear();
                        
                    } else {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Change Password Denied.");
                            alert.setGraphic(null);
                            alert.setHeaderText(null);
                            alert.setContentText("Please Enter a valid password in the Password Field before attempting change.");
                            alert.show();
                    }
                });
                
                Label adminStatusLabel = new Label();
                adminStatusLabel.setMinWidth(ELEMENT_WIDTH);
                adminStatusLabel.setAlignment(Pos.CENTER);
                if(u.getAdmin()==0){
                    adminStatusLabel.setText("Programmer");
                } else {
                    adminStatusLabel.setText("Administrator");
                    adminStatusLabel.setStyle("-fx-font-weight: bold; -fx-font-color: red;");
                }
                
                //Check Database Connection before Label Queries
                GRDB.checkDatabaseConnection();
                ResultSet result;
                
                //Login Max Query
                Label lastLoggedInLabel = new Label();
                lastLoggedInLabel.setMinWidth(ELEMENT_WIDTH);
                lastLoggedInLabel.setAlignment(Pos.CENTER);
                try {
                    String lastLoggedInQuery = "SELECT login_date, max(login_time) as login_time from gotradio_db.login_history where (username = \"" + u.getUsername() + "\") AND (login_date = (SELECT max(login_date) from gotradio_db.login_history where (username = \"" + u.getUsername() + "\")));";
                    result = GRDB.databaseConnect.getSQLConnection().prepareStatement(lastLoggedInQuery).executeQuery();

                    while(result.next()){
                        String date = result.getString("login_date");
                        String time = result.getString("login_time");
                        if(date==null || time==null){
                            lastLoggedInLabel.setText("No Login History");
                            lastLoggedInLabel.setTextFill(Color.RED);
                        } else {
                            lastLoggedInLabel.setText(date + " " + time);
                        }
                    }
                } catch(SQLException e){
                    System.out.println(e.getMessage());
                    lastLoggedInLabel.setText("N/A");
                }
                
                //Duration Query
                Label durationLoggedInLabel = new Label();
                durationLoggedInLabel.setMinWidth(ELEMENT_WIDTH);
                durationLoggedInLabel.setAlignment(Pos.CENTER);
                
                try {
                    String durationQuery = "select time_to_sec(duration) as secs from gotradio_db.login_history where username = \"" + u.getUsername() + "\";";
                    result = GRDB.databaseConnect.getSQLConnection().prepareStatement(durationQuery).executeQuery();
                    long durationSum = 0;
                    while(result.next()){
                        durationSum += result.getInt("secs");
                    }
                    
                    Duration duration = Duration.ofSeconds(durationSum);
                    
                    durationLoggedInLabel.setText((durationSum/3600) + ":" + ((durationSum%3600)/60) + ":" + (durationSum%60));
                } catch (SQLException e){
                    System.out.println(e.getMessage());
                    durationLoggedInLabel.setText("N/A");
                }
                
                
                //Search and Copy Count Query
                Label totalSearchCountLabel = new Label();
                totalSearchCountLabel.setMinWidth(ELEMENT_WIDTH*.75);
                totalSearchCountLabel.setAlignment(Pos.CENTER);
                
                Label totalCopyCountLabel = new Label();
                totalCopyCountLabel.setMinWidth(ELEMENT_WIDTH*.75);
                totalCopyCountLabel.setAlignment(Pos.CENTER);
                
                try {
                    String countQuery = "select SUM(searches) as total_searches, SUM(copies) as total_copies from gotradio_db.login_history where username = \"" + u.getUsername() + "\";";
                    result = GRDB.databaseConnect.getSQLConnection().prepareStatement(countQuery).executeQuery();
                    while(result.next()){
                        int totalSearches = result.getInt("total_searches");
                        int totalCopies = result.getInt("total_copies");
                        totalSearchCountLabel.setText(totalSearches + "");
                        totalCopyCountLabel.setText(totalCopies + "");
                    }
                } catch(SQLException e){
                    System.out.println(e.getMessage());
                    totalSearchCountLabel.setText("N/A");
                    totalCopyCountLabel.setText("N/A");
                }
                //End of Database Queries
                
                
            userBox.getChildren().addAll(
                    removeUserButton, usernameLabel, changePasswordField, 
                    changePasswordButton, adminStatusLabel, lastLoggedInLabel,
                    durationLoggedInLabel, totalSearchCountLabel, totalCopyCountLabel);    
                
            centerPane.getChildren().add(userBox);
        }
    }
    
    public void refreshCenterPane(){
        createCenterPane();
        mainPane.setCenter(centerPane);
        
    }
    
    private void createUserPane(){
        final int BOX_HEIGHT = 30;
        
        createUserBox = new VBox();
        createUserBox.setMinSize(500, 450);//Heights per box 75, Spacing/Top-Bottom Padding 15
        createUserBox.setMaxSize(mainPane.getMaxWidth()*.95, mainPane.getMaxHeight()*.75);//Labels 160, Spacing 10, Textfields 300, 15 Left Pad, 15 Right pad
        createUserBox.setPadding(new Insets(15, 15, 15, 15));
        createUserBox.setStyle("-fx-font-family: Times New Roman; -fx-font-size: 16px;");
        createUserBox.setAlignment(Pos.CENTER);
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.LIGHTCYAN);
        dropShadow.setRadius(5.0);
        dropShadow.setOffsetX(3.0);
        dropShadow.setOffsetY(3.0);
        createUserBox.setEffect(dropShadow);
        createUserBox.setSpacing(15);
            //Close Pane Box
            HBox closeCreateUserPaneBox = new HBox();
            closeCreateUserPaneBox.setMinSize(470, BOX_HEIGHT);
            closeCreateUserPaneBox.setMaxSize(800, BOX_HEIGHT);
            closeCreateUserPaneBox.setAlignment(Pos.CENTER_RIGHT);
            closeCreateUserPaneBox.setSpacing(10);
                //Add CLose AdminView Button
                closeCreateUserPaneButton = new Button();
                closeCreateUserPaneButton.setMinSize(30,30);
                closeCreateUserPaneButton.setMaxSize(30,30);
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

                closeCreateUserPaneButton.setBackground(backgroundCloseNormal);
                closeCreateUserPaneButton.setOnMousePressed(event -> {
                    closeCreateUserPaneButton.setBackground(backgroundClosePressed);
                });
                closeCreateUserPaneButton.setOnMouseReleased(event -> {
                    closeCreateUserPaneButton.setBackground(backgroundCloseNormal);
                });
                closeCreateUserPaneButton.setOnMouseEntered(event ->{
                    closeCreateUserPaneButton.setBackground(backgroundCloseHighlight);
                });
                closeCreateUserPaneButton.setOnMouseExited(event ->{
                    closeCreateUserPaneButton.setBackground(backgroundCloseNormal);
                });
            
            closeCreateUserPaneBox.getChildren().addAll(closeCreateUserPaneButton);
            //First Name Box
            HBox firstNameBox = new HBox();
            firstNameBox.setMinSize(470, BOX_HEIGHT);
            firstNameBox.setMaxSize(470, BOX_HEIGHT); 
            firstNameBox.setAlignment(Pos.CENTER_LEFT);
            firstNameBox.setSpacing(10);
                Label firstNameLabel = new Label("First Name:");
                    firstNameLabel.setMinSize(160, BOX_HEIGHT);
                    firstNameLabel.setMaxSize(160, BOX_HEIGHT);
                    firstNameLabel.setAlignment(Pos.CENTER_RIGHT);
                firstName_TextField = new TextField();
                    firstName_TextField.setMinSize(300,BOX_HEIGHT);
                    firstName_TextField.setMaxSize(300,BOX_HEIGHT);
            firstNameBox.getChildren().addAll(firstNameLabel, firstName_TextField);
            //Last Name Box
            HBox lastNameBox = new HBox();
            lastNameBox.setMinSize(470, BOX_HEIGHT);
            lastNameBox.setMaxSize(470, BOX_HEIGHT);  
            lastNameBox.setAlignment(Pos.CENTER_LEFT);
            lastNameBox.setSpacing(10);
                Label lastName_Label = new Label("Last Name:");
                    lastName_Label.setMinSize(160, BOX_HEIGHT);
                    lastName_Label.setMaxSize(160, BOX_HEIGHT);
                    lastName_Label.setAlignment(Pos.CENTER_RIGHT);
                lastName_TextField = new TextField();
                    lastName_TextField.setMinSize(300,BOX_HEIGHT);
                    lastName_TextField.setMaxSize(300,BOX_HEIGHT);
            lastNameBox.getChildren().addAll(lastName_Label, lastName_TextField);
            //Email Box
            HBox emailBox = new HBox();
            emailBox.setMinSize(470, BOX_HEIGHT);
            emailBox.setMaxSize(470, BOX_HEIGHT);   
            emailBox.setAlignment(Pos.CENTER_LEFT);
            emailBox.setSpacing(10);
                Label email_Label = new Label("Email:");
                    email_Label.setMinSize(160, BOX_HEIGHT);
                    email_Label.setMaxSize(160, BOX_HEIGHT);
                    email_Label.setAlignment(Pos.CENTER_RIGHT);
                email_TextField = new TextField();
                    email_TextField.setMinSize(300,BOX_HEIGHT);
                    email_TextField.setMaxSize(300,BOX_HEIGHT);
            emailBox.getChildren().addAll(email_Label, email_TextField);
            //Username Box
            HBox usernameBox = new HBox();
            usernameBox.setMinSize(470, BOX_HEIGHT);
            usernameBox.setMaxSize(470, BOX_HEIGHT); 
            usernameBox.setAlignment(Pos.CENTER_LEFT);
            usernameBox.setSpacing(10);
                Label username_Label = new Label("Username:");
                username_Label.setTextFill(Color.RED);
                    username_Label.setMinSize(160, BOX_HEIGHT);
                    username_Label.setMaxSize(160, BOX_HEIGHT);
                    username_Label.setAlignment(Pos.CENTER_RIGHT);
                username_TextField = new TextField();
                    username_TextField.setMinSize(300,BOX_HEIGHT);
                    username_TextField.setMaxSize(300,BOX_HEIGHT);
            usernameBox.getChildren().addAll(username_Label, username_TextField);
            //Password Box
            HBox passwordBox = new HBox();
            passwordBox.setMinSize(470, BOX_HEIGHT);
            passwordBox.setMaxSize(470, BOX_HEIGHT); 
            passwordBox.setAlignment(Pos.CENTER_LEFT);
            passwordBox.setSpacing(10);
                Label password_Label = new Label("Password:");
                password_Label.setTextFill(Color.RED);
                    password_Label.setMinSize(160, BOX_HEIGHT);
                    password_Label.setMaxSize(160, BOX_HEIGHT);
                    password_Label.setAlignment(Pos.CENTER_RIGHT);
                password_PasswordField = new PasswordField();
                    password_PasswordField.setMinSize(300,BOX_HEIGHT);
                    password_PasswordField.setMaxSize(300,BOX_HEIGHT);
            passwordBox.getChildren().addAll(password_Label, password_PasswordField);
            
            //Re-enter Password Box
            HBox passwordBox2 = new HBox();
            passwordBox2.setMinSize(470, BOX_HEIGHT);
            passwordBox2.setMaxSize(470, BOX_HEIGHT); 
            passwordBox2.setAlignment(Pos.CENTER_LEFT);
            passwordBox2.setSpacing(10);
                Label password_Label2 = new Label("Re-enter Password:");
                password_Label2.setTextFill(Color.RED);
                    password_Label2.setMinSize(160, BOX_HEIGHT);
                    password_Label2.setMaxSize(160, BOX_HEIGHT);
                    password_Label2.setAlignment(Pos.CENTER_RIGHT);
                password_PasswordField2 = new PasswordField();
                    password_PasswordField2.setMinSize(300,BOX_HEIGHT);
                    password_PasswordField2.setMaxSize(300,BOX_HEIGHT);
            passwordBox2.getChildren().addAll(password_Label2, password_PasswordField2);
            //Admin Access Box
            HBox adminAccessBox = new HBox();
            adminAccessBox.setMinSize(470, BOX_HEIGHT);
            adminAccessBox.setMaxSize(470, BOX_HEIGHT);   
            adminAccessBox.setAlignment(Pos.CENTER_LEFT);
            adminAccessBox.setSpacing(10);
                Label adminAccess_Label = new Label("Admin Access?");
                adminAccess_Label.setTextFill(Color.RED);
                    adminAccess_Label.setMinSize(160, BOX_HEIGHT);
                    adminAccess_Label.setMaxSize(160, BOX_HEIGHT);
                    adminAccess_Label.setAlignment(Pos.CENTER_RIGHT);
                    
                adminCheckBox = new CheckBox();
                
            adminAccessBox.getChildren().addAll(adminAccess_Label, adminCheckBox);
            //Create User/Clear Info Box
            HBox bottomButtonBox = new HBox();
            bottomButtonBox.setMinSize(470, BOX_HEIGHT);
            bottomButtonBox.setMaxSize(800, BOX_HEIGHT);  
            bottomButtonBox.setAlignment(Pos.CENTER_RIGHT);
            bottomButtonBox.setSpacing(20);
                clearFieldsButton = new Button("Clear All Fields");
                    clearFieldsButton.setMinHeight(BOX_HEIGHT);
                    clearFieldsButton.setMaxHeight(BOX_HEIGHT);
                createNewUserButton = new Button("Create New User");
                    createNewUserButton.setMinHeight(BOX_HEIGHT);
                    createNewUserButton.setMaxHeight(BOX_HEIGHT);
            bottomButtonBox.getChildren().addAll(createUserStatusLabel, clearFieldsButton, createNewUserButton);
            
            textFields.add(firstName_TextField);
            textFields.add(lastName_TextField);
            textFields.add(email_TextField);
            textFields.add(username_TextField);
            passwordFields.add(password_PasswordField);
            passwordFields.add(password_PasswordField2);
            
        createUserBox.getChildren().addAll(closeCreateUserPaneBox, firstNameBox, lastNameBox, emailBox, usernameBox, passwordBox, passwordBox2, adminAccessBox, bottomButtonBox);
    }
    
    public void addUserToList(User user){
        userList.add(user);
    }
    
    public ArrayList<User> getUserList(){
        return this.userList;
    }
    
    public Button getClearFieldsButton(){
        return this.clearFieldsButton;
    }
    
    public void clearCreateUserFields(){
        for(TextField t : textFields){
            t.clear();
        }
        
        for(PasswordField p : passwordFields){
            p.clear();
        }
    }
    
    public Button getCloseAdminButton(){
        return this.closeAdminViewButton;
    }
    
    public Button getCreateUserButton(){
        return this.createUserButton; //Returns button to show CreateUserPane
    }
    
    public Button getCreateNewUserButton(){
        return this.createNewUserButton; //Returns button that creates new user
    }
    
    public Button getCloseCreateUserPaneButton(){
        return this.closeCreateUserPaneButton;
    }
    
    public void showCreateUserPane(){
        if(!stackPane.getChildren().contains(createUserBox)){
            centerPane.setEffect(new GaussianBlur());
            stackPane.getChildren().add(createUserBox);
        }
    }
    
    public void hideCreateUserPane(){
        if(stackPane.getChildren().contains(createUserBox)){
            centerPane.setEffect(null);
            stackPane.getChildren().remove(createUserBox);
        }
        
    }
    
    public CheckBox getAdminCheckBox(){
        return this.adminCheckBox;
    }
    
    public void setCreateUserStatusLabel(String s){
        this.createUserStatusLabel.setText(s);
    }
    
    public User createUser(){
        User newUser = null;
        //Check Fields Appropriate
        if(         username_TextField.getText().trim().length()!=0
                && password_PasswordField.getText().trim().length()!=0 
                && password_PasswordField2.getText().trim().length()!=0
          ){
            //Check if Admin is Selected
            if(adminCheckBox.isSelected()){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Administrator Confirmation");
                alert.setContentText("Are you sure you want to make '" + username_TextField.getText() + "' an Administrator?");
                alert.getDialogPane().setHeader(null);
                alert.getButtonTypes().remove(ButtonType.OK);
                alert.getButtonTypes().add(ButtonType.YES);
                Optional<ButtonType> result = alert.showAndWait();
                if(!result.get().equals(ButtonType.YES)){
                    return null;
                } 
            }//End of Admin Check
            //Create User Object
            int adminStatus = 0;
            if(adminCheckBox.isSelected()){
                adminStatus = 1;
            }
            
            //Check if passwords match
            if(!password_PasswordField.getText().equals(password_PasswordField2.getText())){
                createUserStatusLabel.setText("Password Mismatch.");
                createUserStatusLabel.setTextFill(Color.RED);
                password_PasswordField.clear();
                password_PasswordField2.clear();
                return null;
            }
            
            for(User u : userList){
                if(u.getUsername().equalsIgnoreCase(username_TextField.getText())){
                    createUserStatusLabel.setText("User already exists.");
                    createUserStatusLabel.setTextFill(Color.RED);
                    password_PasswordField.clear();
                    password_PasswordField2.clear();
                    return null;
                }
            }
            
            newUser = new User(firstName_TextField.getText(), lastName_TextField.getText(), email_TextField.getText(),
                username_TextField.getText(), adminStatus);
            newUser.setPassword(password_PasswordField.getText());
            
            createUserStatusLabel.setText("'" + username_TextField.getText() + "' created.");
            createUserStatusLabel.setTextFill(Color.GREEN);
        } else {
            createUserStatusLabel.setText("Please Enter All 'Red' Fields.");
            createUserStatusLabel.setTextFill(Color.RED);
            password_PasswordField.clear();
            password_PasswordField2.clear();
        }
            
        return newUser;
    }
    
    public User convertUser(String firstName, String lastName, String email, String username, int admin){
        User newUser = new User(firstName, lastName, email, username, admin);
        return newUser;
    }
    
    public class User{
        private final String firstName, lastName, email, username;
        private String password;
        private final int admin;
        
        public User(String firstName, String lastName, String email, String username, int admin){
            this.username = username;
            this.admin = admin;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
        }
        
        public String getFirstName(){return this.firstName;}
        public String getLastName(){return this.lastName;}
        public String getEmail(){return this.email;}
        public String getUsername(){return this.username;}
        public int getAdmin(){return this.admin;}
        public String getPassword(){
            return this.password;
        }
        
        public void setPassword(String password){
            this.password = password;
        }
        
        @Override
        public String toString(){
            return username + ": " + firstName + " " + lastName + ", " + email + " (" + admin + ")";
        }
    }
}
