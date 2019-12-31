/*
    Last Updated: 12/26/2019
    Updated On: Sean's Computer
    Sheets Updated: DatabaseTableLoader.java
*/
package grdb;
//Controller
import java.sql.*;
import java.time.*;
import java.util.*;
import javafx.application.*;
import javafx.collections.*;
import javafx.concurrent.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.stage.*;

public class GRDB extends Application {
    private static final Image APPLICATION_ICON = new Image(GRDB.class.getResourceAsStream("/resources/pics/gotradio_icon.jpg"));
    private final String VERSION_NUMBER = "2.1";
    private Scene scene;
    private double FULLSCREEN_WIDTH, FULLSCREEN_HEIGHT;
    private MainBorderDisplay mainBorderDisplay;
    private MenuBarDisplay menuBarDisplay;
    private SelectionDisplay selectionDisplay;
    private DatabaseDisplay databaseDisplay;
    private LoginDisplay loginDisplay;
    public static DatabaseConnect databaseConnect;
    private DatabaseTableLoader databaseTableLoader;
    private SongCopier songCopier;
    private DatabaseContextMenu dbContextMenu;
    private AdminDisplay adminDisplay;
    
    //Temp variables
    private LocalTime loginLocalTime, logoutLocalTime;
    private Calendar calendar;
    private java.sql.Date loginDate, logoutDate;
    private java.sql.Time loginTime, logoutTime;
    private ArrayList<AdminDisplay.User> userList;
    public static int totalSearches = 0, totalCopies = 0;
    public static int COPY_LIMIT = 5000;
    
    
    @Override
    public void start(Stage primaryStage) {
        establishDatabaseConnection();
        
        mainBorderDisplay = new MainBorderDisplay();
        menuBarDisplay = new MenuBarDisplay();
            setMenuBarActions();
        selectionDisplay = new SelectionDisplay();
            setSelectionDisplayActions();
        dbContextMenu = new DatabaseContextMenu();
            establishDatabaseDisplayContextMenu();
        databaseDisplay = new DatabaseDisplay(dbContextMenu);
        songCopier = new SongCopier(selectionDisplay);
        mainBorderDisplay.getPane().setCenter(loginDisplay.getPane());
        adminDisplay = new AdminDisplay();
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        FULLSCREEN_WIDTH = bounds.getWidth();
        FULLSCREEN_HEIGHT = bounds.getHeight();
        setDimensionsAndContent();
        
        scene = new Scene(new ScrollPane(mainBorderDisplay.getPane()));
        primaryStage.setTitle("GotRadio, LLC. Database, Version " + VERSION_NUMBER);
        primaryStage.getIcons().add(APPLICATION_ICON); 
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::exitProgram); 
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    private void getUserList(){
        userList = new ArrayList<>();
        checkDatabaseConnection();
        String search = "SELECT first_name, last_name, email_address, username, administrator FROM gotradio_db.programmers;";
        
        ResultSet result;

        try {
            result = databaseConnect.getSQLConnection().prepareStatement(search).executeQuery();

            while(result.next()){
                String first = result.getString("first_name");
                String last = result.getString("last_name");
                String email = result.getString("email_address");
                String username = result.getString("username");
                int admin = result.getInt("administrator");
                userList.add(adminDisplay.convertUser(first, last, email, username, admin));
            }
        } catch (Exception e){};
    }
    
    private void setDimensionsAndContent(){
        mainBorderDisplay.getPane().setMinSize(FULLSCREEN_WIDTH*.999, FULLSCREEN_HEIGHT*.95);
        menuBarDisplay.getMenuBar().setMaxWidth(FULLSCREEN_WIDTH);
        selectionDisplay.getPane().setMaxHeight(FULLSCREEN_HEIGHT);
        databaseDisplay.getPane().setMaxSize
                (
                mainBorderDisplay.getPane().getMinWidth() - selectionDisplay.getPane().getWidth(),
                mainBorderDisplay.getPane().getMinHeight()        
                );
        songCopier.getPane().setMaxSize
                (
                (mainBorderDisplay.getPane().getMinWidth() - selectionDisplay.getPane().getWidth())*.9,
                (mainBorderDisplay.getPane().getMinHeight())*.9    
                );
            songCopier.setContent();
            setCopyDisplayButtonActions();
        adminDisplay.getMainBorderPane().setMaxSize
                (FULLSCREEN_WIDTH*.999, FULLSCREEN_HEIGHT*.95);
            
            adminDisplay.setContent();
            setAdminDisplayActions();
        
    }

