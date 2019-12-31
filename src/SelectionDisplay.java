/*
    Last Updated: 12/21/2019
    Updated On: Sean's Computer
*/
package grdb;


import java.time.*;
import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.util.Duration;

/**
 *
 * @author ELLIS
 */
public class SelectionDisplay {
    private VBox selectionPane;
    private TabPane tabPane;
    private Tab databaseTab, chartTab, bucketTab;
    private Label userLabel, userLabelValue, totalSearchesLabel, totalSearchesLabelCount, totalCopiesLabelCount, totalCopiesLabel;
    private int tabPaneHeight, tabPaneWidth;
    private final Font TAB_FONT = new Font("Times New Roman", 16);
    private LocalTime currentTime;
    private LocalTime loginTime, logoutTime;
    
    public SelectionDisplay(){
        initializePane();
    }
    
    private void initializePane(){
        selectionPane = new VBox();
        selectionPane.setSpacing(25);
        selectionPane.setStyle("-fx-background-color: gainsboro;");

        //Initiate TabPane
        tabPane = new TabPane();
                Label databaseLabel = new Label("Database");
                databaseLabel.setFont(TAB_FONT);
                databaseLabel.setAlignment(Pos.CENTER_LEFT);

                Label chartLabel = new Label("Chart Handler");
                chartLabel.setFont(TAB_FONT);
                chartLabel.setAlignment(Pos.CENTER_LEFT);

                Label bucketLabel = new Label("Bucket Handler");
                bucketLabel.setFont(TAB_FONT);
                bucketLabel.setAlignment(Pos.CENTER_LEFT);
                
                databaseTab = new Tab();
                databaseTab.setGraphic(databaseLabel);
                
                chartTab = new Tab();
                chartTab.setGraphic(chartLabel);
                
                bucketTab = new Tab();
                bucketTab.setGraphic(bucketLabel);
                
            tabPane.getTabs().addAll(databaseTab, chartTab, bucketTab);
            tabPane.setSide(Side.LEFT);
            tabPane.rotateGraphicProperty();
            tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
            
            tabPaneHeight = 150;
            tabPaneWidth = 100;
            
            tabPane.setTabMinHeight(tabPaneHeight);
            tabPane.setTabMinWidth(tabPaneWidth);
            
            VBox infoBox = new VBox();
            infoBox.setSpacing(10);
            infoBox.setAlignment(Pos.CENTER);
            infoBox.setPadding(new Insets(0,0,0,10));
                HBox userIndicator = new HBox();
                //userIndicator.setMinWidth(200);
                //userIndicator.setMaxWidth(200);
                userIndicator.setAlignment(Pos.CENTER_LEFT);
                    userLabel = new Label("User: ");
                    userLabel.setFont(Font.font("Times New Roman", FontWeight.BOLD, 16));
                    userLabelValue = new Label();
                userIndicator.getChildren().addAll(userLabel, userLabelValue);
                
                HBox searchCountBox = new HBox();
                //searchCountBox.setMinWidth(200);
                //searchCountBox.setMaxWidth(200);
                searchCountBox.setAlignment(Pos.CENTER_LEFT);
                    totalSearchesLabel = new Label("Session Searches: ");
                    totalSearchesLabel.setFont(Font.font("Times New Roman", FontWeight.BOLD, 16));
                    totalSearchesLabelCount = new Label();
                searchCountBox.getChildren().addAll(totalSearchesLabel, totalSearchesLabelCount);
                
                HBox copiesCountBox = new HBox();
                //copiesCountBox.setMinWidth(200);
                //copiesCountBox.setMaxWidth(200);
                copiesCountBox.setAlignment(Pos.CENTER_LEFT);
                    totalCopiesLabel = new Label("Session Copies: ");
                    totalCopiesLabel.setFont(Font.font("Times New Roman", FontWeight.BOLD, 16));
                    totalCopiesLabelCount = new Label();
                copiesCountBox.getChildren().addAll(totalCopiesLabel, totalCopiesLabelCount);
                
            infoBox.getChildren().addAll(userIndicator, searchCountBox, copiesCountBox);

            selectionPane.getChildren().addAll(tabPane, infoBox);
    }
    
    public void setUserLabel(String s){
        userLabelValue.setText(s);
    }
    
    public void setSearchCount(int count){
        totalSearchesLabelCount.setText("" + count);
    }
    
    public void setCopyCount(int count){
        totalCopiesLabelCount.setText("" + count);
    }
    
    public Tab getTab(SelectionDisplay.TAB_ENUM e){
        switch (e) {
            case DATABASE:
                return databaseTab;
            case CHART:
                return chartTab;
            default:
                return bucketTab;
        }
    }
    
    public Pane getPane(){
        return this.selectionPane;
    }
    
    public void deselect(){
        tabPane.getSelectionModel().clearSelection();
    }
    
    public void setLoginTime(LocalTime time){
        this.loginTime = time;
    }
    
    public void setLogoutTime(LocalTime time){
        this.logoutTime = time;
    }
    
    public LocalTime getLoginTime(){
        return this.loginTime;
    }
    
    public LocalTime getLogoutTime(){
        return this.logoutTime;
    }
    
    public enum TAB_ENUM{
        DATABASE, CHART, BUCKET;
    }
}
