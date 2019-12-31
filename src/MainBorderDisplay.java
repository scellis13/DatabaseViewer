/*
    Last Updated: 12/21/2019
    Updated On: Sean's Computer
*/
package grdb;

import javafx.scene.layout.*;

public class MainBorderDisplay {
    private BorderPane pane;
    
    public MainBorderDisplay(){
        initializePane();
    }
    
    private void initializePane(){
        pane = new BorderPane();
        //pane.setStyle("-fx-border-color: black;");
        
    }
    
    public BorderPane getPane(){
        return this.pane;
    }

}