    private void establishDatabaseConnection(){
        loginDisplay = new LoginDisplay();
        loginDisplay.setButtonAction(loginDisplay.getSubmitButton(), e ->{
            //Login to Database
            databaseConnect = new DatabaseConnect( 
                loginDisplay.getUserTextField(), 
                loginDisplay.getPasswordField(), 
                loginDisplay.getStatusLabel());
            
            
            
            Task<Boolean> connectionTask = new Task<Boolean>(){
            
            @Override
            protected Boolean call() throws Exception {
                Platform.runLater(()->{
                    loginDisplay.showPI();
                    loginDisplay.getStatusLabel().setText("Attempting Connection..");
                    loginDisplay.getStatusLabel().setTextFill(Color.BLACK);
                });
                return databaseConnect.databaseConnect();
                    
            }
            };

            connectionTask.setOnSucceeded(f -> {
                if (connectionTask.getValue()){
                    Platform.runLater(()->{
                        loginDisplay.getStatusLabel().setText("Success, loading DB.");
                        loginDisplay.getStatusLabel().setTextFill(Color.GREEN);
                        calendar = Calendar.getInstance();
                        loginDate = new java.sql.Date(calendar.getTime().getTime());
                        loginTime = new java.sql.Time(calendar.getTime().getTime());
                        loginLocalTime = LocalTime.now();
                        databaseConnect.setLoggedIn(true);
                        setMainView();
                    }); 
                } else {
                    Platform.runLater(()->{
                    loginDisplay.getStatusLabel().setText("Invalid Login.");
                    loginDisplay.getStatusLabel().setTextFill(Color.RED);
                    loginDisplay.hidePI();
            }); 
                }
            });
            
            Platform.runLater(()->{
                loginDisplay.getStatusLabel().setText("Invalid Login.");
                loginDisplay.getStatusLabel().setTextFill(Color.RED);
                loginDisplay.hidePI();
            }); 

            Thread th = new Thread(connectionTask);
            th.start();
        });
        loginDisplay.setButtonAction(loginDisplay.getCancelButton(), this::exitProgram);
    }
    
    private void setMainView(){
        mainBorderDisplay.getPane().setTop(menuBarDisplay.getMenuBar());
        mainBorderDisplay.getPane().setLeft(selectionDisplay.getPane());
        mainBorderDisplay.getPane().setCenter(databaseDisplay.getPane());
        selectionDisplay.setUserLabel(loginDisplay.getUserTextField());
        resetTempVariables();
        getUserList();
        adminDisplay.setUserList(userList);
        adminDisplay.refreshCenterPane();
        loadDatabase();
    }
    
    //Initial Database Loader
    private void loadDatabase(){
        checkDatabaseConnection();
        databaseTableLoader = new DatabaseTableLoader(databaseDisplay, databaseConnect.getSQLConnection(), selectionDisplay);
        databaseTableLoader.generateData(false);
        
        setDatabaseViewObjectActions();
    }
    
    //Subsequent Database Loader
    private void loadDatabaseRequest(boolean quick){
        checkDatabaseConnection();
        
        if(quick){
            databaseTableLoader.generateData(true);
        } else {
            databaseTableLoader.generateData(false);
        }
    }
    
