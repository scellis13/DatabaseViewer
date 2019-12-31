/*
    Last Updated: 12/21/2019
    Updated On: Sean's Computer
*/
package grdb;

import com.jcraft.jsch.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class SSH_SQL_Test {

    private String remoteHost, user, pw;
    private int remotePort, forwardedPort;
    private JSch jsch;
    private Session session;
    private Connection sqlConn;
    
    public SSH_SQL_Test (String host, int port, String user, String pw) {
        this.remoteHost = host;
        this.remotePort = port;
        this.user = user;
        this.pw = pw;
    }
    
    public boolean remoteConnect(){
        try {
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            jsch = new JSch();
            session = jsch.getSession(
                    this.user, 
                    this.remoteHost, 
                    this.remotePort);
            session.setPassword(this.pw);
            session.setConfig(config);
            session.connect();
            
            return true;
            
        } catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }
    
    public Session getSession(){
        return this.session;
    }
    
    private void remoteDisconnect(){
        try {
            session.disconnect();
            System.out.println("SSH Connetion Closed.");
        } catch (Exception e){
            System.out.println("Failed to Disconnect: " + e.getMessage());
        }
    }
    
    public boolean forwardPort(int port){
        assert checkConnections();
        try{
            this.forwardedPort = session.setPortForwardingL(remotePort, remoteHost, port);
            return true;
        } catch (Exception e){
            System.out.println("Port Forwarding Failed: " + e.getMessage());
            return false;
        }
    }
    
    //MySQL Connetion Methods
    public boolean databaseConnect(String user, String pw){
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            sqlConn = DriverManager.getConnection("jdbc:mysql://localhost:"+remotePort+"/", user, pw);
            System.out.println("Database Successfully Connected.");
            return true;
        } catch (Exception e){
            System.out.println("Database Connection Failed: " + e.getMessage());
            return false;
        }
    }
    
    public void submitQuery(String query){
        assert checkConnections();
        
        try {
            PreparedStatement ps = sqlConn.prepareStatement(query);
            ResultSet resultSet = ps.executeQuery();
            
            ResultSetMetaData data = resultSet.getMetaData();
            int cols = data.getColumnCount();
            
                
            while(resultSet.next()){
               for(int i = 0; i < cols; i++){
                   System.out.println(resultSet.getString(i+1));
               }
           }
            
        } catch (Exception e){
            System.out.println("Query Failed: " + e.getMessage());
        }
    }
    
    private void databaseDisconnect(){
        assert checkConnections();
        
        try {
            sqlConn.close();
            System.out.println("Database Connetion Closed.");
        } catch (Exception e){
            System.out.println("Failed to Close Database: " + e.getMessage());
        }
    }
    
    public boolean checkConnections(){
        try{
            return sqlConn.isValid(forwardedPort) && session.isConnected();
        } catch (Exception e){
            System.out.println("Failed to Check Database Connection: " + e.getMessage());
            return false;
        }
    }
    
    public Connection getDatabaseConnection(){
        return this.sqlConn;
    }
    
    public void disconnect(){
        /*
        try {
            session.delPortForwardingL(remoteHost, forwardedPort);
        } catch (Exception e){
            System.out.println("SSH_SQL_TEST: " + e.getMessage());
        }
        */
        databaseDisconnect();
        remoteDisconnect();
    }
    
    public static void main(String[] args) {
        /*
        String host = "";
        int port = ;
        String user = "";
        String pw = "";
        SSH_SQL_Test ssh = new SSH_SQL_Test(host, port, user, pw);
        System.out.println("Connected: " + ssh.remoteConnect());
        System.out.println("Port Forwarded? " + ssh.forwardPort(3306));
        System.out.println("Database Connected: " + ssh.databaseConnect("", ""));
        
        ssh.submitQuery("SELECT COUNT(*) FROM gotradio_db.songs;");
        
        System.out.println("Disconnecting from Database..");
        ssh.databaseDisconnect();
        System.out.println("Disconnecting from SSH..");
        ssh.remoteDisconnect();
        */
    }
    
}
