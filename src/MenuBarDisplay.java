/*
    Last Updated: 12/21/2019
    Updated On: Sean's Computer
*/
package grdb;

import javafx.event.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class MenuBarDisplay {
        private MenuBar menu;
        private Menu fileMenu, databaseMenu, helpMenu;
        private MenuItem adminMenuItem, databaseMenuItem, logoutMenuItem, exitMenuItem;
    
    public MenuBarDisplay(){
        intializeMenuBar();
    }
    
    private void intializeMenuBar(){
        menu = new MenuBar();
        fileMenu = new Menu("File");
            logoutMenuItem = new MenuItem("Logout");
            exitMenuItem = new MenuItem("Exit");
        fileMenu.getItems().addAll(logoutMenuItem, exitMenuItem);
        databaseMenu = new Menu("Database");
            adminMenuItem = new MenuItem("Admin Display");
            databaseMenuItem = new MenuItem("Database Display");
        databaseMenu.getItems().addAll(databaseMenuItem, adminMenuItem);
        helpMenu = new Menu("Help");

        menu.getMenus().addAll(fileMenu, databaseMenu, helpMenu);
        
    }
    
    public void setMenuAction(MenuBarDisplay.SELECTION selection, EventHandler<ActionEvent> event){
            switch (selection) {
                case File:
                    fileMenu.setOnAction(event);
                    break;
                case Database:
                    databaseMenu.setOnAction(event);
                    break;
                default:
                    helpMenu.setOnAction(event);
                    break;
            }
    }
    
    public void setMenuItemAction(MenuBarDisplay.SELECTION_ITEM item, EventHandler<ActionEvent> event){
            switch (item) {
                case Admin:
                    adminMenuItem.setOnAction(event);
                    break;
                case Database:
                    databaseMenuItem.setOnAction(event);
                    break;
                case Logout:
                    logoutMenuItem.setOnAction(event);
                    break;
                case Exit:
                    exitMenuItem.setOnAction(event);
                    break;
            }
    }
    
    public MenuBar getMenuBar(){
        return this.menu;
    }
    
    public enum SELECTION {
        File, Database, Help;
    }
    
    public enum SELECTION_ITEM {
        Admin, Database, Logout, Exit;
    }
}