    private void setDatabaseViewObjectActions(){
        //Refresh Button Action
        databaseDisplay.getRefreshButton().setOnAction(e->{
            loadDatabaseRequest(true);
        });
        //Quick Search Field Action
        databaseDisplay.getQuickSearchField().textProperty().addListener((observable, oldValue, newValue) -> {
            loadDatabaseRequest(true);
        });
        
        //Quick Clear Button Action
        databaseDisplay.getQuickClearButton().setOnAction(e->{
            databaseDisplay.getQuickSearchField().setText("");
        });
        
        //Advanced Clear Button Action
        databaseDisplay.getAdvancedClearButton().setOnAction(e->{
            databaseDisplay.getAdvancedSearchField().setText("");
        });
        
        //Reset Filter Button
        databaseDisplay.getClearFiltersButton().setOnAction(e->{
            databaseDisplay.clearFilters();
        });
        
        //Submit Advanced Filter Button
        databaseDisplay.getAdvancedSearchButton().setOnAction(e->{
            loadDatabaseRequest(false);
        });
        
        //Hide Advanced Pane Button
        databaseDisplay.getHideAdvancedPaneButton().setOnAction(e->{
            databaseDisplay.hideAdvancedSearchPane();
        });
        
        databaseDisplay.setCopyListButtonAction(e->{ 
            mainBorderDisplay.getPane().setCenter(songCopier.getPane());
        });
        
        databaseDisplay.setCopyListLabel(songCopier.getCopyListCount() + " songs queued to copy.");
        
        songCopier.setClosePaneButtonAction(e->{
            mainBorderDisplay.getPane().setCenter(databaseDisplay.getPane());
        }); 
        
    }
    
    private void setCopyDisplayButtonActions(){
        songCopier.getButton(SongCopier.ButtonSelection.START).setOnAction(e->{
            checkDatabaseConnection();
            
            if(songCopier.getCopyListCount()!=0){
                System.out.println("Starting Copy Process...");
                songCopier.setCopierLabelStatus(SongCopier.CopierLabelEnum.Copying);
                songCopier.getButton(SongCopier.ButtonSelection.START).setDisable(true);
                songCopier.getButton(SongCopier.ButtonSelection.CANCEL).setDisable(false);
                songCopier.getButton(SongCopier.ButtonSelection.CLEAR).setDisable(true);
                songCopier.setCopyingStatus(true);

                songCopier.startCopy(databaseConnect);
            } else {
                System.out.println("Copy List Empty, please add songs to the list.");
            }
            
            
        });
        
        songCopier.getButton(SongCopier.ButtonSelection.CANCEL).setOnAction(e->{
            System.out.println("Canceling Copy Process...");
            songCopier.setCopierLabelStatus(SongCopier.CopierLabelEnum.Canceled);
            songCopier.getButton(SongCopier.ButtonSelection.CLEAR).setDisable(false);
            songCopier.setCopyingStatus(false);
            songCopier.getButton(SongCopier.ButtonSelection.START).setDisable(false);
        });
        
        
        songCopier.getButton(SongCopier.ButtonSelection.CLEAR).setOnAction(e->{
            songCopier.clearLeftPaneContent();
            databaseDisplay.setCopyListLabel(songCopier.getCopyListCount() + " songs queued to copy.");
            songCopier.setCopierLabelStatus(SongCopier.CopierLabelEnum.Pending);
            songCopier.getButton(SongCopier.ButtonSelection.START).setDisable(true);
        });
    }
    
    private void setMenuBarActions(){
        //File Menu Items
        menuBarDisplay.setMenuItemAction(MenuBarDisplay.SELECTION_ITEM.Exit, e->{
            System.out.println("Exiting program...");
           exitProgram(e); 
        });
        
        menuBarDisplay.setMenuItemAction(MenuBarDisplay.SELECTION_ITEM.Logout, e->{
            System.out.println("Logging out...");
           logout(); 
        });
        
        //Database Menu Items
        menuBarDisplay.setMenuItemAction(MenuBarDisplay.SELECTION_ITEM.Admin, e ->{
            for(AdminDisplay.User u : adminDisplay.getUserList()){
                if(u.getUsername().equals(databaseConnect.getUser())){
                    if(u.getAdmin()!=0){
                        //Clear mainBorderDisplay
                        clearMainBorderDisplay(); 
                        mainBorderDisplay.getPane().setTop(menuBarDisplay.getMenuBar());
                        mainBorderDisplay.getPane().setCenter(adminDisplay.getPane());
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Access Restricted.");
                        alert.setGraphic(null);
                        alert.setHeaderText(null);
                        alert.setContentText("Access Denied.\n\nYou do not have Administrative Access.");
                        alert.show();
                    }
                }
            }
            
        });
        
        menuBarDisplay.setMenuItemAction(MenuBarDisplay.SELECTION_ITEM.Database, e->{
            clearMainBorderDisplay();
            mainBorderDisplay.getPane().setTop(menuBarDisplay.getMenuBar());
            mainBorderDisplay.getPane().setLeft(selectionDisplay.getPane());
            mainBorderDisplay.getPane().setCenter(databaseDisplay.getPane());
        });
    }
    
