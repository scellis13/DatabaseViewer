/*
    Last Updated: 12/21/2019
    Updated On: Sean's Computer
*/
package grdb;

import javafx.event.*;
import javafx.scene.control.*;

public class DatabaseContextMenu {
    private ContextMenu contextMenu;
    
    public DatabaseContextMenu(){
        initializeMenu();
    }
    
    private void initializeMenu(){
        contextMenu = new ContextMenu();
        
    }
    
    public ContextMenu getContextMenu(){
        return this.contextMenu;
    }
    
    public void addMenu(Menu item){
        contextMenu.getItems().add(item);
    }
    
    public void addMenuItem(MenuItem item){
        contextMenu.getItems().add(item);
    }
    
    public void addSeparatorMenuItem(SeparatorMenuItem item){
        contextMenu.getItems().add(item);
    }
    
    public void addRadioButtonMenuItem(RadioMenuItem item){
        contextMenu.getItems().add(item);
    }
    
    public void setAction(EventHandler<ActionEvent> event){
        contextMenu.setOnAction(event);
    }
}
