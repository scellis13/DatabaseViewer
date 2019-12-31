/*
    Last Updated: 12/21/2019
    Updated On: Sean's Computer
*/
package grdb;

import com.jcraft.jsch.Session;
import java.sql.Connection;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class DatabaseConnect {
    private boolean connection;
    private String user, password;
    private SSH_SQL_Test ssh;
    private Label statusLabel; 
    private Connection myConn;
    private boolean loggedIn;
    
    public DatabaseConnect(String user, String password, Label statusLabel){
        this.statusLabel = statusLabel;
        this.user = user;
        this.password = password;
    }
    
    public synchronized boolean databaseConnect(){
    connection = false;
    try {
        String host = "";
        int port = ;
        String user = "";
        String pw = "";
        ssh = new SSH_SQL_Test(host, port, user, pw);

        if(ssh.remoteConnect()){
            Platform.runLater(()->{
                statusLabel.setText("Server Connection Established..");
                statusLabel.setTextFill(Color.GREEN);
            });
            System.out.println("Port Forwarded? " + ssh.forwardPort(3306));

            connection = ssh.databaseConnect(this.user, this.password);
            if(connection){
                Platform.runLater(()->{
                    statusLabel.setText("Successful DB Login, loading data.");
                    statusLabel.setTextFill(Color.GREEN);
                });
                myConn = ssh.getDatabaseConnection();
            }
        }
    } catch (Exception e){
        connection = false;  
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("MySQL Localhost Connection Failed");
        alert.setContentText(e.getMessage());
        alert.showAndWait();
        //e.printStackTrace();
    }

    return connection;
    }
    
    public Connection getSQLConnection(){
        return ssh.getDatabaseConnection();
    }
    
    public void databaseDisconnect(){
        ssh.disconnect();
    }
    
    public boolean isConnected(){
        return ssh.checkConnections();
    }
    
    public Session getSession(){
        return ssh.getSession();
    }
    
    public String getUser(){
        return this.user;
    }
    
    public void setLoggedIn(boolean in){
        this.loggedIn = in;
    }
    
    public boolean getLoggedIn(){
        return this.loggedIn;
    }
}