    private void clearMainBorderDisplay(){
        mainBorderDisplay.getPane().setTop(null);
        mainBorderDisplay.getPane().setRight(null);
        mainBorderDisplay.getPane().setBottom(null);
        mainBorderDisplay.getPane().setLeft(null);
        mainBorderDisplay.getPane().setCenter(null);
    }
    
    private void setSelectionDisplayActions(){
        selectionDisplay.setCopyCount(totalCopies);
        
        selectionDisplay.getTab(SelectionDisplay.TAB_ENUM.DATABASE).setOnSelectionChanged(e->{
            if(selectionDisplay.getTab(SelectionDisplay.TAB_ENUM.DATABASE).isSelected()){
                mainBorderDisplay.getPane().setCenter(databaseDisplay.getPane());
            }
        });
        
        selectionDisplay.getTab(SelectionDisplay.TAB_ENUM.CHART).setOnSelectionChanged(e->{
            if(selectionDisplay.getTab(SelectionDisplay.TAB_ENUM.CHART).isSelected()){
                mainBorderDisplay.getPane().setCenter(new Pane());
            }
        });
        
        selectionDisplay.getTab(SelectionDisplay.TAB_ENUM.BUCKET).setOnSelectionChanged(e->{
            if(selectionDisplay.getTab(SelectionDisplay.TAB_ENUM.BUCKET).isSelected()){
                mainBorderDisplay.getPane().setCenter(new Pane());
            }
        });
    }
    
    private void setAdminDisplayActions(){
        adminDisplay.getCloseAdminButton().setOnAction(e->{
            clearMainBorderDisplay();
            mainBorderDisplay.getPane().setTop(menuBarDisplay.getMenuBar());
            mainBorderDisplay.getPane().setLeft(selectionDisplay.getPane());
            mainBorderDisplay.getPane().setCenter(databaseDisplay.getPane());
        });
        
        adminDisplay.getCreateUserButton().setOnAction(e->{
            adminDisplay.showCreateUserPane();
        });
        
        adminDisplay.getCloseCreateUserPaneButton().setOnAction(e->{
            adminDisplay.hideCreateUserPane();
        });
        
        adminDisplay.getClearFieldsButton().setOnAction(e->{
           adminDisplay.clearCreateUserFields();
           adminDisplay.setCreateUserStatusLabel("");
        });
        
        adminDisplay.getCreateNewUserButton().setOnAction(e->{
           AdminDisplay.User newUser = adminDisplay.createUser();
           
           if(newUser != null){
                //Add User to DB
                PreparedStatement preparedStmt;
                try {
                    checkDatabaseConnection();

                    String query = "insert into gotradio_db.programmers (first_name, last_name, email_address, username, administrator)"
                        + " values (?, ?, ?, ?, ?)";
                    preparedStmt = databaseConnect.getSQLConnection().prepareStatement(query);
                    preparedStmt.setString(1, newUser.getFirstName());
                    preparedStmt.setString(2, newUser.getLastName());
                    preparedStmt.setString(3, newUser.getEmail());
                    preparedStmt.setString(4, newUser.getUsername());
                    preparedStmt.setInt(5, newUser.getAdmin());
                    preparedStmt.execute();


                    query = "create user '" + newUser.getUsername() + "'@'%' identified by '" + newUser.getPassword() + "';";

                    
                    preparedStmt = databaseConnect.getSQLConnection().prepareStatement(query);
                    preparedStmt.execute();
                    
                    if(newUser.getAdmin()==0){
                        query = "grant select, insert on gotradio_db.* to '" + newUser.getUsername() + "'@'%';"; 
                    } else {
                        query = "grant all privileges on gotradio_db.* to '" + newUser.getUsername() + "'@'%';"; 
                    }
                    preparedStmt = databaseConnect.getSQLConnection().prepareStatement(query);
                    preparedStmt.executeQuery();
                    
                    
                    //Add User to List
                    adminDisplay.addUserToList(newUser);

                    //Refresh adminDisplay Center Pane to reflect new user addition
                    adminDisplay.refreshCenterPane();
                    adminDisplay.hideCreateUserPane();
                    adminDisplay.showCreateUserPane();
                    //Clear Fields
                    adminDisplay.clearCreateUserFields();
                    System.out.println("GRDB: Creation successful, adding User to Database.");
                    System.out.println(newUser.toString());
                    
                    try{
                        preparedStmt = GRDB.databaseConnect.getSQLConnection().prepareStatement("flush privileges;");
                        preparedStmt.execute();
                    } catch (SQLException sql){System.out.println(sql.getMessage());}
                    
                } catch (Exception f){
                    System.out.println(f.getMessage());
                    System.out.println("GRDB: Creation failed, please try again.");
                }
               
               
           } else {
               System.out.println("GRDB: Creation failed, please try again.");
           }
           
        });
    }
    
    private void establishDatabaseDisplayContextMenu(){
        Menu viewMenu = new Menu("Select View");
            MenuItem dbView = new MenuItem("Database");
                dbView.setOnAction(e->{
                    mainBorderDisplay.getPane().setCenter(databaseDisplay.getPane());
                });
            MenuItem copyListView = new MenuItem("Copy List");
                copyListView.setOnAction(e->{
                    mainBorderDisplay.getPane().setCenter(songCopier.getPane());
                });
            MenuItem chartView = new MenuItem("Chart Handler");
            MenuItem bucketView = new MenuItem("Bucket Handler");
        viewMenu.getItems().addAll(dbView, copyListView, chartView, bucketView);
        
        dbContextMenu.addMenu(viewMenu);
        
        dbContextMenu.addSeparatorMenuItem(new SeparatorMenuItem());
        
        MenuItem addSelected = new MenuItem("Add Selected Songs");
        
            addSelected.setOnAction(e->{
               if(!databaseDisplay.getColsSelected().contains("file_name")){
                   Alert alert = new Alert(Alert.AlertType.INFORMATION);
                   alert.setTitle("Copy Song Error");
                   alert.setHeaderText(null);
                   alert.setGraphic(null);
                   alert.setContentText("Please enable the following Database Column in Advanced Search:\n\n"
                           + "\tFilename");
                   alert.show();
                   
               } else {
                   if(!songCopier.getCopyingStatus()){
                       if((songCopier.getCopyListCount()+databaseDisplay.getTableView().getSelectionModel().getSelectedItems().size()) < COPY_LIMIT ){
                            selectionDisplay.deselect();
                            songCopier.setCopierLabelStatus(SongCopier.CopierLabelEnum.Queued);
                            songCopier.getButton(SongCopier.ButtonSelection.START).setDisable(false);
                            //Proceed with Adding Song
                            int colIndex = databaseTableLoader.getColumn("file_name"); //Finds columnIndex for Filename
                            TableView table = databaseDisplay.getTableView();
                            TableColumn col = (TableColumn) table.getColumns().get(colIndex);
                            ObservableList items = table.getSelectionModel().getSelectedItems();
                            for(Object i : items){
                                String fileName = col.getCellObservableValue(i).getValue().toString();
                                songCopier.addSongBox(fileName);
                                databaseDisplay.setCopyListLabel(songCopier.getCopyListCount() + " songs queued to copy.");
                            } 
                       } else {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.getDialogPane().setMinSize(600, 150);
                            alert.setTitle("Song Queue Limit Reached");
                            alert.setHeaderText(null);
                            alert.setGraphic(null);
                            alert.setContentText("Song Queue Count cannot exceed " + COPY_LIMIT + ".");
                            alert.show();
                       }
                        
                   } else {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.getDialogPane().setMinSize(600, 150);
                        alert.setTitle("Copy Song Error");
                        alert.setHeaderText(null);
                        alert.setGraphic(null);
                        alert.setContentText("Copying currently in progress. "
                                + "If you wish to cancel, navigate to the "
                                + "'Copy List Display' and press the 'Cancel' Button. Otherwise, "
                                + "please wait for process to end before adding new songs.");
                        alert.show();
                   }
               }
            });
        
        dbContextMenu.addMenuItem(addSelected);
    }
    
    public static void checkDatabaseConnection(){
        if(!databaseConnect.isConnected()){
            databaseConnect.databaseConnect();
        }
    }
    
    private void resetTempVariables(){
        totalSearches = 0;
        totalCopies = 0;
        selectionDisplay.setCopyCount(totalCopies);
        selectionDisplay.setSearchCount(totalSearches);
    }
    
    private void getTimes(){
        if(databaseConnect.getLoggedIn()){
            calendar = Calendar.getInstance();
            logoutDate = new java.sql.Date(calendar.getTime().getTime());
            logoutTime = new java.sql.Time(calendar.getTime().getTime());
            logoutLocalTime = LocalTime.now();

            Duration duration = Duration.between(loginLocalTime, logoutLocalTime);

            java.sql.Time sqlDurationTime = java.sql.Time.valueOf(duration.toHours() + ":" + duration.toMinutes() + ":" + duration.getSeconds());

            PreparedStatement preparedStmt;
            try {
                checkDatabaseConnection();
                
                String query = "insert into gotradio_db.login_history (username, duration, login_date, login_time, logout_date, logout_time, searches, copies)"
                    + " values (?, ?, ?, ?, ?, ?, ?, ?)";
                preparedStmt = databaseConnect.getSQLConnection().prepareStatement(query);
                preparedStmt.setString(1, databaseConnect.getUser());
                preparedStmt.setTime(2, sqlDurationTime);
                preparedStmt.setDate(3, loginDate);
                preparedStmt.setTime(4, loginTime);
                preparedStmt.setDate(5, logoutDate);
                preparedStmt.setTime(6, logoutTime);
                preparedStmt.setInt(7, totalSearches);
                preparedStmt.setInt(8, totalCopies);
                preparedStmt.execute();
            } catch (Exception e){
                System.out.println(e.getMessage());
            }
            
        }
    }
    
    private void logout(){
        getTimes();
        databaseConnect.setLoggedIn(false);
        //Add login/logout times to db
        clearMainBorderDisplay();
        
        establishDatabaseConnection();
        mainBorderDisplay.getPane().setCenter(loginDisplay.getPane());
        
        
        
        menuBarDisplay = new MenuBarDisplay();
            setMenuBarActions();
        selectionDisplay = new SelectionDisplay();
            setSelectionDisplayActions();
        dbContextMenu = new DatabaseContextMenu();
            establishDatabaseDisplayContextMenu();
        databaseDisplay = new DatabaseDisplay(dbContextMenu);
        songCopier = new SongCopier(selectionDisplay);
        
        adminDisplay = new AdminDisplay();
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        FULLSCREEN_WIDTH = bounds.getWidth();
        FULLSCREEN_HEIGHT = bounds.getHeight();
        setDimensionsAndContent();
        
    }
    
    private void exitProgram(WindowEvent event){
        exitProgramHelper(event);
    }
    
    private void exitProgram(ActionEvent event){
        exitProgramHelper(event);
    }
    
    private void exitProgramHelper(Event event){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.getDialogPane().setMinSize(400, 200);
            alert.setTitle("Database Exit Confirmation");
            alert.setContentText("Would you like to close the Application?");

            alert.getButtonTypes().remove(ButtonType.OK);
            alert.getButtonTypes().add(ButtonType.CLOSE);
            Optional<ButtonType> result = alert.showAndWait();
            
            if(result.get().equals(ButtonType.CLOSE)){
                try {
                    getTimes();
                    
                    //Add login/logout times to db
                    databaseConnect.databaseDisconnect();
                    databaseConnect.setLoggedIn(false);
                } catch (NullPointerException e){//No Connection Attempt
                }

                Platform.exit();
                System.exit(0);
            } else {
                System.out.println("Resuming operation.");
                event.consume();
            }  
    }
}
